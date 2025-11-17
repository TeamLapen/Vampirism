package de.teamlapen.vampirism.common.blockentity;

import de.teamlapen.lib.common.blockentities.NetworkedBlockEntity;
import de.teamlapen.vampirism.common.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

/**
 * TileEntity for coffins. Handles coffin lid position and color
 */
public class CoffinBlockEntity extends NetworkedBlockEntity {
    public float lidPos;
    public DyeColor color = DyeColor.RED;
    private boolean playLidSoundFlag;

    public CoffinBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModBlockEntities.COFFIN.get(), pos, state);
    }

    public CoffinBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state, DyeColor color) {
        super(ModBlockEntities.COFFIN.get(), pos, state);
        this.color = color;
    }

    public void changeColor(DyeColor color) {
        this.color = color;
        setChanged();
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.color = input.read("color", DyeColor.CODEC).orElse(DyeColor.BLACK);
        this.lidPos = input.getFloatOr("lidPos", 0);
        this.playLidSoundFlag = this.lidPos==0;
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.store("color", DyeColor.CODEC, this.color);
        output.putFloat("lidPos", this.lidPos);
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