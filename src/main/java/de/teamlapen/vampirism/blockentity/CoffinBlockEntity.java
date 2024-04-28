package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TileEntity for coffins. Handles coffin lid position and color
 */
public class CoffinBlockEntity extends BlockEntity {
    public float lidPos;
    public DyeColor color = DyeColor.RED;
    private boolean playLidSoundFlag;

    public CoffinBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModTiles.COFFIN.get(), pos, state);
    }

    public CoffinBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state, DyeColor color) {
        super(ModTiles.COFFIN.get(), pos, state);
        this.color = color;
    }

    public void changeColor(DyeColor color) {
        this.color = color;
        setChanged();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        this.color = compound.contains("color") ? DyeColor.byId(compound.getInt("color")) : DyeColor.BLACK;
        this.lidPos = compound.getFloat("lidPos");
    }

    @Override
    public void onDataPacket(Connection net, @NotNull ClientboundBlockEntityDataPacket packet, HolderLookup.Provider provider) {
        if (hasLevel()) loadCustomOnly(packet.getTag(), provider);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("color", this.color.getId());
        compound.putFloat("lidPos", this.lidPos);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        }
    }

    public static void clientTickHead(@NotNull Level level, @NotNull BlockPos pos, BlockState state, @NotNull CoffinBlockEntity blockEntity) {
        boolean occupied = CoffinBlock.isClosed(level, pos);
        if (blockEntity.playLidSoundFlag != occupied) {
            level.playLocalSound(pos.getX(), (double) pos.getY() + 0.5D, pos.getZ(), ModSounds.COFFIN_LID.get(), SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F, true);
            blockEntity.playLidSoundFlag = CoffinBlock.isClosed(level, pos);
        }

        // Calculate lid position
        boolean isClosed = blockEntity.hasLevel() && CoffinBlock.isClosed(level, pos);
        if (!isClosed) {
            blockEntity.lidPos += 0.02F;
        } else {
            blockEntity.lidPos -= 0.02F;
        }
        blockEntity.lidPos = Mth.clamp(blockEntity.lidPos, 0, 1);

    }
}