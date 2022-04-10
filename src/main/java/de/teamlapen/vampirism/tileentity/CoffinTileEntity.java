package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * TileEntity for coffins. Handles coffin lid position and color
 */
public class CoffinTileEntity extends TileEntity implements ITickableTileEntity {
    public final boolean renderAsItem;
    public float lidPos;
    public DyeColor color = DyeColor.BLACK;
    private boolean lastTickOccupied;

    public CoffinTileEntity() {
        super(ModTiles.coffin);
        this.renderAsItem = false;
    }

    public CoffinTileEntity(boolean renderAsItem) {
        super(ModTiles.coffin);
        this.renderAsItem = renderAsItem;
    }

    public CoffinTileEntity(DyeColor color) {
        this();
        this.changeColor(color);
    }

    public void changeColor(DyeColor color) {
        this.color = color;
        setChanged();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(worldPosition.getX() - 4, worldPosition.getY(), worldPosition.getZ() - 4, worldPosition.getX() + 4, worldPosition.getY() + 2, worldPosition.getZ() + 4);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getBlockPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.color = compound.contains("color") ? DyeColor.byId(compound.getInt("color")) : DyeColor.BLACK;

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        if (hasLevel()) load(level.getBlockState(packet.getPos()), packet.getTag());
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        CompoundNBT nbt = super.save(compound);
        nbt.putInt("color", color.getId());
        return nbt;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    @Override
    public void tick() {
        if (!hasLevel() || !CoffinBlock.isHead(level, worldPosition)) {
            return;

        }
        boolean occupied = CoffinBlock.isOccupied(level, worldPosition);
        if (lastTickOccupied != occupied) {
            this.level.playLocalSound(worldPosition.getX(), (double) this.worldPosition.getY() + 0.5D, worldPosition.getZ(), ModSounds.coffin_lid, SoundCategory.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F, true);
            lastTickOccupied = occupied;
        }


    }
}