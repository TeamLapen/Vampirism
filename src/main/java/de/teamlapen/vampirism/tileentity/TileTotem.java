package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.EntityVampirism;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBase;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


/**
 * Central component of the Village Control system.
 * <p>
 * This tile entity is used to store the controlling faction, update the VampirismVillage instance and manages capturing progress.
 * It displays the current status and allows players to capture the village.
 */
public class TileTotem extends TileEntity implements ITickable {
    private final static int NOTIFY_DISTANCE_SQ = 10000;
    private final static String TAG = "TileTotem";
    private final static int DURATION_PHASE_1 = 60;
    private boolean force_village_update = false;
    private boolean isComplete;
    private final BossInfoServer captureInfo = (new BossInfoServer(new TextComponentTranslation("text.vampirism.village.bossinfo.capture"), BossInfo.Color.YELLOW, BossInfo.Overlay.PROGRESS));

    private boolean insideVillage;

    @SideOnly(Side.CLIENT)
    private long beamRenderCounter;
    @SideOnly(Side.CLIENT)
    private float beamRenderScale;
    private AxisAlignedBB affectedArea = null;
    @Nullable
    private IPlayableFaction controllingFaction;
    private float[] baseColors = EnumDyeColor.WHITE.getColorComponentValues();
    private float[] capturingColors = EnumDyeColor.WHITE.getColorComponentValues();

    @Nullable
    private IPlayableFaction capturingFaction;
    /**
     * Phase of the capture procedure
     */
    private CAPTURE_PHASE capture_phase = null;

    /*
    Capture relevant progress. Variables are only valid if {@link #capturingFaction} !=null
     */
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
        if (player.capabilities.isCreativeMode) return true;
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

    public int getCaptureProgress() {
        return this.capturingFaction == null ? 0 : this.capture_phase == CAPTURE_PHASE.PHASE_2 ? 80 : (int) (this.capture_timer / (float) DURATION_PHASE_1 * 80f);
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
        if(!insideVillage) {
            player.sendMessage(new TextComponentTranslation("text.vampirism.village.no_near_village"));
            return;
        }
        capturingFaction = faction;
        captureInfo.setPercent(0F);

        if (this.controllingFaction == null) {
            this.capture_phase = CAPTURE_PHASE.PHASE_1_NEUTRAL;
            notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.neutral_village_under_attack", new TextComponentTranslation(faction.getUnlocalizedNamePlural())));
        } else {
            this.capture_phase = CAPTURE_PHASE.PHASE_1_OPPOSITE;
            notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.faction_village_under_attack", new TextComponentTranslation(this.controllingFaction.getUnlocalizedName()), new TextComponentTranslation(faction.getUnlocalizedNamePlural())));
        }
        this.capture_timer = 0;
        force_village_update = true;
        this.markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
    }

    @SideOnly(Side.CLIENT)
    public float[] getBaseColors() {
        return baseColors;
    }

    public void onRemoved() {
        VampirismVillage v = getVillage();
        if (v != null) {
            v.removeTotemAndReset(this.pos);
        }
        removePlayerFromBossInfo();
    }

    @SideOnly(Side.CLIENT)
    public float[] getCapturingColors() {
        return capturingColors;
    }

