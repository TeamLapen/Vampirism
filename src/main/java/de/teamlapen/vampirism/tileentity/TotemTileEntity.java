package de.teamlapen.vampirism.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IAggressiveVillager;
import de.teamlapen.vampirism.api.entity.ICaptureIgnore;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.api.world.ITotem;
import de.teamlapen.vampirism.blocks.TotemBaseBlock;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.FactionVillagerProfession;
import de.teamlapen.vampirism.entity.VampirismEntity;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterest;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.BossInfo;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static de.teamlapen.vampirism.tileentity.TotemHelper.*;

@ParametersAreNonnullByDefault
public class TotemTileEntity extends TileEntity implements ITickableTileEntity, ITotem {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RNG = new Random();
    private static final ResourceLocation nonFactionTotem = new ResourceLocation("none");

    //block attributes
    private boolean isComplete;
    private boolean isInsideVillage;
    private boolean isDisabled;

    //tile attributes
    private @Nonnull
    Set<PointOfInterest> village = Sets.newHashSet();
    /**
     * use {@link #setControllingFaction(IFaction)}
     */
    private @Nullable
    IFaction<?> controllingFaction;
    /**
     * use {@link #setCapturingFaction(IFaction)}
     */
    private @Nullable
    IFaction<?> capturingFaction;
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
    IFaction<?> forcedFaction;
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

    /**
     * checks if the player can remove the totem
     *
     * @param player player to check
     * @return weather he can or not
     */
    public boolean canPlayerRemoveBlock(PlayerEntity player) {
        if (player.abilities.isCreativeMode) return true;
        if (!player.isAlive()) return false;
        @Nullable IFaction<?> faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (faction == this.controllingFaction) {
            if (this.capturingFaction == null) {
                return true;
            } else {
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.totem_destroy.fail_other_capturing"), true);
                return false;
            }
        } else if (faction == this.capturingFaction) {
            if (this.controllingFaction == null) {
                return true;
            } else {
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.totem_destroy.fail_other_faction"), true);
                return false;
            }
        } else {
            if(!(this.capturingFaction == null && this.controllingFaction == null)) {
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.totem_destroy.fail_other_faction"), true);
                return false;
            }
            return true;
        }
    }

    @Override
    public void markDirty() {
        if (this.world != null) {
            super.markDirty();
            this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
            if (!this.village.isEmpty()) {
                if (this.controllingFaction == VReference.VAMPIRE_FACTION) {
                    TotemHelper.addVampireVillage(this.world.getDimensionKey(), this.pos, this.getVillageArea());
                } else {
                    TotemHelper.removeVampireVillage(this.world.getDimensionKey(), this.pos);
                }
            }
        }
    }

    /**
     * gets the size of the village
     *
     * @return amount of {@link PointOfInterest} related to this village totem
     */
    public int getSize() {
        return this.village.size();
    }

