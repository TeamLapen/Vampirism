package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


/**
 * Central component of the Village Control system.
 * <p>
 * This tile entity is used to store the controlling faction, update the VampirismVillage instance and manages capturing progress.
 * It displays the current status and allows players to capture the village.
 */
public class TileTotem extends TileEntity implements ITickable {

    private boolean isComplete;

    private boolean insideVillage;

    @SideOnly(Side.CLIENT)
    private long beamRenderCounter;
    @SideOnly(Side.CLIENT)
    private float beamRenderScale;


    @Nullable
    private IPlayableFaction controllingFaction = VReference.HUNTER_FACTION;//;
    private float[] baseColors = UtilLib.getColorComponents(VReference.HUNTER_FACTION.getColor());// EnumDyeColor.WHITE.getColorComponentValues();

    @Nullable
    private IPlayableFaction capturingFaction;
    private float[] capturingColors = UtilLib.getColorComponents(VReference.VAMPIRE_FACTION.getColor());//EnumDyeColor.WHITE.getColorComponentValues();

    public boolean canPlayerRemoveBlock(EntityPlayer player) {
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

    @SideOnly(Side.CLIENT)
    public float[] getBaseColors() {
        return baseColors;
    }

    public int getCaptureProgress() {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public float[] getCapturingColors() {
        return capturingColors;
    }

    @Nullable
    public IPlayableFaction getCapturingFaction() {
        return capturingFaction;
    }

    private void setCapturingFaction(@Nullable IPlayableFaction faction) {
        this.capturingFaction = faction;
        this.baseColors = faction != null ? UtilLib.getColorComponents(faction.getColor()) : EnumDyeColor.WHITE.getColorComponentValues();
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

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        String controlling = compound.getString("controlling");
        String capturing = compound.getString("capturing");
        IPlayableFaction controllingFaction = null;
        IPlayableFaction capturingFaction = null;
        if (!"none".equals(controlling)) {
            IFaction f = VampirismAPI.factionRegistry().getFactionByName(controlling);
            if (f == null || !(f instanceof IPlayableFaction)) {
                VampirismMod.log.w("TileTotem", "Stored faction %s does not exist or is not playable", controlling);
            } else {
                controllingFaction = (IPlayableFaction) f;
            }
        }
        if (!"none".equals(capturing)) {
            IFaction f = VampirismAPI.factionRegistry().getFactionByName(capturing);
            if (f == null || !(f instanceof IPlayableFaction)) {
                VampirismMod.log.w("TileTotem", "Stored faction %s does not exist or is not playable", capturing);
            } else {
                capturingFaction = (IPlayableFaction) f;
            }
        }
        this.setControllingFaction(controllingFaction);
        this.setCapturingFaction(capturingFaction);
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
    public void update() {
        if (this.world.getTotalWorldTime() % 80L == 0L) {
            this.updateTotem();
        }
    }

    public void updateTotem() {
        boolean complete = this.world.getBlockState(this.pos.down()).getBlock().equals(ModBlocks.totem_base);
        if (complete != isComplete) {
            //TODO
        }
        isComplete = complete;
        if (isComplete) {
            @Nullable IVampirismVillage village = null;//VampirismVillageHelper.getNearestVillage(this.world,this.pos,5);
            boolean insideVillageNew = village != null;
            if (insideVillageNew != insideVillage) {
                //TODO
            }
            insideVillage = insideVillageNew;
            if (insideVillage) {
                //Destroy all (breakable) blocks above
                int x = pos.getX();
                int y = pos.getY();
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

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("controlling", controllingFaction == null ? "null" : controllingFaction.name());
        compound.setString("capturing", capturingFaction == null ? "null" : capturingFaction.name());
        return super.writeToNBT(compound);
    }
}