    @Nullable
    public IPlayableFaction getCapturingFaction() {
        return capturingFaction;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        String controlling = compound.getString("controlling");
        String capturing = compound.getString("capturing");
        IPlayableFaction controllingFaction = null;
        IPlayableFaction capturingFaction = null;
        if (!"".equals(controlling)) {
            IFaction f = VampirismAPI.factionRegistry().getFactionByName(controlling);
            if (!(f instanceof IPlayableFaction)) {
                VampirismMod.log.w("TileTotem", "Stored faction %s does not exist or is not playable", controlling);
            } else {
                controllingFaction = (IPlayableFaction) f;
            }
        }
        if (!"".equals(capturing)) {
            IFaction f = VampirismAPI.factionRegistry().getFactionByName(capturing);
            if (!(f instanceof IPlayableFaction)) {
                VampirismMod.log.w("TileTotem", "Stored faction %s does not exist or is not playable", capturing);
            } else {
                capturingFaction = (IPlayableFaction) f;
            }
        }
        this.setControllingFaction(controllingFaction);
        this.setCapturingFaction(capturingFaction);
        if (capturingFaction != null) {
            this.capture_timer = compound.getInteger("timer");
            this.capture_abort_timer = compound.getInteger("abort_timer");
            this.capture_remainingEnemies_cache = compound.getInteger("rem_enem");
            this.capture_phase = CAPTURE_PHASE.valueOf(compound.getString("phase"));
        }
        force_village_update=true;
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
    @Override
    public ITextComponent getDisplayName() {
        if (capturingFaction != null) {
            return new TextComponentTranslation("text.vampirism.village.faction_capturing_progress", new TextComponentTranslation(capturingFaction.getUnlocalizedNamePlural()), getCaptureProgress());
        } else if (controllingFaction != null) {
            return new TextComponentTranslation("text.vampirism.village.faction_controlling", new TextComponentTranslation(controllingFaction.getUnlocalizedNamePlural()));
        } else {
            return new TextComponentTranslation("text.vampirism.village.neutral");
        }

    }

    @Override
    public void update() {
        int time = (int) this.world.getTotalWorldTime();

        if (this.world.isRemote) {
            if (this.capturingFaction != null && time % 40 == 9) {
                this.capture_timer++;
            }

            return;
        }
        if (force_village_update || time % 80 == 0L) {
            this.updateTotem();
            force_village_update = false;
        }
        //Handle capture
        if (this.capturingFaction != null && time % 40 == 9) {
            removePlayerFromBossInfo();
            List<Entity> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, getAffectedArea());
            int attacker = 0; //Includes players
            int attackerPlayer = 0;
            int defender = 0;//Includes player
            int defenderPlayer = 0;
            int neutral = 0;
            float attackStrength = 1;
            float defenseStrength = 1;
            for (Entity e : entities) {
                IFaction f = VampirismAPI.factionRegistry().getFaction(e);
                if (f == null) continue;
                if (this.capturingFaction.equals(f)) {
                    attacker++;
                    if (e instanceof EntityPlayer) {
                        attackerPlayer++;
                        attackStrength += FactionPlayerHandler.get((EntityPlayer) e).getCurrentLevelRelative();
                        captureInfo.addPlayer((EntityPlayerMP) e);
                    }
                } else if (controllingFaction != null && controllingFaction.equals(f)) {
                    defender++;
                    if (e instanceof EntityPlayer) {
                        defenderPlayer++;
                        defenseStrength += FactionPlayerHandler.get((EntityPlayer) e).getCurrentLevelRelative();
                        captureInfo.addPlayer((EntityPlayerMP) e);
                    }
                } else {
                    neutral++;
                }
            }
            VampirismMod.log.t("Capture progress update: Timer %d [%s], Abort Timer %s. Attacker %d(%d) - %s. Defender %d(%d) - %s. Neutral %d", capture_timer, capture_phase, capture_abort_timer, attacker, attackerPlayer, attacker * attackStrength, defender, defenderPlayer, defender * defenseStrength, neutral);
            if (attackerPlayer == 0) {
                this.capture_abort_timer++;
            } else {
                capture_abort_timer = 0;
                capture_timer++;
            }

            if (this.capture_abort_timer > 7) {
                this.abortCapture();
            }

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
                            if (attacker * attackStrength * 1.1f > defender * defenseStrength) {
                                spawnCreature(false);
                            } else if (attacker * attackStrength < defender * defenseStrength * 1.1f) {
                                spawnCreature(true);
                            }
                        }

                    }
                    break;
                case PHASE_2:
                    if (defender == 0) {
                        capture_timer++;
                        if (capture_timer > 4) {
                            this.completeCapture();
                        }
                    } else {
                        capture_timer = 1;
                    }
                    break;
                default:
                    break;
            }
            captureInfo.setPercent((float) getCaptureProgress() / 100);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        //TODO client processing
    }

    public void updateTotem() {
        if (this.world.isRemote)return;
        boolean complete = this.world.getBlockState(this.pos.down()).getBlock().equals(ModBlocks.totem_base);
        if (complete != isComplete) {
            //TODO
        }
        isComplete = complete;
        if (isComplete) {
            @Nullable VampirismVillage village = getVillage();
            boolean insideVillageNew = village != null;
            if (insideVillageNew != insideVillage) {
                //TODO
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
                int y = pos.getY()+1;
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

    @SideOnly(Side.CLIENT)
    public float shouldRenderBeam() {
        if (!this.isComplete || !this.insideVillage) {
            return 0.0F;
        } else {
            int i = (int) (this.world.getTotalWorldTime() - this.beamRenderCounter);
            this.beamRenderCounter = this.world.getTotalWorldTime();

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
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("controlling", controllingFaction == null ? "" : controllingFaction.name());
        compound.setString("capturing", capturingFaction == null ? "" : capturingFaction.name());
        if (capturingFaction != null) {
            compound.setInteger("timer", capture_timer);
            compound.setInteger("abort_timer", capture_abort_timer);
            compound.setString("phase", capture_phase.name());
            compound.setInteger("rem_enem", capture_remainingEnemies_cache);
        }
        return super.writeToNBT(compound);
    }

    private void abortCapture() {
        this.setCapturingFaction(null);
        force_village_update = true;
        this.markDirty();
        VampirismMod.log.t("Abort capture");
        informEntitiesAboutCaptureStop();
        notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.village_capture_aborted"));
        removePlayerFromBossInfo();
    }

    private void completeCapture() {
        this.setControllingFaction(capturingFaction);
        this.setCapturingFaction(null);
        force_village_update = true;
        this.markDirty();
        VampirismMod.log.t("Completed capture");
        informEntitiesAboutCaptureStop();
        notifyNearbyPlayers(new TextComponentTranslation("text.vampirism.village.village_captured_by", new TextComponentTranslation(controllingFaction.getUnlocalizedNamePlural())));
        removePlayerFromBossInfo();
    }

    private void informEntitiesAboutCaptureStop() {
        List<EntityVampirism> list = this.world.getEntitiesWithinAABB(EntityVampirism.class, getAffectedArea());
        for (EntityVampirism e : list) {
            if (e instanceof IVillageCaptureEntity) {
                ((IVillageCaptureEntity) e).stopVillageAttackDefense();
            }
        }
    }

    @Nonnull
    private AxisAlignedBB getAffectedArea() {
        if (affectedArea == null) {
            updateAffectedArea();
        }
        return affectedArea;
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
        return this.world.isRemote ? null : VampirismVillageHelper.getNearestVillageNew(this.world, this.pos, 5);
    }

    private void notifyNearbyPlayers(ITextComponent msg) {
        final BlockPos pos = this.getPos();
        for (EntityPlayer p : this.world.getPlayers(EntityPlayer.class, input -> input != null && input.getDistanceSq(pos) < NOTIFY_DISTANCE_SQ)) {
            p.sendStatusMessage(msg, true);
        }
    }

    private void setCapturingFaction(@Nullable IPlayableFaction faction) {
        this.capturingFaction = faction;
        this.capturingColors = faction != null ? UtilLib.getColorComponents(faction.getColor()) : EnumDyeColor.WHITE.getColorComponentValues();
    }

    /**
     * Try to spawn an appropriate creature and set AI tasks
     *
     * @param attack Attack or defense
     */
    private void spawnCreature(boolean attack) {
        ResourceLocation id;
        if (attack) {
            if (this.capturingFaction == null) return;
            id = getEntityForFaction(this.capturingFaction);
        } else {
            if (this.controllingFaction == null) return;
            id = getEntityForFaction(this.controllingFaction);
        }
        if (id == null) {
            VampirismMod.log.w(TAG, "No village capture entity registered for %s", attack ? this.capturingFaction : this.controllingFaction);
            return;
        }
        Entity e = EntityList.createEntityByIDFromName(id, world);
        if (e instanceof EntityVampireBase) {
            ((EntityVampireBase) e).allowVillageSpawn();
        }
        if (e != null && !UtilLib.spawnEntityInWorld(world, this.getAffectedArea(), e, 50)) {
            e.setDead();
            e = null;
        }
        if (e instanceof IVillageCaptureEntity) {
            if (attack) {
                ((IVillageCaptureEntity) e).attackVillage(this.getAffectedArea());
            } else {
                ((IVillageCaptureEntity) e).defendVillage(this.getAffectedArea());
            }
            VampirismMod.log.t("Spawned %s", e.getName());
        } else if (e != null) {
            VampirismMod.log.w(TAG, "Creature registered for village capture does not implement IVillageCaptureEntity");
        } else {
            VampirismMod.log.t("Failed to spawn creature");
        }

    }

    private void updateAffectedArea() {
        @Nullable VampirismVillage v = getVillage();
        AxisAlignedBB box;
        BlockPos b = v == null ? this.pos : v.getVillage().getCenter();
        int r = v == null ? 15 : v.getVillage().getVillageRadius() + 5;
        box = new AxisAlignedBB(b).grow(r, 1, r);

        if (!box.contains(new Vec3d(this.pos))) {
            VampirismMod.log.w(TAG, "Totem outside of calculated village bb %s %s", box, this.pos);
        }
        double xLength = box.maxX - box.minX;
        double zLength = box.maxZ - box.minZ;
        double cX = 0, cZ = 0;
        if (xLength > 50) {
            cX = 50 - xLength;
        } else if (xLength < 15) {
            cX = 15 - xLength;
        }
        if (zLength > 50) {
            cZ = 50 - zLength;
        } else if (zLength < 15) {
            cZ = 15 - zLength;
        }
        affectedArea = box.grow(cX / 2d, 10, cZ / 2d); //Ensure a maximum and minimum size of the village area. Also set y limits to +-10
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

    private void removePlayerFromBossInfo() {
        for (EntityPlayerMP p : captureInfo.getPlayers()) {
            captureInfo.removePlayer(p);
        }
    }

}
