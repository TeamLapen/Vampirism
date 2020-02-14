package de.teamlapen.vampirism.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IAggressiveVillager;
import de.teamlapen.vampirism.api.entity.ICaptureIgnore;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.AggressiveVillagerEntity;
import de.teamlapen.vampirism.entity.hunter.DummyHunterTrainerEntity;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.hunter.HunterTrainerEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.potion.PotionSanguinareEffect;
import de.teamlapen.vampirism.util.ModEventFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.BossInfo;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.structure.Structures;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class TotemTileEntity extends TileEntity implements ITickableTileEntity {//TODO 1.14 add village events
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RNG = new Random();
    private static final ResourceLocation nonFactionTotem = new ResourceLocation("none");
    /**
     * stores all BoundingBoxes of vampire controlled villages per dimension, mapped from totem positions
     */
    private static final HashMap<Dimension, Map<BlockPos, MutableBoundingBox>> vampireVillages = Maps.newHashMap();
    /**
     * saves the position
     */
    private static final Map<StructureStart, BlockPos> totemPositions = Maps.newHashMap();
    /**
     * weighted entitylist for capture entity spawn based on Faction
     * setup once
     */
    private static final Map<IFaction, List<CaptureEntityEntry>> captureEntities;

    public static boolean isInsideVampireAreaCached(Dimension dimension, BlockPos blockPos) {
        if (vampireVillages.containsKey(dimension)) {
            for (Map.Entry<BlockPos, MutableBoundingBox> entry : vampireVillages.get(dimension).entrySet()) {
                if (entry.getValue().isVecInside(blockPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void clearCacheForDimension(Dimension dimension) {
        vampireVillages.remove(dimension);
    }

    private static void addVampireVillage(Dimension dimension, BlockPos pos, MutableBoundingBox box) {
        vampireVillages.computeIfAbsent(dimension, dimension1 -> Maps.newHashMap()).put(pos, box);
    }

    private static void removeVampireVillage(Dimension dimension, BlockPos pos) {
        vampireVillages.computeIfPresent(dimension, (dimension1, structureStarts) -> {
            structureStarts.remove(pos);
            return structureStarts;
        });
    }

    /**
     * @return false if another totem exists
     */
    private static boolean addTotem(StructureStart structure, BlockPos totemPos) {
        return totemPositions.computeIfAbsent(structure, (start -> totemPos)).equals(totemPos);
    }

    private static void removeTotem(StructureStart structure) {
        totemPositions.remove(structure);
    }
    /**
     * @return {@code null} if no totem exists
     */
    public static @Nullable
    BlockPos getTotemPosition(StructureStart structure) {
        return totemPositions.get(structure);
    }

    public static ITextComponent forceFactionCommand(IFaction faction, ServerPlayerEntity player) {
        if (!Structures.VILLAGE.isPositionInStructure(player.world, player.getPosition())) {
            return new TranslationTextComponent("command.vampirism.test.village.no_village");
        }
        StructureStart village = Structures.VILLAGE.getStart(player.world, player.getPosition(), false);
        if (village == StructureStart.DUMMY) {
            return new TranslationTextComponent("command.vampirism.test.village.no_village");
        }
        TileEntity te = player.getEntityWorld().getTileEntity(totemPositions.get(village));
        if (!(te instanceof TotemTileEntity)) {
            LOGGER.warn("TileEntity at {} is no TotemTileEntity", totemPositions.get(village));
            return new StringTextComponent("");
        }
        TotemTileEntity tile = (TotemTileEntity) te;
        tile.forcedFaction = faction;
        tile.forcedFactionTimer = 5;
        tile.markDirty();
        return new TranslationTextComponent("command.vampirism.test.village.success", faction.getName());
    }

    public static VillageAttributes getVillageAttributes(TotemTileEntity totem) {
        return new VillageAttributes(totem);
    }

    //block attributes
    private boolean isComplete;
    private boolean isInsideVillage;
    private boolean isDisabled;

    //tile attributes
    private @Nullable
    StructureStart village;
    /**
     * use {@link #setControllingFaction(IFaction)}
     */
    private @Nullable
    IFaction controllingFaction;
    /**
     * use {@link #setCapturingFaction(IFaction)}
     */
    private @Nullable
    IFaction capturingFaction;
    /**
     * use {@link #getVillageArea()}
     */
    private @Nullable
    AxisAlignedBB villageArea;
    /**
     * use {@link #getVillageAreaReduced()}
     */
    private @Nullable
    AxisAlignedBB villageAreaReduced;

    //forced attributes
    private @Nullable
    IFaction forcedFaction;
    private int forcedFactionTimer;
    private boolean forceVillageUpdate;

    //capturing attributes
    private CAPTURE_PHASE phase;
    private int captureTimer;
    private int captureAbortTimer;
    private int defenderMax;
    private int captureForceTargetTimer;

    //client attributes
    private @OnlyIn(Dist.CLIENT)
    long beamRenderCounter;
    private @OnlyIn(Dist.CLIENT)
    float beamRenderScale;
    private float[] baseColors = DyeColor.WHITE.getColorComponentValues();
    private float[] progressColor = DyeColor.WHITE.getColorComponentValues();

    private final ServerBossInfo captureInfo = new ServerBossInfo(new TranslationTextComponent("text.vampirism.village.bossinfo.capture"), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS);

    public TotemTileEntity() {
        super(ModTiles.totem);
    }

    public boolean canPlayerRemoveBlock(PlayerEntity player) {
        if (player.abilities.isCreativeMode) return true;
        if (!player.isAlive()) return false;
        @Nullable IFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (faction == this.controllingFaction) {
            if (this.capturingFaction == null) {
                return true;
            } else {
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.totem_destroy.fail_other_capturing"), true);
            }
        } else if (faction == this.capturingFaction) {
            if (this.controllingFaction == null) {
                return true;
            } else {
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.totem_destroy.fail_other_faction"), true);
            }
        }
        return false;
    }

    @Override
    public void markDirty() {
        if (this.world != null) {
            super.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            if (this.village != null) {
                if (this.controllingFaction == VReference.VAMPIRE_FACTION) {
                    addVampireVillage(this.world.dimension, this.village.getPos(), this.village.getBoundingBox());
                } else {
                    removeVampireVillage(this.world.dimension, this.village.getPos());
                }
            }
        }
    }

    @Override
    public void remove() {
        removeVampireVillage(this.world.dimension, this.pos);
        removeTotem(this.village);
        if (this.capturingFaction != null) {
            this.abortCapture(false);
        } else {
            this.updateBossinfoPlayers(null);
        }
        super.remove();
    }

    private void abortCapture(boolean notifyPlayer) {
        this.setCapturingFaction(null);
        this.forceVillageUpdate = true;
        this.informEntitiesAboutCaptureStop();
        if (notifyPlayer)
            notifyNearbyPlayers(new TranslationTextComponent("text.vampirism.village.village_capture_aborted"));
        this.updateBossinfoPlayers(null);
        this.defenderMax = 0;
        this.markDirty();
    }

    private void completeCapture(boolean notifyPlayer, boolean fullConvert) {
        this.informEntitiesAboutCaptureStop();
        if (!this.world.isRemote)
            this.updateCreaturesOnCapture(fullConvert);
        this.setControllingFaction(this.capturingFaction);
        this.setCapturingFaction(null);
        if (notifyPlayer)
            this.notifyNearbyPlayers(new TranslationTextComponent("text.vampirism.village.village_captured_by", controllingFaction.getNamePlural()));
        this.updateBossinfoPlayers(null);
        this.markDirty();
    }

    /**
     * @return 0-100. 80 if in stage 2
     */
    @OnlyIn(Dist.CLIENT)
    public int getCaptureProgress() {
        return this.capturingFaction == null ? 0 : this.phase == CAPTURE_PHASE.PHASE_2 ? 80 : (int) (this.captureTimer / (float) VampirismConfig.BALANCE.viPhase1Duration.get() * 80f);
    }

    public void updateTileStatus() {
        Block b = this.world.getBlockState(this.pos).getBlock();
        if (!(this.isComplete = b instanceof TotemTopBlock && this.world.getBlockState(this.pos.down()).getBlock().equals(ModBlocks.totem_base)))
            return;
        ResourceLocation blockFaction = ((TotemTopBlock) b).faction;
        if (!(blockFaction.equals(this.controllingFaction == null ? nonFactionTotem : this.controllingFaction.getID()))) { //If block faction does not match tile faction, force the tile to update to the block faction
            this.forcedFaction = VampirismAPI.factionRegistry().getFactionByID(blockFaction);
        }
        if (!(this.isInsideVillage = Structures.VILLAGE.isPositionInStructure(this.world, this.pos))) return;
        StructureStart structure = Structures.VILLAGE.getStart(this.world, this.pos, false);
        if (structure == StructureStart.DUMMY) return;
        this.village = structure;
        this.isDisabled = !addTotem(this.village, this.pos);
        this.markDirty();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        this.read(tag);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putBoolean("isDisabled", this.isDisabled);
        compound.putBoolean("isComplete", this.isComplete);
        compound.putBoolean("isInsideVillage", this.isInsideVillage);
        if (this.controllingFaction != null)
            compound.putString("controllingFaction", this.controllingFaction.getID().toString());
        if (this.capturingFaction != null) {
            compound.putString("capturingFaction", this.capturingFaction.getID().toString());
            compound.putInt("captureTimer", this.captureTimer);
            compound.putInt("captureAbortTimer", this.captureAbortTimer);
            compound.putString("phase", this.phase.name());
        }
        if (village != null) {
            compound.putIntArray("villageArea", UtilLib.bbToInt(this.getVillageArea()));
        }
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.isDisabled = compound.getBoolean("isDisabled");
        this.isComplete = compound.getBoolean("isComplete");
        this.isInsideVillage = compound.getBoolean("isInsideVillage");
        if (compound.contains("controllingFaction")) {
            this.setControllingFaction(VampirismAPI.factionRegistry().getFactionByID(new ResourceLocation(compound.getString("controllingFaction"))));
        } else {
            this.setControllingFaction(null);
        }
        if (compound.contains("capturingFaction")) {
            this.setCapturingFaction(VampirismAPI.factionRegistry().getFactionByID(new ResourceLocation(compound.getString("capturingFaction"))));
            this.captureTimer = compound.getInt("captureTimer");
            this.captureAbortTimer = compound.getInt("captureabortTimer");
            this.phase = CAPTURE_PHASE.valueOf(compound.getString("phase"));
        } else {
            this.setCapturingFaction(null);
        }
        if (this.world != null) {
            if (compound.contains("villageArea")) {
                if (VReference.VAMPIRE_FACTION.equals(this.controllingFaction)) {
                    addVampireVillage(this.world.dimension, this.pos, UtilLib.intToMB(compound.getIntArray("villageArea")));
                } else {
                    removeVampireVillage(this.world.dimension, this.pos);
                }
            }
        }
        this.forceVillageUpdate = true;
    }

    private boolean spawnVillagerDefault(boolean poisonousBlood) {
        VillagerEntity newVillager = EntityType.VILLAGER.create(this.world);
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        return spawnEntity(newVillager);
    }

    private void makeAgressive() {
        if (!this.world.isRemote) {
            List<VillagerEntity> villagerEntities = this.world.getEntitiesWithinAABB(VillagerEntity.class, this.getVillageArea());
            if (VReference.VAMPIRE_FACTION.equals(this.capturingFaction)) {
                for (VillagerEntity villager : villagerEntities) {
                    if (villager instanceof IFactionEntity) continue;
                    if (villager.getGrowingAge() < 0) continue;
                    if (RNG.nextInt(3) == 0) {
                        makeAgressive(villager);
                    }
                }
            }
        }
    }

    public @Nonnull
    AxisAlignedBB getVillageArea() {
        return this.villageArea == null ? this.villageArea = AxisAlignedBB.toImmutable(this.village.getBoundingBox()) : this.villageArea;
    }

    private boolean checkTileStatus() {
        return this.isComplete && this.isInsideVillage && !this.isDisabled && this.village != null;
    }

    private boolean capturePreconditions(@Nullable IFaction faction, PlayerEntity player) {
        if (faction == null) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.no_faction"), true);
            return false;
        }
        if (capturingFaction != null) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.capturing_in_progress"), true);
            return false;
        }
        if (faction.equals(controllingFaction)) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.same_faction"), true);
            return false;
        }
        if (!isInsideVillage) {
            if (getControllingFaction() != null) {
                this.setControllingFaction(null);  //Reset the controlling faction only on interaction, not in tick. Maybe village is just temporarily unavailable #417
                this.markDirty();
            }
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.no_near_village"), true);
            return false;
        }
        if (isDisabled) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.othertotem"), true);
            return false;
        }
        return true;
    }

    private void setControllingFaction(@Nullable IFaction faction) {
        this.controllingFaction = faction;
        this.baseColors = faction != null ? faction.getColor().getColorComponents(null) : DyeColor.WHITE.getColorComponentValues();
        if (this.world != null) {
            this.world.setBlockState(this.pos, TotemTopBlock.getTotem(faction != null ? this.controllingFaction.getID() : nonFactionTotem).getDefaultState(), 55);
        }
    }

    private void setCapturingFaction(@Nullable IFaction faction) {
        this.capturingFaction = faction;
        this.progressColor = faction != null ? faction.getColor().getColorComponents(null) : DyeColor.WHITE.getColorComponentValues();
    }

    @Override
    public void tick() {
        if (this.world == null) return;
        long time = this.world.getGameTime();
        //client ---------------------------------
        if (this.world.isRemote) {
            if (time % 10 == 7 && controllingFaction != null) {
                ModParticles.spawnParticlesClient(this.world, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "generic_4"), 20, controllingFaction.getColor().getRGB(), 0.2F), this.pos.getX(), this.pos.getY(), this.pos.getZ(), 3, 30, this.world.rand);
            }
        }
        //server ---------------------------------
        else {
            if (isDisabled) this.world.destroyBlock(this.pos, true);
            if (time % 20 == 0)
                this.updateTileStatus();
            if (!this.checkTileStatus()) return;
            if (this.forcedFaction != null) {
                if (this.forcedFactionTimer > 0) {
                    if (this.forcedFactionTimer == 1) {
                        this.abortCapture(false);
                    }
                    this.forcedFactionTimer--;
                } else {
                    this.setCapturingFaction(forcedFaction);
                    this.completeCapture(false, true);
                    this.forcedFaction = null;
                }
            }
            if (this.forceVillageUpdate) {
                this.updateTileStatus();
                this.forceVillageUpdate = false;
            }
            if (capturingFaction != null) {
                if (time % 40 == 0) {
                    List<LivingEntity> entities = this.world.getEntitiesWithinAABB(LivingEntity.class, getVillageArea());
                    this.updateBossinfoPlayers(entities);
                    int attacker = 0; //include player
                    int attackerPlayer = 0;
                    int defender = 0; //include player
                    int defenderPlayer = 0;
                    int neutral = 0;
                    float attackerStrength = 0f;
                    float defenderStrength = 0f;

                    //count entities
                    for (LivingEntity entity : entities) {
                        IFaction faction = VampirismAPI.factionRegistry().getFaction(entity);
                        if (faction == null) continue;
                        if (entity instanceof ICaptureIgnore) continue;
                        if (this.capturingFaction.equals(faction)) {
                            attacker++;
                            attackerStrength += this.getStrength(entity);
                            if (entity instanceof PlayerEntity) attackerPlayer++;
                            if (entity instanceof IVillageCaptureEntity) {
                                ((IVillageCaptureEntity) entity).attackVillage(getVillageAttributes(this));
                            }
                        } else if (faction.equals(this.controllingFaction)) {
                            defender++;
                            defenderStrength += this.getStrength(entity);
                            if (entity instanceof PlayerEntity) defenderPlayer++;
                            if (entity instanceof IVillageCaptureEntity) {
                                ((IVillageCaptureEntity) entity).defendVillage(getVillageAttributes(this));
                            }
                        } else {
                            neutral++;
                        }
                    }

                    if (attackerPlayer == 0) {
                        this.captureAbortTimer++;
                    } else {
                        this.captureAbortTimer = 0;
                        captureTimer++;
                        if (this.phase == CAPTURE_PHASE.PHASE_2)
                            captureForceTargetTimer++;
                    }

                    if (this.captureAbortTimer > 7) {
                        this.abortCapture(true);
                    } else {
                        switch (this.phase) {
                            case PHASE_1_NEUTRAL:
                                if (captureTimer >= VampirismConfig.BALANCE.viPhase1Duration.get()) {
                                    this.captureTimer = 1;
                                    this.phase = CAPTURE_PHASE.PHASE_2;
                                    this.markDirty();
                                }
                                break;
                            case PHASE_1_OPPOSITE:
                                if (captureTimer >= VampirismConfig.BALANCE.viPhase1Duration.get()) {
                                    captureTimer = 1;
                                    this.phase = CAPTURE_PHASE.PHASE_2;
                                    this.markDirty();
                                    this.notifyNearbyPlayers(new TranslationTextComponent("text.vampirism.village.almost_captured", defender));
                                } else {
                                    if (captureTimer % 2 == 0) {
                                        if (attackerStrength * 1.1f > defenderStrength) {
                                            this.spawnCaptureEntity(this.controllingFaction);
                                        } else if (defenderStrength * 1.1f > attackerStrength) {
                                            this.spawnCaptureEntity(this.capturingFaction);
                                        }
                                    }
                                }
                                break;
                            case PHASE_2:
                                if (defender == 0) {
                                    captureTimer++;
                                    if (captureTimer > 4)
                                        this.completeCapture(true, false);
                                } else {
                                    captureTimer = 1;
                                }
                                break;
                            default:
                                break;
                        }
                        this.handleBossBar(defender);
                    }
                }
            }
            //normal village life
            else {
                if (this.controllingFaction != null && time % 512 == 0) {
                    if (((ServerWorld) world).getPointOfInterestManager().func_219146_b(pointOfInterestType -> ForgeRegistries.PROFESSIONS.getValues().stream().anyMatch(villagerProfession -> villagerProfession.getPointOfInterest() == pointOfInterestType), this.pos, ((int) Math.sqrt(Math.pow(this.getVillageArea().getXSize(), 2) + Math.pow(this.getVillageArea().getZSize(), 2))) / 2, PointOfInterestManager.Status.HAS_SPACE).findFirst().isPresent()) {
                        boolean isConverted = this.controllingFaction != VReference.HUNTER_FACTION && RNG.nextBoolean();
                        if (isConverted) {
                            this.spawnVillagerVampire();
                        } else {
                            this.spawnVillagerDefault(this.controllingFaction == VReference.HUNTER_FACTION);
                        }
                    }
                    int defenderNumMax = Math.min(6, this.village.getComponents().size() / 5);
                    List<? extends MobEntity> guards = Lists.newArrayList();
                    if (VReference.HUNTER_FACTION.equals(this.controllingFaction)) {
                        guards = this.world.getEntitiesWithinAABB(HunterBaseEntity.class, this.getVillageArea());
                    } else if (VReference.VAMPIRE_FACTION.equals(this.controllingFaction)) {
                        guards = this.world.getEntitiesWithinAABB(VampireBaseEntity.class, this.getVillageArea());
                    }
                    if (defenderNumMax > guards.size()) {
                        EntityType<? extends MobEntity> entityType = getCaptureEntityForFaction(this.controllingFaction);
                        this.spawnEntity(entityType.create(this.world));
                    }
                }

                //replace blocks
                if (this.controllingFaction != null && VampirismConfig.BALANCE.viReplaceBlocks.get() && time % 20 == 0) {
                    int x = (int) (this.getVillageArea().minX + RNG.nextInt((int) (this.getVillageArea().maxX - this.getVillageArea().minX)));
                    int z = (int) (this.getVillageArea().minZ + RNG.nextInt((int) (this.getVillageArea().maxZ - this.getVillageArea().minZ)));
                    BlockPos pos = new BlockPos(x, world.getHeight(Heightmap.Type.WORLD_SURFACE, new BlockPos(x, 0, z)).getY() - 1, z);
                    BlockState b = world.getBlockState(pos);
                    boolean flag = false;
                    if (VReference.VAMPIRE_FACTION.equals(this.controllingFaction)) {
                        if (!(world.getBlockState(pos.up()).getBlock() instanceof BushBlock)) {
                            if (b.getBlock() == world.getBiome(pos).getSurfaceBuilderConfig().getTop().getBlock() && b.getBlock() != Blocks.SAND) {
                                world.removeBlock(pos.up(), false);
                                world.setBlockState(pos, ModBlocks.cursed_earth.getDefaultState());
                                if (world.getBlockState(pos.up()).getBlock() == Blocks.TALL_GRASS) {
                                    world.removeBlock(pos.up(), false);
                                    flag = true;
                                }
                            }
                        }
                    } else if (controllingFaction == VReference.HUNTER_FACTION) {
                        if (b.getBlock() == ModBlocks.cursed_earth) {
                            world.setBlockState(pos, world.getBiome(pos).getSurfaceBuilderConfig().getTop());
                            flag = true;
                        }
                    }
                    if (!flag) {
                        ModEventFactory.fireReplaceVillageBlockEvent(world, b, pos, controllingFaction);
                    }
                }
            }
        }
    }

    private @Nonnull
    AxisAlignedBB getVillageAreaReduced() {
        return this.villageAreaReduced == null ? this.villageAreaReduced = AxisAlignedBB.toImmutable(this.village.getBoundingBox()).grow(-30, -10, -30) : this.villageAreaReduced;
    }

    public void initiateCapture(PlayerEntity player) {
        this.updateTileStatus();
        if (!player.isAlive()) return;
        IFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (!this.capturePreconditions(faction, player)) return;
        this.forceVillageUpdate = true;
        this.captureAbortTimer = 0;
        this.captureTimer = 0;
        this.captureForceTargetTimer = 0;
        this.setCapturingFaction(faction);
        this.captureInfo.setName(new TranslationTextComponent("text.vampirism.village.bossinfo.capture"));
        this.captureInfo.setColor(BossInfo.Color.YELLOW);
        this.captureInfo.setPercent(0f);
        this.defenderMax = 0;

        if (this.controllingFaction == null) {
            this.phase = CAPTURE_PHASE.PHASE_1_NEUTRAL;
            this.notifyNearbyPlayers(new TranslationTextComponent("text.vampirism.village.neutral_village_under_attack", faction.getNamePlural()));
        } else {
            this.phase = CAPTURE_PHASE.PHASE_1_OPPOSITE;
            this.notifyNearbyPlayers(new TranslationTextComponent("text.vampirism.village.faction_village_under_attack", this.controllingFaction.getNamePlural(), faction.getNamePlural()));
        }

        this.markDirty();

        this.makeAgressive();
    }

    private void informEntitiesAboutCaptureStop() {
        if (this.world.isRemote) return;
        List<CreatureEntity> list = this.world.getEntitiesWithinAABB(CreatureEntity.class, this.getVillageArea());
        for (CreatureEntity e : list) {
            if (e instanceof IVillageCaptureEntity) {
                ((IVillageCaptureEntity) e).stopVillageAttackDefense();
            }
        }
    }

    /**
     * Update the bossbar to only include players that are in the given list.
     * If list is null, remove all players
     *
     * @param includedPlayerEntities List of player entities to be included. May contain other non player entities
     */
    private void updateBossinfoPlayers(@Nullable List<LivingEntity> includedPlayerEntities) {
        Set<ServerPlayerEntity> oldList = new HashSet<>(captureInfo.getPlayers());
        if (includedPlayerEntities != null) {
            for (LivingEntity entity : includedPlayerEntities) {
                if (entity instanceof ServerPlayerEntity) {
                    if (!oldList.remove(entity)) {
                        captureInfo.addPlayer((ServerPlayerEntity) entity);
                    }
                }
            }
        }
        for (ServerPlayerEntity player : oldList) {
            captureInfo.removePlayer(player);
        }
    }

    static {
        captureEntities = Maps.newHashMap();
        captureEntities.put(VReference.HUNTER_FACTION, Lists.newArrayList(new CaptureEntityEntry(ModEntities.hunter, 10), new CaptureEntityEntry(ModEntities.advanced_hunter, 2)));
        captureEntities.put(VReference.VAMPIRE_FACTION, Lists.newArrayList(new CaptureEntityEntry(ModEntities.vampire, 10), new CaptureEntityEntry(ModEntities.advanced_vampire, 2)));
    }

    private static class CaptureEntityEntry extends WeightedRandom.Item {
        private final EntityType<? extends MobEntity> entity;

        CaptureEntityEntry(EntityType<? extends MobEntity> entity, int itemWeightIn) {
            super(itemWeightIn);
            this.entity = entity;
        }

        public EntityType<? extends MobEntity> getEntity() {
            return entity;
        }
    }

    private enum CAPTURE_PHASE {
        PHASE_1_NEUTRAL, PHASE_1_OPPOSITE, PHASE_2
    }

    //support methods --------------------------------------------------------------------------------------------------
    public static AggressiveVillagerEntity makeAgressive(VillagerEntity villager) {
        AggressiveVillagerEntity hunter = AggressiveVillagerEntity.makeHunter(villager);
        villager.getEntityWorld().addEntity(hunter);
        villager.remove();
        return hunter;
    }

    private void spawnCaptureEntity(IFaction faction) {
        EntityType<? extends MobEntity> entityType = this.getCaptureEntityForFaction(faction);
        if (entityType == null) {
            LOGGER.warn("No village capture entity registered for {}", faction);
            return;
        }
        MobEntity entity = entityType.create(this.world);
        if (entity instanceof VampireBaseEntity)
            ((VampireBaseEntity) entity).setSpawnRestriction(VampireBaseEntity.SpawnRestriction.SIMPLE);
        List<? extends PlayerEntity> players = this.world.getPlayers();
        players.removeIf(PlayerEntity::isSpectator);
        if (entity != null && !UtilLib.spawnEntityInWorld(this.world, this.getVillageAreaReduced(), entity, 50, players, SpawnReason.EVENT)) {
            entity.remove();
            entity = null;
        }
        if (entity instanceof IVillageCaptureEntity) {
            if (this.controllingFaction.equals(faction))
                ((IVillageCaptureEntity) entity).defendVillage(getVillageAttributes(this));
            else
                ((IVillageCaptureEntity) entity).attackVillage(getVillageAttributes(this));
        } else if (entity != null) {
            LOGGER.warn("Creature registered for village capture does not implement IVillageCaptureEntity ({})", entity.getEntityString());
        } else {
            LOGGER.info("Failed to spawn capture creature");
        }
    }

    private void handleBossBar(int defenderLeft) {
        if (phase == CAPTURE_PHASE.PHASE_1_NEUTRAL || phase == CAPTURE_PHASE.PHASE_1_OPPOSITE) {
            captureInfo.setPercent(this.captureTimer / (float) VampirismConfig.BALANCE.viPhase1Duration.get());
        } else if (phase == CAPTURE_PHASE.PHASE_2) {
            if (defenderMax != 0) {
                if (defenderLeft > defenderMax) defenderMax = defenderLeft;
                captureInfo.setPercent((float) defenderLeft / (float) defenderMax);
            } else {
                defenderMax = defenderLeft;
                captureInfo.setName(new TranslationTextComponent("text.vampirism.village.defender_remaining"));
                captureInfo.setColor(BossInfo.Color.WHITE);
            }
        }
    }

    private EntityType<? extends MobEntity> getCaptureEntityForFaction(IFaction faction) {
        return WeightedRandom.getRandomItem(RNG, captureEntities.get(faction)).getEntity();
    }

    private boolean spawnVillagerReplace(MobEntity oldEntity, boolean poisonousBlood, boolean replaceOld) {
        VillagerEntity newVillager = EntityType.VILLAGER.create(this.world);
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        if (oldEntity instanceof VillagerEntity)
            newVillager.setHomePosAndDistance(oldEntity.getHomePosition(), (int) oldEntity.getMaximumHomeDistance());
        return spawnEntity(newVillager, oldEntity, replaceOld);
    }

    private boolean spawnVillagerReplaceForced(MobEntity oldEntity, boolean poisonousBlood, boolean replaceOld) {
        VillagerEntity newVillager = EntityType.VILLAGER.create(this.world);
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        newVillager.copyLocationAndAnglesFrom(oldEntity);
        if (oldEntity instanceof VillagerEntity) {
            newVillager.setHomePosAndDistance(oldEntity.getHomePosition(), (int) oldEntity.getMaximumHomeDistance());
        }
        if (replaceOld) {
            oldEntity.remove();
        }
        return this.world.addEntity(newVillager);
    }

    private boolean spawnVillagerVampire() {
        return this.spawnEntity(ModEntities.villager_converted.create(this.world));
    }

    private void updateCreaturesOnCapture(boolean fullConvert) {
        List<VillagerEntity> villagerEntities = this.world.getEntitiesWithinAABB(VillagerEntity.class, getVillageArea());
        if (VReference.HUNTER_FACTION.equals(this.capturingFaction)) {
            List<HunterBaseEntity> hunterEntities = this.world.getEntitiesWithinAABB(HunterBaseEntity.class, getVillageArea());
            int i = Math.max(2, hunterEntities.size() / 2);
            for (HunterBaseEntity hunter : hunterEntities) {
                if (i-- > 0) {
                    this.spawnVillagerReplace(hunter, true, true);
                }
            }
            for (int o = i; o > 0; o--) {
                this.spawnVillagerDefault(true);
            }
            for (VillagerEntity villager : villagerEntities) {
                if (villager.getVillagerData().getProfession() == ModVillage.vampire_expert) {
                    villager.setVillagerData(villager.getVillagerData().withProfession(VillagerProfession.NONE));
                }
                if (villager.isPotionActive(ModEffects.sanguinare))
                    villager.removePotionEffect(ModEffects.sanguinare);
                ExtendedCreature.getSafe(villager).ifPresent(e -> e.setPoisonousBlood(true));
                if (fullConvert) {
                    if (villager instanceof ConvertedVillagerEntity) {
                        this.spawnVillagerReplaceForced(villager, true, true);
                    }
                }
            }
            this.updateTrainer(false);
            if (fullConvert) {
                List<VampireBaseEntity> vampireEntities = this.world.getEntitiesWithinAABB(VampireBaseEntity.class, getVillageArea());
                for (VampireBaseEntity vampire : vampireEntities) {
                    this.spawnEntity(this.getCaptureEntityForFaction(this.capturingFaction).create(this.world), vampire, true);
                }
            }
        } else if (VReference.VAMPIRE_FACTION.equals(this.capturingFaction)) {
            for (VillagerEntity villager : villagerEntities) {
                if (villager.getVillagerData().getProfession() == ModVillage.hunter_expert) {
                    villager.setVillagerData(villager.getVillagerData().withProfession(VillagerProfession.NONE));
                }
                ExtendedCreature.getSafe(villager).ifPresent(e -> e.setPoisonousBlood(false));
                if (!fullConvert) {
                    if (RNG.nextInt(2) == 1) continue;
                    PotionSanguinare.addRandom(villager, false);
                } else {
                    villager.addPotionEffect(new PotionSanguinareEffect(11));
                }
            }
            if (fullConvert) {
                List<HunterBaseEntity> vampireEntities = this.world.getEntitiesWithinAABB(HunterBaseEntity.class, getVillageArea());
                for (HunterBaseEntity vampire : vampireEntities) {
                    this.spawnEntity(this.getCaptureEntityForFaction(this.capturingFaction).create(this.world), vampire, true);
                }
            }
            updateTrainer(true);
        }
    }

    private boolean spawnEntity(MobEntity newEntity) {
        return UtilLib.spawnEntityInWorld(this.world, this.getVillageAreaReduced(), newEntity, 50, Lists.newArrayList(), SpawnReason.EVENT);
    }

    private boolean spawnEntity(MobEntity newEntity, MobEntity oldEntity, boolean replaceOld) {
        newEntity.copyDataFromOld(oldEntity);
        newEntity.setUniqueId(MathHelper.getRandomUUID());
        if (replaceOld) oldEntity.remove();
        return this.world.addEntity(newEntity);
    }

    private void updateTrainer(boolean toDummy) {
        if (toDummy) {
            List<HunterTrainerEntity> huntertrainer = this.world.getEntitiesWithinAABB(HunterTrainerEntity.class, this.getVillageArea());
            for (HunterTrainerEntity trainer : huntertrainer) {
                DummyHunterTrainerEntity dummy = ModEntities.hunter_trainer_dummy.create(this.world);
                dummy.copyDataFromOld(trainer);
                dummy.setUniqueId(MathHelper.getRandomUUID());
                trainer.remove();
                world.addEntity(dummy);
            }
        } else {
            List<DummyHunterTrainerEntity> huntertrainerdummy = this.world.getEntitiesWithinAABB(DummyHunterTrainerEntity.class, this.getVillageArea());
            for (DummyHunterTrainerEntity dummy : huntertrainerdummy) {
                HunterTrainerEntity trainer = ModEntities.hunter_trainer.create(this.world);
                trainer.copyDataFromOld(dummy);
                trainer.setUniqueId(MathHelper.getRandomUUID());
                trainer.setHome(dummy.getHome());
                dummy.remove();
                world.addEntity(trainer);
            }
        }
    }

    //accessors for other classes --------------------------------------------------------------------------------------

    private float getStrength(LivingEntity entity) {
        if (entity instanceof PlayerEntity)
            return FactionPlayerHandler.getOpt((PlayerEntity) entity).map(FactionPlayerHandler::getCurrentLevelRelative).orElse(0f);
        if (entity instanceof ConvertedVillagerEntity)
            return 0.5f;
        if (entity instanceof IAggressiveVillager)
            return 0.7f;
        if (entity instanceof VillagerEntity)
            return 0.4f;
        return 1f;
    }

    public @Nullable
    IFaction getCapturingFaction() {
        return capturingFaction;
    }

    public @Nullable
    IFaction getControllingFaction() {
        return controllingFaction;
    }

    private void notifyNearbyPlayers(ITextComponent textComponent) {
        for (PlayerEntity player : this.world.getPlayers()) {
            if (player.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ()) > VampirismConfig.BALANCE.viNotifyDistanceSQ.get())
                continue;
            player.sendStatusMessage(textComponent, true);
        }
    }

    //client------------------------------------------------------------------------------------------------------------

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.handleUpdateTag(pkt.getNbtCompound());
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getBaseColors() {
        return this.baseColors;
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getCapturingColors() {
        return this.progressColor;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    private static class VillageAttributes implements IVillageAttributes {
        private final @Nullable
        IFaction defendingFaction;
        private final @Nullable
        IFaction attackingFaction;
        private final AxisAlignedBB villageArea;
        private final TotemTileEntity totem;
        private final BlockPos pos;

        private VillageAttributes(TotemTileEntity totem) {
            this.defendingFaction = totem.controllingFaction;
            this.attackingFaction = totem.capturingFaction;
            this.villageArea = totem.getVillageAreaReduced();
            this.totem = totem;
            this.pos = totem.pos;
        }

        @Override
        public @Nullable
        IFaction getDefendingFaction() {
            return this.defendingFaction;
        }

        @Override
        public @Nullable
        IFaction getAttackingFaction() {
            return this.attackingFaction;
        }

        @Override
        public AxisAlignedBB getVillageArea() {
            return this.villageArea;
        }

        @Override
        public BlockPos getPosition() {
            return this.pos;
        }

        public boolean shouldForceTargets() {
            return this.totem.captureForceTargetTimer > VampirismConfig.BALANCE.viForceTargetTime.get();
        }

    }

    @OnlyIn(Dist.CLIENT)
    public float shouldRenderBeam() {
        if (!this.isComplete || isDisabled) return 0f;
        int i = (int) (this.world.getGameTime() - this.beamRenderCounter);
        this.beamRenderCounter = this.world.getGameTime();
        if (i > 1) {
            this.beamRenderScale -= (float) i / 40.0f;
            if (this.beamRenderScale < 0f)
                this.beamRenderScale = 0f;
        }
        this.beamRenderScale += 0.025f;
        if (this.beamRenderScale > 1f)
            this.beamRenderScale = 1.0f;
        return this.beamRenderScale;
    }
}
