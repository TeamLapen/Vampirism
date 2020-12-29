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
    public int lidPos;
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
        markDirty();
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY() + 2, pos.getZ() + 4);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world != null)
            world.notifyBlockUpdate(getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
    }


    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.color = compound.contains("color") ? DyeColor.byId(compound.getInt("color")) : DyeColor.BLACK;

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        if (hasWorld()) read(world.getBlockState(packet.getPos()), packet.getNbtCompound());
    }

    @Override
    public void tick() {
        if (!hasWorld() || !CoffinBlock.isHead(world, pos)) {
            return;

        }
        boolean occupied = CoffinBlock.isOccupied(world, pos);
        if (lastTickOccupied != occupied) {
            this.world.playSound(pos.getX(), (double) this.pos.getY() + 0.5D, pos.getZ(), ModSounds.coffin_lid, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F, true);
            lastTickOccupied = occupied;
        }


    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        nbt.putInt("color", color.getId());
        return nbt;
    }
}