    //support methods --------------------------------------------------------------------------------------------------
    public static void makeAgressive(VillagerEntity villager) {
        AggressiveVillagerEntity hunter = AggressiveVillagerEntity.makeHunter(villager);
        villager.getEntityWorld().addEntity(hunter);
        villager.remove();
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

    @Override
    public void remove() {
        //noinspection ConstantConditions
        TotemHelper.removeVampireVillage(this.world.getDimensionKey(), this.pos);
        TotemHelper.removeTotem(this.village, this.pos);
        if (this.capturingFaction != null) {
            this.abortCapture(false);
        } else {
            this.updateBossinfoPlayers(null);
        }
        super.remove();
    }

    /**
     * @return 0-100. 80 if in stage 2
     */
    @OnlyIn(Dist.CLIENT)
    public int getCaptureProgress() {
        return this.capturingFaction == null ? 0 : this.phase == CAPTURE_PHASE.PHASE_2 ? 80 : (int) (this.captureTimer / (float) VampirismConfig.BALANCE.viPhase1Duration.get() * 80f);
    }

    private void completeCapture(boolean notifyPlayer, boolean fullConvert) {
        this.informEntitiesAboutCaptureStop();
        //noinspection ConstantConditions
        if (!this.world.isRemote)
            this.updateCreaturesOnCapture(fullConvert);
        if (this.controllingFaction != null) {
            for (PlayerEntity p : world.getPlayers()) { //Add stat
                if (this.getVillageArea().contains(p.getPositionVec())) {
                    FactionPlayerHandler.getOpt(p).filter(fph -> fph.getCurrentFaction() == this.capturingFaction).ifPresent(fph -> fph.getPlayer().addStat(ModStats.capture_village));
                }
            }
        }


        this.setControllingFaction(this.capturingFaction);
        this.setCapturingFaction(null);

        if (notifyPlayer) {
            assert controllingFaction != null;
            this.notifyNearbyPlayers(new TranslationTextComponent("text.vampirism.village.village_captured_by", controllingFaction.getNamePlural()));
        }
        this.updateBossinfoPlayers(null);
        this.markDirty();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.read(state, tag);
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
            if (this.phase == CAPTURE_PHASE.PHASE_2) {
                compound.putInt("defenderMax", this.defenderMax);
            }
        }
        if (!village.isEmpty()) {
            compound.putIntArray("villageArea", UtilLib.bbToInt(this.getVillageArea()));
        }
        return super.write(compound);
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
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
            if (this.phase == CAPTURE_PHASE.PHASE_2 && compound.contains("defenderMax")) {
                this.defenderMax = compound.getInt("defenderMax");
                this.setupPhase2();
            }
        } else {
            this.setCapturingFaction(null);
        }
        if (this.world != null) {
            if (compound.contains("villageArea")) {
                if (VReference.VAMPIRE_FACTION.equals(this.controllingFaction)) {
                    TotemHelper.addVampireVillage(this.world.getDimensionKey(), this.pos, UtilLib.intToBB(compound.getIntArray("villageArea")));
                } else {
                    TotemHelper.removeVampireVillage(this.world.getDimensionKey(), this.pos);
                }
            }
        }
        this.forceVillageUpdate = true;
    }

    /**
     * updates the tile status and determines the related {@link PointOfInterest}s
     * <p>
     * this includes checking if the totem is placed in a village, the totem is complete, if there is another totem and forces a faction
     */
    public void updateTileStatus() {
        if(!(this.world instanceof ServerWorld))return;

        //noinspection ConstantConditions
        Block b = this.world.getBlockState(this.pos).getBlock();
        if (!(this.isComplete = b instanceof TotemTopBlock && this.world.getBlockState(this.pos.down()).getBlock().equals(ModBlocks.totem_base)))
            return;
        ResourceLocation blockFaction = ((TotemTopBlock) b).faction;
        if (!(blockFaction.equals(this.controllingFaction == null ? nonFactionTotem : this.controllingFaction.getID()))) { //If block faction does not match tile faction, force the tile to update to the block faction
            this.forcedFaction = VampirismAPI.factionRegistry().getFactionByID(blockFaction);
        }
        Set<PointOfInterest> points = TotemHelper.getVillagePointsOfInterest((ServerWorld) world, this.pos);
        if (!(this.isInsideVillage = !points.isEmpty() )) {
            this.village = Collections.emptySet();
            if (this.controllingFaction != null) {
                this.setControllingFaction(null);
            }
        } else if (!(7 == TotemHelper.isVillage(points, (ServerWorld) this.world, this.pos, this.controllingFaction != null || this.capturingFaction != null))) {
            this.isInsideVillage = false;
            this.village = Collections.emptySet();
            if (this.controllingFaction != null) {
                this.setControllingFaction(null);
            }
        }else if (!(this.isDisabled = !TotemHelper.addTotem((ServerWorld) this.world, points, this.pos))) {
            this.village = points;
        } else {
            this.village = Collections.emptySet();
        }
        this.markDirty();
    }

    private void spawnVillagerDefault(boolean poisonousBlood) {
        //noinspection ConstantConditions
        VillagerEntity newVillager = EntityType.VILLAGER.create(this.world);
        //noinspection ConstantConditions
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        newVillager = ModEventFactory.fireSpawnNewVillagerEvent(this, null, newVillager, false, poisonousBlood);
        spawnEntity(newVillager);
    }

    public void setForcedFaction(@Nullable IFaction<?> faction) {
        this.forcedFaction = faction;
        this.forcedFactionTimer = 5;
        this.markDirty();
    }

    private void makeAgressive() {
        //noinspection ConstantConditions
        if (!this.world.isRemote) {
            List<VillagerEntity> villagerEntities = this.world.getEntitiesWithinAABB(VillagerEntity.class, this.getVillageArea());
            for (VillagerEntity villager : villagerEntities) {
                if (ModEventFactory.fireMakeAggressive(this, villager)) {
                    if (VReference.VAMPIRE_FACTION.equals(this.capturingFaction)) {
                        if (villager instanceof IFactionEntity) continue;
                        if (villager.getGrowingAge() < 0) continue;
                        if (RNG.nextInt(3) == 0) {
                            makeAgressive(villager);
                        }
                    }
                }
            }
        }
    }

    private boolean checkTileStatus() {
        return this.isComplete && this.isInsideVillage && !this.isDisabled && !this.village.isEmpty();
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
            if (isDisabled){
                this.world.destroyBlock(this.pos, true);
                if(this.world.getBlockState(this.pos.down()).getBlock() instanceof TotemBaseBlock){
                    this.world.destroyBlock(this.pos.down(), true);
                }
            }
            if (time % 20 == 0) {
                this.updateTileStatus();
            }
            if (!this.checkTileStatus()) {
                if(!isInsideVillage && capturingFaction != null) {
                    abortCapture(false);
                }
                return;
            }
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
            if (time % 12000 == 0) {
                this.updateVillageArea();
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
                    CaptureInfo captureInfo = new CaptureInfo(this);
                    for (LivingEntity entity : entities) {
                        IFaction<?> faction = VampirismAPI.factionRegistry().getFaction(entity);
                        if (faction == null) continue;
                        if (entity instanceof ICaptureIgnore) continue;
                        if (this.capturingFaction.equals(faction)) {
                            attacker++;
                            attackerStrength += this.getStrength(entity);
                            if (entity instanceof PlayerEntity) attackerPlayer++;
                            if (entity instanceof IVillageCaptureEntity) {
                                ((IVillageCaptureEntity) entity).attackVillage(captureInfo);
                            }
                        } else if (faction.equals(this.controllingFaction)) {
                            defender++;
                            defenderStrength += this.getStrength(entity);
                            if (entity instanceof PlayerEntity) defenderPlayer++;
                            if (entity instanceof IVillageCaptureEntity) {
                                ((IVillageCaptureEntity) entity).defendVillage(captureInfo);
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
                                    this.setupPhase2();
                                    this.markDirty();
                                }
                                break;
                            case PHASE_1_OPPOSITE:
                                if (captureTimer >= VampirismConfig.BALANCE.viPhase1Duration.get()) {
                                    captureTimer = 1;
                                    this.setupPhase2();
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
                    int beds = (int) ((ServerWorld) world).getPointOfInterestManager().func_219146_b(pointOfInterestType -> pointOfInterestType.equals(PointOfInterestType.HOME), this.pos, ((int) Math.sqrt(Math.pow(this.getVillageArea().getXSize(), 2) + Math.pow(this.getVillageArea().getZSize(), 2))) / 2, PointOfInterestManager.Status.ANY).count();
                    boolean spawnTaskMaster = RNG.nextInt(6) == 0;
                    int villager = this.world.getEntitiesWithinAABB(VillagerEntity.class, this.getVillageArea().grow(20)).size();
                    int max = Math.min(beds, VampirismConfig.BALANCE.viMaxVillagerRespawn.get());
                    if (villager < max) {
                        boolean isConverted = this.controllingFaction == VReference.VAMPIRE_FACTION && RNG.nextBoolean();
                        if (isConverted) {
                            this.spawnVillagerVampire();
                        } else {
                            this.spawnVillagerDefault(this.controllingFaction == VReference.HUNTER_FACTION);
                        }
                    } else {
                        spawnTaskMaster = true;
                    }
                    if (spawnTaskMaster && this.world.getEntitiesWithinAABB(VampirismEntity.class, this.getVillageArea(), entity -> entity instanceof ITaskMasterEntity).isEmpty()) {
                        this.spawnTaskMaster();
                    }
                    int defenderNumMax = Math.min(6, this.village.size() / 5);
                    List<? extends MobEntity> guards = this.world.getEntitiesWithinAABB(this.controllingFaction.getVillageData().getGuardSuperClass(), this.getVillageArea());
                    if (defenderNumMax > guards.size()) {
                        EntityType<? extends MobEntity> entityType = getCaptureEntityForFaction(this.controllingFaction);
                        //noinspection ConstantConditions
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
                            if (b.getBlock() == world.getBiome(pos).getGenerationSettings().getSurfaceBuilderConfig().getTop().getBlock() && b.getBlock() != Blocks.SAND) {
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
                            world.setBlockState(pos, world.getBiome(pos).getGenerationSettings().getSurfaceBuilderConfig().getTop());
                            flag = true;
                        }
                    }
                    if (!flag) {
                        ModEventFactory.fireReplaceVillageBlockEvent(this, b, pos);
                    }
                }
            }
        }
    }

    private void setControllingFaction(@Nullable IFaction<?> faction) {
        this.controllingFaction = faction;
        this.baseColors = faction != null ? faction.getColor().getColorComponents(null) : DyeColor.WHITE.getColorComponentValues();
        if (this.world != null) {
            BlockState oldBlockState = this.getBlockState();
            Block b = oldBlockState.getBlock();
            boolean crafted = b instanceof TotemTopBlock && ((TotemTopBlock) b).isCrafted();
            BlockState newBlockState = (faction == null ? crafted ? ModBlocks.totem_top_crafted : ModBlocks.totem_top : crafted ? faction.getVillageData().getTotemTopBlock().getRight() : faction.getVillageData().getTotemTopBlock().getLeft()).getDefaultState();
            try { //https://github.com/TeamLapen/Vampirism/issues/793 no idea what might cause this
                this.world.setBlockState(this.pos, newBlockState, 55);
            } catch (IllegalStateException e) {
                LOGGER.error("Setting blockstate from {} to {}", oldBlockState, newBlockState);
                LOGGER.error("Failed to set totem blockstate", e);
            }
        }
    }

    private void setCapturingFaction(@Nullable IFaction<?> faction) {
        this.capturingFaction = faction;
        this.progressColor = faction != null ? faction.getColor().getColorComponents(null) : DyeColor.WHITE.getColorComponentValues();
    }

    @Override
    public @Nonnull
    AxisAlignedBB getVillageArea() {
        if (this.villageArea == null) {
            updateVillageArea();
        }
        return this.villageArea;
    }

    @Override
    public @Nonnull
    AxisAlignedBB getVillageAreaReduced() {
        if (this.villageAreaReduced == null) {
            updateVillageArea();
        }
        return this.villageAreaReduced;
    }

    private boolean capturePreconditions(@Nullable IFaction<?> faction, PlayerEntity player) {
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
            //noinspection ConstantConditions
            Map<Integer, Integer> stats = TotemHelper.getVillageStats(TotemHelper.getVillagePointsOfInterest((ServerWorld) world, this.pos), this.world);
            int status = TotemHelper.isVillage(stats, this.controllingFaction != null || this.capturingFaction != null);
            IFormattableTextComponent text = new TranslationTextComponent("text.vampirism.village.missing_components");
            if ((status & 1) == 0) {
                text.appendString("\n  - ");
                text.append(new TranslationTextComponent("text.vampirism.village.missing_components.home"));
                text.appendString(" " + stats.get(1) + "/" + MIN_HOMES);
            }
            if ((status & 2) == 0) {
                text.appendString("\n  - ");
                text.append(new TranslationTextComponent("text.vampirism.village.missing_components.workstations"));
                text.appendString(" " + stats.get(2) + "/" + MIN_WORKSTATIONS);
            }
            if ((status & 4) == 0) {
                text.appendString("\n  - ");
                text.append(new TranslationTextComponent("text.vampirism.village.missing_components.villager"));
                text.appendString(" " + stats.get(4) + "/" + MIN_VILLAGER);
            }
            player.sendStatusMessage(text, false);


            return false;
        }
        if (isDisabled) {
            player.sendStatusMessage(new TranslationTextComponent("text.vampirism.village.othertotem"), true);
            return false;
        }
        VampirismVillageEvent.InitiateCapture event = new VampirismVillageEvent.InitiateCapture(this, faction);
        MinecraftForge.EVENT_BUS.post(event);
        if(event.getResult().equals(Event.Result.DENY)) {
            player.sendStatusMessage(new TranslationTextComponent(event.getMessage()), true);
            return false;
        }
        return true;
    }

    private void updateVillageArea() {
        if(this.villageArea != null && this.villageAreaReduced != null) {
            if(this.village.stream().allMatch(point -> this.villageAreaReduced.contains(Vector3d.copy(point.getPos())))){
                return;
            }
        }
        AxisAlignedBB totem = TotemHelper.getAABBAroundPOIs(this.village);
        StructureStart<?> start = UtilLib.getStructureStartAt(this.world,this.pos,Structure.field_236381_q_);
        if (start!=null&&start!=StructureStart.DUMMY&&start.isValid()) {
            totem = UtilLib.MBtoAABB(start.getBoundingBox());
        }
        assert totem != null;
        this.villageArea = totem;
        this.villageAreaReduced = totem.grow(-30,-10,-30);
    }

    @SuppressWarnings("ConstantConditions")
    public void initiateCapture(PlayerEntity player) {
        this.updateTileStatus();
        if (!player.isAlive()) return;
        IFaction<?> faction = FactionPlayerHandler.get(player).getCurrentFaction();
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

    private enum CAPTURE_PHASE {
        PHASE_1_NEUTRAL, PHASE_1_OPPOSITE, PHASE_2
    }

    private void informEntitiesAboutCaptureStop() {
        //noinspection ConstantConditions
        if (this.world.isRemote) return;
        List<CreatureEntity> list = this.world.getEntitiesWithinAABB(CreatureEntity.class, this.getVillageArea());
        for (CreatureEntity e : list) {
            if (e instanceof IVillageCaptureEntity) {
                ((IVillageCaptureEntity) e).stopVillageAttackDefense();
            }
        }
    }

    private void spawnCaptureEntity(@Nullable IFaction<?> faction) {
        if (faction == null) return;
        assert this.world instanceof ServerWorld;
        EntityType<? extends MobEntity> entityType = this.getCaptureEntityForFaction(faction);
        if (entityType == null) {
            LOGGER.warn("No village capture entity registered for {}", faction);
            return;
        }
        //noinspection ConstantConditions
        MobEntity entity = entityType.create(this.world);
        if (entity instanceof VampireBaseEntity)
            ((VampireBaseEntity) entity).setSpawnRestriction(VampireBaseEntity.SpawnRestriction.SIMPLE);
        List<? extends PlayerEntity> players = this.world.getPlayers();
        players.removeIf(PlayerEntity::isSpectator);
        if (entity != null && !UtilLib.spawnEntityInWorld((ServerWorld) this.world, this.getVillageAreaReduced(), entity, 50, players, SpawnReason.EVENT)) {
            entity.remove();
            entity = null;
        }
        if (entity instanceof IVillageCaptureEntity) {
            if (faction == this.controllingFaction)
                ((IVillageCaptureEntity) entity).defendVillage(new CaptureInfo(this));
            else
                ((IVillageCaptureEntity) entity).attackVillage(new CaptureInfo(this));
        } else if (entity != null) {
            LOGGER.warn("Creature registered for village capture does not implement IVillageCaptureEntity ({})", entityType.getRegistryName());
        } else {
            LOGGER.info("Failed to spawn capture creature");
        }
    }

    private void setupPhase2() {
        if (this.phase != CAPTURE_PHASE.PHASE_2)
            this.phase = CAPTURE_PHASE.PHASE_2;
        this.captureInfo.setName(new TranslationTextComponent("text.vampirism.village.defender_remaining"));
        this.captureInfo.setColor(BossInfo.Color.WHITE);
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
                this.setupPhase2();
            }
        }
    }

    @Nullable
    @Override
    public EntityType<? extends MobEntity> getCaptureEntityForFaction(@Nonnull IFaction<?> faction) {
        return WeightedRandom.getRandomItem(RNG, faction.getVillageData().getCaptureEntries()).getEntity();
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnVillagerReplace(MobEntity oldEntity, boolean poisonousBlood) {
        VillagerEntity newVillager = EntityType.VILLAGER.create(this.world);
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        if (oldEntity instanceof VillagerEntity)
            newVillager.setHomePosAndDistance(oldEntity.getHomePosition(), (int) oldEntity.getMaximumHomeDistance());
        newVillager = ModEventFactory.fireSpawnNewVillagerEvent(this, oldEntity, newVillager, true, poisonousBlood);
        spawnEntity(newVillager, oldEntity, true, true);
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnVillagerReplaceForced(MobEntity oldEntity, boolean poisonousBlood) {
        VillagerEntity newVillager = EntityType.VILLAGER.create(this.world);
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        newVillager.copyLocationAndAnglesFrom(oldEntity);
        if (oldEntity instanceof VillagerEntity) {
            newVillager.setHomePosAndDistance(oldEntity.getHomePosition(), (int) oldEntity.getMaximumHomeDistance());
        }
        newVillager = ModEventFactory.fireSpawnNewVillagerEvent(this, oldEntity, newVillager, true, poisonousBlood);
        oldEntity.remove();
        this.world.addEntity(newVillager);
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnVillagerVampire() {
        this.spawnEntity(ModEntities.villager_converted.create(this.world));
    }

    private void updateCreaturesOnCapture(boolean fullConvert) {
        //noinspection ConstantConditions
        List<VillagerEntity> villagerEntities = this.world.getEntitiesWithinAABB(VillagerEntity.class, getVillageArea());
        if (ModEventFactory.fireVillagerCaptureEventPre(this, villagerEntities, fullConvert)) {
            return;
        }
        if (VReference.HUNTER_FACTION.equals(this.capturingFaction)) {
            List<HunterBaseEntity> hunterEntities = this.world.getEntitiesWithinAABB(HunterBaseEntity.class, getVillageArea());
            int i = Math.max(2, hunterEntities.size() / 2);
            for (HunterBaseEntity hunter : hunterEntities) {
                if (hunter instanceof ICaptureIgnore)
                    continue;
                if (i-- > 0) {
                    this.spawnVillagerReplace(hunter, true);
                }
            }
            for (int o = i; o > 0; o--) {
                this.spawnVillagerDefault(true);
            }
            for (VillagerEntity villager : villagerEntities) {
                ExtendedCreature.getSafe(villager).ifPresent(e -> e.setPoisonousBlood(true));
            }
            this.updateTrainer(false);

        }else if (VReference.HUNTER_FACTION.equals(this.controllingFaction)){
            updateTrainer(true);
            for (VillagerEntity villager : villagerEntities) {
                ExtendedCreature.getSafe(villager).ifPresent(e -> e.setPoisonousBlood(false));
            }

            if (fullConvert) {
                List<HunterBaseEntity> hunterEntities = this.world.getEntitiesWithinAABB(HunterBaseEntity.class, getVillageArea());
                for (HunterBaseEntity hunter : hunterEntities) {
                    if (hunter instanceof ICaptureIgnore)
                        continue;
                    //noinspection ConstantConditions
                    this.spawnEntity(this.getCaptureEntityForFaction(this.capturingFaction).create(this.world), hunter, true, false);
                }
            }
        } else {
            updateTrainer(true);
        }

        if (VReference.VAMPIRE_FACTION.equals(this.capturingFaction)) {
            for (VillagerEntity villager : villagerEntities) {
                if (!fullConvert) {
                    if (RNG.nextInt(2) == 1) continue;
                    PotionSanguinare.addRandom(villager, false);
                } else {
                    villager.addPotionEffect(new PotionSanguinareEffect(11));
                }
            }

        } else if(VReference.VAMPIRE_FACTION.equals(this.controllingFaction)) {
                for (VillagerEntity villager : villagerEntities) {
                    if (villager.isPotionActive(ModEffects.sanguinare))
                        villager.removePotionEffect(ModEffects.sanguinare);
                    if (fullConvert) {
                        if (villager instanceof ConvertedVillagerEntity) {
                            this.spawnVillagerReplaceForced(villager, this.capturingFaction == VReference.HUNTER_FACTION);
                        }
                    }
                }
                if (fullConvert) {
                    List<VampireBaseEntity> vampireEntities = this.world.getEntitiesWithinAABB(VampireBaseEntity.class, getVillageArea());
                    for (VampireBaseEntity vampire : vampireEntities) {
                        if (vampire instanceof ICaptureIgnore)
                            continue;
                        //noinspection ConstantConditions
                        this.spawnEntity(this.getCaptureEntityForFaction(this.capturingFaction).create(this.world), vampire, true, false);
                    }
                }
        }

        villagerEntities = this.world.getEntitiesWithinAABB(VillagerEntity.class, getVillageArea());

        for (VillagerEntity villager : villagerEntities) {
            if (villager.getVillagerData().getProfession() instanceof FactionVillagerProfession) {
                villager.setVillagerData(villager.getVillagerData().withProfession(VillagerProfession.NONE));
            }
        }
        ModEventFactory.fireVillagerCaptureEventPost(this, villagerEntities, fullConvert);
    }

    private void spawnEntity(MobEntity newEntity) {
        assert world instanceof ServerWorld;
        UtilLib.spawnEntityInWorld((ServerWorld)this.world, this.getVillageAreaReduced(), newEntity, 50, Lists.newArrayList(), SpawnReason.EVENT);
    }

    private void spawnEntity(MobEntity newEntity, MobEntity oldEntity, boolean replaceOld, boolean copyData) {
        if (copyData) {
            newEntity.copyDataFromOld(oldEntity);
        } else {
            newEntity.copyLocationAndAnglesFrom(oldEntity);
        }
        newEntity.setUniqueId(MathHelper.getRandomUUID());
        if (replaceOld) oldEntity.remove();
        assert this.world != null;
        this.world.addEntity(newEntity);
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnTaskMaster() {
        assert world instanceof ServerWorld;
        assert this.controllingFaction != null;
        EntityType<? extends ITaskMasterEntity> entity = this.controllingFaction.getVillageData().getTaskMasterEntity();
        if(entity != null) {
            ITaskMasterEntity newEntity = entity.create(this.world);
            newEntity.setHome(this.getVillageAreaReduced());
            UtilLib.spawnEntityInWorld((ServerWorld)this.world, this.getVillageAreaReduced(), (Entity) newEntity, 25, Lists.newArrayList(), SpawnReason.EVENT);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void updateTrainer(boolean toDummy) {
        List<Entity> trainer;
        EntityType<?> entityType;
        if (toDummy) {
            trainer = this.world.getEntitiesWithinAABB(HunterTrainerEntity.class, this.getVillageArea());
            entityType = ModEntities.hunter_trainer_dummy;
        } else {
            trainer = this.world.getEntitiesWithinAABB(DummyHunterTrainerEntity.class, this.getVillageArea());
            entityType = ModEntities.hunter_trainer;
        }
        for (Entity oldEntity : trainer) {
            Entity newEntity = entityType.create(this.world);
            if(newEntity == null) continue;
            newEntity.copyDataFromOld(oldEntity);
            newEntity.setUniqueId(MathHelper.getRandomUUID());
            oldEntity.remove();
            newEntity.setInvulnerable(true);
            world.addEntity(newEntity);
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

    @Override
    public @Nullable
    IFaction<?> getCapturingFaction() {
        return capturingFaction;
    }

    @Override
    public @Nullable
    IFaction<?> getControllingFaction() {
        return controllingFaction;
    }

    public void notifyNearbyPlayers(ITextComponent textComponent) {
        //noinspection ConstantConditions
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
        if (hasWorld()) this.handleUpdateTag(this.world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
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

    @OnlyIn(Dist.CLIENT)
    public float shouldRenderBeam() {
        if (!this.isComplete || isDisabled || !isInsideVillage) return 0f;
        //noinspection ConstantConditions
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

    public static class CaptureInfo implements ICaptureAttributes {
        @Nullable
        private final IFaction<?> defendingFaction;
        @Nullable
        private final IFaction<?> attackingFaction;
        private final AxisAlignedBB villageArea;
        private final BlockPos pos;
        private final boolean shouldForceTargets;

        private CaptureInfo(TotemTileEntity totem) {
            this.defendingFaction = totem.controllingFaction;
            this.attackingFaction = totem.capturingFaction;
            this.villageArea = totem.getVillageAreaReduced();
            this.pos = totem.pos;
            this.shouldForceTargets = totem.captureForceTargetTimer > VampirismConfig.BALANCE.viForceTargetTime.get();
        }

        @Nullable
        @Override
        public IFaction<?> getDefendingFaction() {
            return this.defendingFaction;
        }

        @Nullable
        @Override
        public IFaction<?> getAttackingFaction() {
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
            return this.shouldForceTargets;
        }

    }
}
