package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * TileEntity for coffins. Handles coffin lid position and color
 */
public class CoffinBlockEntity extends BlockEntity {
    public final boolean renderAsItem;
    public int lidPos;
    public DyeColor color = DyeColor.BLACK;
    private boolean lastTickOccupied;

    public CoffinBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.COFFIN.get(), pos, state);
        this.renderAsItem = false;
    }

    public CoffinBlockEntity(boolean renderAsItem, BlockPos pos, BlockState state) {
        super(ModTiles.COFFIN.get(), pos, state);
        this.renderAsItem = renderAsItem;
    }

    public CoffinBlockEntity(DyeColor color, BlockPos pos, BlockState state) {
        this(pos, state);
        this.changeColor(color);
    }

    public void changeColor(DyeColor color) {
        this.color = color;
        setChanged();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX() - 4, worldPosition.getY(), worldPosition.getZ() - 4, worldPosition.getX() + 4, worldPosition.getY() + 2, worldPosition.getZ() + 4);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);
        this.color = compound.contains("color") ? DyeColor.byId(compound.getInt("color")) : DyeColor.BLACK;

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        if (hasLevel()) load(packet.getTag());
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("color", color.getId());
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }

    public static void clientTickHead(Level level, BlockPos pos, BlockState state, CoffinBlockEntity blockEntity) {
        boolean occupied = CoffinBlock.isOccupied(level, pos);
        if (blockEntity.lastTickOccupied != occupied) {
            level.playLocalSound(pos.getX(), (double) pos.getY() + 0.5D, pos.getZ(), ModSounds.COFFIN_LID.get(), SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F, true);
            blockEntity.lastTickOccupied = occupied;
        }

    }
}