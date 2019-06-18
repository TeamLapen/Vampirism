package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IAggressiveVillager;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.EntityFactionVillager;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedVillager;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.*;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import de.teamlapen.vampirism.entity.vampire.EntityVampireFactionVillager;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.Village;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


/**
 * Central component of the Village Control system.
 * <p>
 * This tile entity is used to store the controlling faction, update the VampirismVillage instance and manages capturing progress.
 * It displays the current status and allows players to capture the village.
 */
public class TileTotem extends TileEntity implements ITickable {
    private final static Logger LOGGER = LogManager.getLogger(TileTotem.class);
    private final static int NOTIFY_DISTANCE_SQ = 40000;
    private final static String TAG = "TileTotem";
    private final static int DURATION_PHASE_1 = 60;
    /**
     * Store a dimension -> blockpos -> BoundingBox map of villages controlled by vampires. Added/Updated on update package. Removed on invalidate.
     * <p>
     * This is originally intended to store the area on clientside, but also used on server side to create a matching experience.
     * On integrated servers this is updated from both client and server (so twice), but values should just override each other
     */
    private final static HashMap<Dimension, HashMap<BlockPos, MutableBoundingBox>> vampireVillages = new HashMap<>();


    public TileTotem() {
        super(ModTiles.totem);
    }

