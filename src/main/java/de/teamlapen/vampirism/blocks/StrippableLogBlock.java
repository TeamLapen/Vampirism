package de.teamlapen.vampirism.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class StrippableLogBlock extends LogBlock {

    private final @Nonnull Supplier<? extends LogBlock> strippedBlock;

    public StrippableLogBlock(AbstractBlock.Properties properties, @Nonnull Supplier<? extends LogBlock> strippedLog) {
        super(properties);
        this.strippedBlock = strippedLog;
    }

    public StrippableLogBlock(MaterialColor color1, MaterialColor color2, @Nonnull Supplier<? extends LogBlock> strippedLog) {
        super(color1, color2);
        this.strippedBlock = strippedLog;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
        if (toolType == ToolType.AXE) {
            return getStrippedState(state, world, pos);
        }
        return super.getToolModifiedState(state, world, pos, player, stack, toolType);
    }

    private BlockState getStrippedState(BlockState state, World level, BlockPos clickedPos) {
        LogBlock strippedBlock = this.strippedBlock.get();
        return strippedBlock.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
    }
}