    /**
     * Check if the given position is inside a (statically) cached list of vampire village BBs
     */
    public static boolean isInsideVampireAreaCached(Dimension dimension, BlockPos pos) { //TODO use {@link Dimension} instead of id
        HashMap<BlockPos, MutableBoundingBox> map = vampireVillages.get(dimension);
        if (map != null) {
            for (MutableBoundingBox bb : map.values()) {
                if (bb.isVecInside(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Create a aggressive copy of the given villager and replace the old instance in the world
     *
     * @return New entity
     */
    public static @Nullable
    IVillageCaptureEntity makeAggressive(EntityVillager villager, @Nullable VampirismVillage v) {
        VampirismVillageEvent.MakeAggressive event = new VampirismVillageEvent.MakeAggressive(v, villager);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            IVillageCaptureEntity aggressive = event.getAggressiveVillager();
            if (aggressive != null) {
                villager.getEntityWorld().spawnEntity((Entity) aggressive);
                villager.remove();
            }
            return aggressive;
        } else {
            EntityAggressiveVillager hunter = EntityAggressiveVillager.makeHunter(villager);
            villager.getEntityWorld().spawnEntity(hunter);
            villager.remove();
            return hunter;
        }
    }

    /**
     * Remove all cached areas for the given map/dimension
     */
    public static void clearCacheForDimension(Dimension i) {
        Map m = vampireVillages.get(i);
        if (m != null) {
            m.clear();
        }
    }

    private final BossInfoServer captureInfo = (new BossInfoServer(new TextComponentTranslation("text.vampirism.village.bossinfo.capture"), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS));
    private boolean force_village_update = true;
    private boolean isComplete;
    private int defenderMax = 0;
    private boolean insideVillage;
    @OnlyIn(Dist.CLIENT)
    private long beamRenderCounter;
    @OnlyIn(Dist.CLIENT)
    private float beamRenderScale;
    /**
     * The area covered by this totem
     */
    private AxisAlignedBB affectedArea = null;
    /**
     * Slightly smaller than {@link #affectedArea}
     */
    private AxisAlignedBB affectedAreaReduced = null;
    @Nullable
    private IPlayableFaction controllingFaction;
    private float[] baseColors = EnumDyeColor.WHITE.getColorComponentValues();
    private float[] capturingColors = EnumDyeColor.WHITE.getColorComponentValues();
    /**
     * Can be set (e.g. in world gen) to force a (immediate) faction change in the next tick. Variable is cleared afterwards
     */
    @Nullable
    private IPlayableFaction forced_faction;

    /*
    Capture relevant progress. Variables are only valid if {@link #capturingFaction} !=null
     */
    /**
     * If true, check for an hunter trainer when forcing a faction and default to hunter faction if found
     */
    private boolean forced_faction_check_trainer;
    /**
     * Count down timer for forced faction update.
     * Required for factions set in world gen, so the village is properly created beforehand
     */
    private int forced_faction_timer = 0;
    @Nullable
    private IPlayableFaction capturingFaction;
    /**
     * Phase of the capture procedure
     */
    private CAPTURE_PHASE capture_phase = null;
    /**
     * Upwards counter for the number of ticks there wasn't anyone of the capturing faction present.
     * Reset once someone is spotted
     */
    private int capture_abort_timer;
    /**
     * For final stage this is the cached number of enemies remaining before the capture is successful
     */
    private int capture_remainingEnemies_cache;
    /**
     * General purpose (phase specific) counter for the capture procedure
     */
    private int capture_timer;

    public boolean canPlayerRemoveBlock(EntityPlayer player) {
        if (player.abilities.isCreativeMode) return true;
        @Nullable IPlayableFaction faction = FactionPlayerHandler.get(player).getCurrentFaction();
        if (controllingFaction == null) {
            if (capturingFaction == null || capturingFaction.equals(faction)) {
                return true;
            }
            if (!world.isRemote)
                player.sendStatusMessage(new TextComponentTranslation("text.vampirism.village.totem_destroy.fail_other_capturing"), false);
            return false;
        } else {
            if (capturingFaction != null) {
                if (!world.isRemote)
                    player.sendStatusMessage(new TextComponentTranslation("text.vampirism.village.totem_destroy.fail_capture_in_progress"), false);
                return false;
            }
            if (controllingFaction.equals(faction)) {
                return true;
            } else {
                if (!world.isRemote)
                    player.sendStatusMessage(new TextComponentTranslation("text.vampirism.village.totem_destroy.fail_other_faction"), false);
                return false;
            }
        }
    }

    /**
     * Force change to the given faction.
     * Is performed in the next update tick.
     * Any ongoing capture is canceled
     *
     * @param newFaction
     * @param checkForHunterTrainer if set, we will default to hunter faction if there is a hunter trainer in the village
     */
    public void forceChangeFaction(IPlayableFaction newFaction, boolean checkForHunterTrainer) {
        this.forced_faction = newFaction;
        this.forced_faction_check_trainer = checkForHunterTrainer;
        this.forced_faction_timer = 20;
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getBaseColors() {
        return baseColors;
    }

    /**
     * @return 0-100. 80 if in stage 2
     */
    public int getCaptureProgress() {
        return this.capturingFaction == null ? 0 : this.capture_phase == CAPTURE_PHASE.PHASE_2 ? 80 : (int) (this.capture_timer / (float) DURATION_PHASE_1 * 80f);
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getCapturingColors() {
        return capturingColors;
    }

    @Nullable
    public IPlayableFaction getCapturingFaction() {
        return capturingFaction;
    }

    private void setCapturingFaction(@Nullable IPlayableFaction faction) {
        this.capturingFaction = faction;
        this.capturingColors = faction != null ? UtilLib.getColorComponents(faction.getColor()) : EnumDyeColor.WHITE.getColorComponentValues();
    }

    @Nullable
    public IPlayableFaction getControllingFaction() {
        return controllingFaction;
    }

    private void setControllingFaction(@Nullable IPlayableFaction faction) {
        this.controllingFaction = faction;
        this.baseColors = faction != null ? UtilLib.getColorComponents(faction.getColor()) : EnumDyeColor.WHITE.getColorComponentValues();
    }

    @Nullable
    public ITextComponent getDisplayName() { //TODO the method doesnt exist anymore
        if (capturingFaction != null) {
            return new TextComponentTranslation("text.vampirism.village.faction_capturing", new TextComponentTranslation(capturingFaction.getTranslationKeyPlural()));
        } else if (controllingFaction != null) {
            return new TextComponentTranslation("text.vampirism.village.faction_controlling", new TextComponentTranslation(controllingFaction.getTranslationKeyPlural()));
        } else {
            return new TextComponentTranslation("text.vampirism.village.neutral");
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Nonnull
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = new NBTTagCompound();
        write(nbt);
        nbt.putIntArray("village_bb", UtilLib.bbToInt(getAffectedArea()));
        return nbt;
    }

    @Override
    public void handleUpdateTag(@Nonnull NBTTagCompound tag) {
        read(tag);
        if (tag.contains("village_bb")) {
            if (controllingFaction == VReference.VAMPIRE_FACTION) {
                MutableBoundingBox bb = new MutableBoundingBox(tag.getIntArray("village_bb"));
                registerVampireArea(bb); //Replaces old area if different
            } else {
                unregisterVampireArea();
            }
        }
        world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    /**
     * Initiate and prepare capture procedure. Should only be called if there isn't a capture in progress already.
     *
     * @param faction The attacking faction
     */
    public void initiateCapture(@Nonnull IPlayableFaction faction, EntityPlayer player) {
        if (capturingFaction != null) return;
        if (faction.equals(controllingFaction)) return;
        updateTotem();
        if (!insideVillage) {
            player.sendMessage(new TextComponentTranslation("text.vampirism.village.no_near_village"));
            return;
        }
        capture_abort_timer = 0;
        capturingFaction = faction;
        captureInfo.setName(new TextComponentTranslation("text.vampirism.village.bossinfo.capture"));
        captureInfo.setColor(BossInfo.Color.YELLOW);
        captureInfo.setPercent(0F);
        defenderMax = 0;

        if (this.controllingFaction == null) {
            this.capture_phase = CAPTURE_PHASE.PHASE_1_NEUTRAL;
            notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.neutral_village_under_attack", new TextComponentTranslation(faction.getTranslationKeyPlural())));
        } else {
            this.capture_phase = CAPTURE_PHASE.PHASE_1_OPPOSITE;
            notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.faction_village_under_attack", new TextComponentTranslation(this.controllingFaction.getTranslationKey()), new TextComponentTranslation(faction.getTranslationKeyPlural())));
        }
        this.capture_timer = 0;
        force_village_update = true;
        this.markDirty();
        if (!world.isRemote && capturingFaction == VReference.VAMPIRE_FACTION) {
            List<EntityVillager> villager = this.world.getEntitiesWithinAABB(EntityVillager.class, getAffectedArea());
            for (EntityVillager v : villager) {
                if (v instanceof EntityFactionVillager) continue;
                if (v.getRNG().nextInt(3) == 0) {
                    makeAggressive(v, this.getVillage());
                }
            }
        }
    }

    @Override
    public void invalidate() {//TODO ?
        super.invalidate();
        unregisterVampireArea();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
        if (this.controllingFaction == VReference.VAMPIRE_FACTION) {
            registerVampireArea(new MutableBoundingBox(UtilLib.bbToInt(getAffectedArea())));
        } else {
            unregisterVampireArea();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());

    }


    @Override
    public void remove() {
        VampirismVillage v = getVillage();
        if (v != null) {
            v.removeTotemAndReset(this.pos);
        }
        updateBossinfoPlayers(null);
        super.remove();
    }

    @Override
    public void read(NBTTagCompound compound) {
        super.read(compound);

        String controlling = compound.getString("controlling");
        String capturing = compound.getString("capturing");
        IPlayableFaction controllingFaction = null;
        IPlayableFaction capturingFaction = null;
        if (!"".equals(controlling)) {
            IFaction f = VampirismAPI.factionRegistry().getFactionByName(controlling);
            if (!(f instanceof IPlayableFaction)) {
                LOGGER.warn("Stored faction %s does not exist or is not playable", controlling);
            } else {
                controllingFaction = (IPlayableFaction) f;
            }
        }
        if (!"".equals(capturing)) {
            IFaction f = VampirismAPI.factionRegistry().getFactionByName(capturing);
            if (!(f instanceof IPlayableFaction)) {
                LOGGER.warn("Stored faction %s does not exist or is not playable", capturing);
            } else {
                capturingFaction = (IPlayableFaction) f;
            }
        }
        this.setControllingFaction(controllingFaction);
        this.setCapturingFaction(capturingFaction);
        if (capturingFaction != null) {
            this.capture_timer = compound.getInt("timer");
            this.capture_abort_timer = compound.getInt("abort_timer");
            this.capture_remainingEnemies_cache = compound.getInt("rem_enem");
            this.capture_phase = CAPTURE_PHASE.valueOf(compound.getString("phase"));
        }
        force_village_update = true;
    }

    /**
     * Client side update if rendering has to be changed
     */
    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            this.updateTotem();
            return true;
        } else {
            return super.receiveClientEvent(id, type);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float shouldBeamRender() {
        if (!this.isComplete) {
            return 0.0F;
        } else {
            int i = (int) (this.world.getGameTime() - this.beamRenderCounter);
            this.beamRenderCounter = this.world.getGameTime();

            if (i > 1) {
                this.beamRenderScale -= (float) i / 40.0F;

                if (this.beamRenderScale < 0.0F) {
                    this.beamRenderScale = 0.0F;
                }
            }

            this.beamRenderScale += 0.025F;

            if (this.beamRenderScale > 1.0F) {
                this.beamRenderScale = 1.0F;
            }

            return this.beamRenderScale;
        }
    }

    @Override
    public void tick() {
        int time = (int) this.world.getGameTime();

        //Remote ----------------------------------
        if (this.world.isRemote) {
            if (time % 10 == 4) {
                this.updateTotem();
            }
            if (this.capturingFaction != null && time % 40 == 9) {
                this.capture_timer++;
            }
            if (controllingFaction != null && time % 10 == 7) {
                VampLib.proxy.getParticleHandler().spawnParticles(this.world, ModParticles.GENERIC_PARTICLE, this.pos.getX(), this.pos.getY(), this.pos.getZ(), 3, 30, this.world.rand, 4, 20, controllingFaction.getColor());
            }
            return;
        }
        //Server -----------------------------------
        else {
            //Update forced faction if set
            if (forced_faction != null) {
                if (forced_faction_timer > 0) {
                    forced_faction_timer--;
                } else {
                    if (forced_faction != VReference.HUNTER_FACTION && forced_faction_check_trainer) {
                        List<EntityHunterTrainer> t = world.getEntitiesWithinAABB(EntityHunterTrainer.class, getAffectedArea());
                        if (t.size() > 0) {
                            forced_faction = VReference.HUNTER_FACTION;
                        }
                    }
                    if (this.getVillage() == null) {
                        LOGGER.warn("Freshly generated totem cannot find village");
                    } else {
                        this.capturingFaction = forced_faction;
                        completeCapture(false);
                    }
                    forced_faction = null;
                }
            }

            //Update  totem
            if (force_village_update || time % 80 == 0L) {
                this.updateTotem();
                force_village_update = false;
            }
            if (time % 1000 == 0) {
                this.updateAffectedArea(); //In case village size has changed
            }
            //Handle capture
            if (this.capturingFaction != null) {
                if (time % 40 == 9) {
                    List<Entity> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, getAffectedArea());
                    updateBossinfoPlayers(entities);
                    int attacker = 0; //Includes players
                    int attackerPlayer = 0;
                    int defender = 0;//Includes player
                    int defenderPlayer = 0;
                    int neutral = 0;
                    float attackStrength = 0;
                    float defenseStrength = 0;
                    for (Entity e : entities) {
                        IFaction f = VampirismAPI.factionRegistry().getFaction(e);
                        if (f == null) continue;
                        if (e instanceof EntityHunterTrainer) continue;
                        if (this.capturingFaction.equals(f)) {
                            attacker++;
                            attackStrength++;
                            if (e instanceof EntityPlayer) {
                                attackerPlayer++;
                                attackStrength += FactionPlayerHandler.get((EntityPlayer) e).getCurrentLevelRelative();
                            }
                        } else if (controllingFaction != null && controllingFaction.equals(f)) {
                            defender++;
                            if (e instanceof EntityPlayer) {
                                defenderPlayer++;
                                defenseStrength += FactionPlayerHandler.get((EntityPlayer) e).getCurrentLevelRelative();
                            }
                            if (e instanceof EntityConvertedVillager) {
                                defenseStrength += 0.5f; //Converted villagers are useless
                            } else if (e instanceof IAggressiveVillager) {
                                defenseStrength += 0.7f;
                            } else if (e instanceof EntityVillager) {
                                defenseStrength += 0.4f;
                            } else {
                                defenseStrength++;
                            }
                        } else {
                            neutral++;
                        }
                    }
                    //VampirismMod.log.t("Capture progress update: Timer %d [%s], Abort Timer %s. Attacker %d(%d) - %s. Defender %d(%d) - %s. Neutral %d", capture_timer, capture_phase, capture_abort_timer, attacker, attackerPlayer, attackStrength, defender, defenderPlayer, defenseStrength, neutral);
                    if (attackerPlayer == 0) {
                        this.capture_abort_timer++;
                    } else {
                        capture_abort_timer = 0;
                        capture_timer++;
                    }

                    if (this.capture_abort_timer > 7) {
                        this.abortCapture(true);
                    } else {
                        switch (capture_phase) {
                            case PHASE_1_NEUTRAL:
                                if (capture_timer >= DURATION_PHASE_1) {
                                    capture_timer = 1;
                                    this.capture_phase = CAPTURE_PHASE.PHASE_2;
                                    this.markDirty();
                                }
                                break;
                            case PHASE_1_OPPOSITE:
                                if (capture_timer >= DURATION_PHASE_1) {
                                    capture_timer = 1;
                                    this.capture_phase = CAPTURE_PHASE.PHASE_2;
                                    this.markDirty();
                                    notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.almost_captured", defender));
                                } else {
                                    if (capture_timer % 2 == 0) {
                                        if (attackStrength * 1.1f > defenseStrength) {
                                            spawnCaptureCreature(false);
                                        } else if (attackStrength < defenseStrength * 1.1f) {
                                            spawnCaptureCreature(true);
                                        }
                                    }
                                }
                                break;
                            case PHASE_2:
                                if (defender == 0) {
                                    capture_timer++;
                                    if (capture_timer > 4) {
                                        this.completeCapture(true);
                                    }
                                } else {
                                    capture_timer = 1;
                                }
                                break;
                            default:
                                break;
                        }
                        handleBossBar(capture_phase, defender);
                    }
                }
            } else {
                //Normal village live
                if (this.controllingFaction != null && time % 512 == 0) {
                    VampirismVillage village = this.getVillage();
                    if (village != null) {
                        List<EntityVillager> l = this.world.getEntitiesWithinAABB(EntityVillager.class, getAffectedArea());
                        if (l.size() > 0) {
                            EntityVillager seed = l.get(l.get(0).getRNG().nextInt(l.size()));
                            int max = (int) Math.min(village.getVillage().getNumVillageDoors() * 1.5f, 30);
                            if (l.size() < max) {
                                if (seed.getRNG().nextInt(15) == 0) {
                                    if (controllingFaction.equals(VReference.HUNTER_FACTION)) {
                                        spawnVillagerInVillage(new EntityHunterFactionVillager(this.world), seed, true);
                                    } else if (controllingFaction.equals(VReference.VAMPIRE_FACTION)) {
                                        spawnVillagerInVillage(new EntityVampireFactionVillager(this.world), seed, false);
                                    }
                                } else {
                                    boolean isVampire = this.controllingFaction == VReference.VAMPIRE_FACTION && seed.getRNG().nextBoolean();
                                    VampirismVillageEvent.SpawnNewVillager event = new VampirismVillageEvent.SpawnNewVillager(village, seed, isVampire);
                                    MinecraftForge.EVENT_BUS.post(event);
                                    if (event.getResult() != Event.Result.DENY) {
                                        EntityVillager newVillager;
                                        if (event.getResult() == Event.Result.ALLOW && event.getNewVillager() != null) {
                                            newVillager = event.getNewVillager();
                                        } else {
                                            newVillager = new EntityVillager(this.world);
                                            newVillager.copyLocationAndAnglesFrom(seed);
                                            newVillager.setGrowingAge(-24000);
                                            seed.setGrowingAge(6000);
                                        }
                                        if (event.isWillBeVampire()) {
                                            IConvertedCreature converted = ExtendedCreature.get(newVillager).makeVampire(); //Already spawns the creature in the world
                                        } else {
                                            this.spawnVillagerInVillage(newVillager, seed, this.controllingFaction == VReference.HUNTER_FACTION);
                                        }
                                    }

                                }

                            }
                            int defenderNumMax = Math.min(6, village.getVillage().getNumVillageDoors() / 5);
                            List<EntityLiving> guards = new ArrayList<>();
                            ResourceLocation entityId = null;
                            if (this.controllingFaction.equals(VReference.HUNTER_FACTION)) {
                                guards = this.world.getEntitiesWithinAABB(EntityHunterBase.class, getAffectedArea());
                                entityId = getEntityForFaction(this.controllingFaction);
                            } else if (this.controllingFaction.equals(VReference.VAMPIRE_FACTION)) {
                                guards = this.world.getEntitiesWithinAABB(EntityVampireBase.class, getAffectedArea());
                                entityId = getEntityForFaction(this.controllingFaction);
                            }
                            if (entityId != null && defenderNumMax > guards.size()) {
                                Entity e = EntityType.create(world, entityId);
                                if (e != null && !spawnEntityInVillage(e, null)) {
                                    e.remove();
                                }
                            }
                        }

                    }
                }
            }
        }


    }

    /**
     * Check if the totem is complete and allowed to exist in this village.
     * On client side this only updates is complete
     */
    public void updateTotem() {
        boolean complete = this.world.getBlockState(this.pos.down()).getBlock().equals(ModBlocks.totem_base);
        if (complete != isComplete) {
            //TODO
        }
        isComplete = complete;
        if (this.world.isRemote) return;
        if (isComplete) {
            @Nullable VampirismVillage village = getVillage();
            boolean insideVillageNew = village != null;
            if (insideVillageNew != insideVillage) {
                //TODO
                if (!insideVillageNew) {
                    this.capturingFaction = null;
                    this.controllingFaction = null;
                    this.capture_timer = 0;
                    this.markDirty();
                }
            }

            insideVillage = insideVillageNew;
            if (insideVillage) {
                if (!updateVillage(village)) {
                    //There is another totem. Destroy this one
                    this.world.destroyBlock(this.getPos(), true);
                    return;//Totem has been destroyed
                }

                //Destroy all (breakable) blocks above
                int x = pos.getX();
                int y = pos.getY() + 1;
                int z = pos.getZ();
                BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();
                for (int i = y; i < 256; i++) {
                    IBlockState blockState = this.world.getBlockState(pos1.setPos(x, i, z));
                    if (!blockState.getBlock().isAir(blockState, world, pos1) && blockState.getMaterial() != Material.GLASS) {
                        if (blockState.getBlockHardness(world, pos1) != -1F) {//Don't destroy unbreakable blocks like bedrock
                            this.world.destroyBlock(pos1, false);
                        }
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.putString("controlling", controllingFaction == null ? "" : controllingFaction.name());
        compound.putString("capturing", capturingFaction == null ? "" : capturingFaction.name());
        if (capturingFaction != null) {
            compound.putInt("timer", capture_timer);
            compound.putInt("abort_timer", capture_abort_timer);
            compound.putString("phase", capture_phase.name());
            compound.putInt("rem_enem", capture_remainingEnemies_cache);
        }
        return super.write(compound);
    }

    private void abortCapture(boolean notifyPlayer) {
        this.setCapturingFaction(null);
        force_village_update = true;
        this.markDirty();
        //VampirismMod.log.t("Abort capture");
        informEntitiesAboutCaptureStop();
        if (notifyPlayer)
            notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.village_capture_aborted"));
        updateBossinfoPlayers(null);
        defenderMax = 0;
    }

    private void completeCapture(boolean notifyPlayer) {
        if (!this.world.isRemote)
            this.updateCreaturesOnCapture();
        if (capturingFaction == null) {
            LOGGER.warn("Completing null capture. That should not happen");
            return;
        }
        this.setControllingFaction(capturingFaction);
        this.setCapturingFaction(null);
        force_village_update = true;
        this.markDirty();
        //VampirismMod.log.t("Completed capture");
        informEntitiesAboutCaptureStop();
        if (notifyPlayer)
            notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.village_captured_by", new TextComponentTranslation(controllingFaction.getTranslationKeyPlural())));
        updateBossinfoPlayers(null);
    }

    @Nonnull
    private AxisAlignedBB getAffectedArea() {
        if (affectedArea == null) {
            updateAffectedArea();
        }
        return affectedArea;
    }

    @Nonnull
    private AxisAlignedBB getAffectedAreaReduced() {
        if (this.affectedAreaReduced == null) {
            updateAffectedArea();
        }
        return affectedAreaReduced;
    }

    @Nullable
    private ResourceLocation getEntityForFaction(@Nonnull IFaction f) {
        //TODO implement weighted random
        if (f == VReference.HUNTER_FACTION) {
            return new ResourceLocation("vampirism:vampire_hunter");
        } else if (f == VReference.VAMPIRE_FACTION) {
            return new ResourceLocation("vampirism:vampire");
        }
        return null;
    }

    @Nullable
    private VampirismVillage getVillage() {
        return this.world.isRemote ? null : VampirismVillageHelper.getNearestVillage(this.world, this.pos, 10);
    }

    private void handleBossBar(CAPTURE_PHASE phase, int defenderLeft) {
        if (phase == CAPTURE_PHASE.PHASE_1_NEUTRAL || phase == CAPTURE_PHASE.PHASE_1_OPPOSITE) {
            captureInfo.setPercent(this.capture_timer / (float) DURATION_PHASE_1);
        } else if (phase == CAPTURE_PHASE.PHASE_2) {
            if (defenderMax != 0) {
                if (defenderLeft > defenderMax) defenderMax = defenderLeft;
                captureInfo.setPercent((float) defenderLeft / (float) defenderMax);
            } else {
                defenderMax = defenderLeft;
                captureInfo.setName(new TextComponentTranslation("text.vampirism.village.defender_remaining"));
                captureInfo.setColor(BossInfo.Color.WHITE);
            }
        }
    }

    private void informEntitiesAboutCaptureStop() {
        List<EntityCreature> list = this.world.getEntitiesWithinAABB(EntityCreature.class, getAffectedArea());
        for (EntityCreature e : list) {
            if (e instanceof IVillageCaptureEntity) {
                ((IVillageCaptureEntity) e).stopVillageAttackDefense();
            }
        }
    }

    private void notifyNearbyPlayers(ITextComponent msg) {
        final BlockPos pos = this.getPos();
        for (EntityPlayer p : this.world.getPlayers(EntityPlayer.class, input -> input != null && input.getDistanceSq(pos) < NOTIFY_DISTANCE_SQ)) {
            p.sendStatusMessage(msg, true);
        }
    }

    private void registerVampireArea(MutableBoundingBox box) {
        HashMap<BlockPos, MutableBoundingBox> map = vampireVillages.get(this.world.getDimension());
        if (map == null) {
            map = new HashMap<>();
            vampireVillages.put(this.world.getDimension(), map);
        }
        map.put(this.getPos(), box);

    }

    /**
     * Try to spawn an appropriate creature and set AI tasks
     *
     * @param attack Attack or defense
     */
    private void spawnCaptureCreature(boolean attack) {
        ResourceLocation id;
        if (attack) {
            if (this.capturingFaction == null) return;
            id = getEntityForFaction(this.capturingFaction);
        } else {
            if (this.controllingFaction == null) return;
            id = getEntityForFaction(this.controllingFaction);
        }
        if (id == null) {
            LOGGER.warn("No village capture entity registered for %s", attack ? this.capturingFaction : this.controllingFaction);
            return;
        }
        Entity e = EntityType.create(world, id);
        if (e instanceof EntityVampireBase) {
            ((EntityVampireBase) e).setSpawnRestriction(EntityVampireBase.SpawnRestriction.SIMPLE);
        }
        if (e != null && !UtilLib.spawnEntityInWorld(world, this.getAffectedAreaReduced(), e, 50, world.getPlayers(EntityPlayer.class, EntitySelectors.NOT_SPECTATING))) {
            e.remove();
            e = null;
        }
        if (e instanceof IVillageCaptureEntity) {
            if (attack) {
                ((IVillageCaptureEntity) e).attackVillage(this.getAffectedArea());
            } else {
                ((IVillageCaptureEntity) e).defendVillage(this.getAffectedArea());
            }
        } else if (e != null) {
            LOGGER.warn("Creature registered for village capture does not implement IVillageCaptureEntity");
        } else {
            LOGGER.info("Failed to spawn creature");
        }

    }

    /**
     * Spawn the given new entity in the world/village
     * if entityToReplace == null the Entity newEntity is spawned at a random position around the village center otherwise the new newEntity copies the location and angles from entityToReplace
     *
     * @param newEntity       new Entity to spawn
     * @param entityToReplace old Entity to be replaced
     * @returns false if spawn is not possible
     */
    private boolean spawnEntityInVillage(@Nonnull Entity newEntity, @Nullable Entity entityToReplace) {
        if (entityToReplace != null) {
            newEntity.copyLocationAndAnglesFrom(entityToReplace);
        } else {
            if (!(newEntity instanceof EntityLiving)) return false;
            VampirismVillage village = this.getVillage();
            if (village == null) return false;
            BlockPos pos = findRandomSpawnPos(village.getVillage(), (EntityLiving) newEntity);
            if (pos == null) return false;
            if (!world.isAirBlock(pos)) pos = pos.add(0, 1, 0);
            newEntity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        }
        if (entityToReplace != null) world.removeEntity(entityToReplace);
        world.spawnEntity(newEntity);
        return true;
    }

    private BlockPos findRandomSpawnPos(Village village, EntityLiving entity) {
        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos = village.getCenter().add(this.world.rand.nextInt(16) - 8, this.world.rand.nextInt(6) - 3, this.world.rand.nextInt(16) - 8);
            if (village.isBlockPosWithinSqVillageRadius(blockpos)) {
                if (entity != null) {
                    if (entity.canSpawn(this.world, false) && entity.isNotColliding(this.world)) {
                        return blockpos;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Spawn the given new villager in the world/village
     * by using {@link TileTotem#spawnEntityInVillage(Entity, Entity)} spawning new Villager
     *
     * @param newVillager     new Entity to spawn
     * @param entityToReplace old Entity to bew replaced
     * @param poisonousBlood  if the villager should have poisonous blood
     * @return false if spawn is not possible
     */
    private boolean spawnVillagerInVillage(@Nonnull EntityVillager newVillager, @Nullable Entity entityToReplace, boolean poisonousBlood) {
        if (!spawnEntityInVillage(newVillager, entityToReplace)) return false;
        if (entityToReplace instanceof EntityVillager) {
            newVillager.setHomePosAndDistance(((EntityVillager) entityToReplace).getHomePosition(), (int) ((EntityVillager) entityToReplace).getMaximumHomeDistance());
        } else {
            VampirismVillage village = this.getVillage();
            if (village == null) return false;
            newVillager.setHomePosAndDistance(village.getVillage().getCenter(), village.getVillage().getVillageRadius());
        }
        newVillager.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(newVillager)), null, null);
        ExtendedCreature.get(newVillager).setPoisonousBlood(poisonousBlood);
        return true;
    }

    private void unregisterVampireArea() {
        HashMap<BlockPos, MutableBoundingBox> map = vampireVillages.get(this.world.getDimension());
        if (map != null) {
            map.remove(this.getPos());
        }
    }

    private void updateAffectedArea() {
        @Nullable VampirismVillage v = getVillage();
        AxisAlignedBB box;
        BlockPos b = v == null ? this.pos : v.getVillage().getCenter();
        int r = v == null ? 30 : v.getVillage().getVillageRadius() + 15;
        box = new AxisAlignedBB(b.getX() - r, b.getY() - 10, b.getZ() - r, b.getX() + r, b.getY() + 30, b.getZ() + r);

        if (!box.contains(new Vec3d(this.pos))) {
            LOGGER.warn("Totem outside of calculated village bb %s %s", box, this.pos);
        }
        double xLength = box.maxX - box.minX;
        double zLength = box.maxZ - box.minZ;
        double cX = 0, cZ = 0;
        if (xLength > 100) {
            cX = 100 - xLength;
        } else if (xLength < 20) {
            cX = 20 - xLength;
        }
        if (zLength > 100) {
            cZ = 100 - zLength;
        } else if (zLength < 20) {
            cZ = 20 - zLength;
        }
        affectedArea = box.grow(cX / 2d, 0, cZ / 2d); //Ensure a maximum and minimum size of the village area. Also set y limits to +-10
        affectedAreaReduced = affectedArea.grow(-10, 0, -10);
        if (!world.isRemote) {
            this.markDirty();
        }
    }

    /**
     * Update the bossbar to only include players that are in the given list.
     * If list is null, remove all players
     *
     * @param includedPlayerEntities List of player entities to be included. May contain other non player entities
     */
    private void updateBossinfoPlayers(@Nullable List<Entity> includedPlayerEntities) {
        Collection<EntityPlayerMP> bossbar = new HashSet<>(captureInfo.getPlayers());
        if (includedPlayerEntities != null) {
            for (Entity e : includedPlayerEntities) {
                if (e instanceof EntityPlayerMP) {
                    if (!bossbar.remove(e)) {
                        captureInfo.addPlayer((EntityPlayerMP) e);
                    }
                }
            }
        }
        for (EntityPlayerMP p : bossbar) {
            captureInfo.removePlayer(p);
        }
    }

    /**
     * handles entities at the end of a complete capture
     */
    private void updateCreaturesOnCapture() {
        List<EntityVillager> villager = this.world.getEntitiesWithinAABB(EntityVillager.class, getAffectedArea());
        if (capturingFaction == VReference.HUNTER_FACTION) {
            List<EntityHunterBase> hunter = this.world.getEntitiesWithinAABB(EntityHunterBase.class, getAffectedArea());
            if (controllingFaction == VReference.VAMPIRE_FACTION) {
                int i = Math.max(2, hunter.size() / 2);
                if (hunter.size() > 0) {
                    for (EntityHunterBase e : hunter) {
                        if (i-- > 0) {
                            spawnVillagerInVillage(new EntityVillager(this.world), e, true);
                        }
                    }
                }
                for (int o = i; o > 0; o--) {
                    spawnVillagerInVillage(new EntityVillager(this.world), null, true);
                }

            } else {
                for (EntityVillager e : villager) {
                    ExtendedCreature.get(e).setPoisonousBlood(true);
                }
            }
            List<EntityHunterTrainerDummy> huntertrainerdummy = this.world.getEntitiesWithinAABB(EntityHunterTrainerDummy.class, getAffectedArea());
            for (EntityHunterTrainerDummy e : huntertrainerdummy) {
                EntityHunterTrainer trainer = new EntityHunterTrainer(this.world);
                trainer.copyLocationAndAnglesFrom(e);
                trainer.setHome(e.getHome());
                world.removeEntity(e);
                world.spawnEntity(trainer);
            }
            spawnVillagerInVillage(new EntityHunterFactionVillager(this.world), null, false);
        } else if (capturingFaction == VReference.VAMPIRE_FACTION) {
            for (EntityVillager e : villager) {
                ExtendedCreature.get(e).setPoisonousBlood(false);
                if (e.getRNG().nextInt(2) == 1) continue;
                PotionSanguinare.addRandom(e, false);
            }
            List<EntityHunterTrainer> huntertrainer = this.world.getEntitiesWithinAABB(EntityHunterTrainer.class, getAffectedArea());
            for (EntityHunterTrainer e : huntertrainer) {
                EntityHunterTrainerDummy dummy = new EntityHunterTrainerDummy(this.world);
                dummy.copyLocationAndAnglesFrom(e);
                dummy.setHome(e.getHome());
                world.removeEntity(e);
                world.spawnEntity(dummy);
            }
            spawnVillagerInVillage(new EntityVampireFactionVillager(this.world), null, false);
        }
    }

    /**
     * Update the village with this totems information
     *
     * @param village
     * @return False if there is another totem
     */
    private boolean updateVillage(@Nonnull VampirismVillage village) {
        BlockPos totemLoc = village.getTotemLocation();
        if (totemLoc != null) {
            if (!totemLoc.equals(this.getPos())) {
                return false;
            }
        }
        village.registerTotem(pos);
        village.setControllingFaction(this.controllingFaction);
        village.setUnderAttack(this.capturingFaction != null);
        return true;
    }

    private enum CAPTURE_PHASE {
        PHASE_1_NEUTRAL, PHASE_1_OPPOSITE, PHASE_2
    }
}
