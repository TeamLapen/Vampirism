package de.teamlapen.vampirism.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class StrippableLogBlock extends LogBlock {

    private final @NotNull Supplier<? extends LogBlock> strippedBlock;

    public StrippableLogBlock(@NotNull BlockBehaviour.Properties properties, @NotNull Supplier<? extends LogBlock> strippedLog) {
        super(properties);
        this.strippedBlock = strippedLog;
    }

    public StrippableLogBlock(MapColor color1, MapColor color2, @NotNull Supplier<? extends LogBlock> strippedLog) {
        super(color1, color2);
        this.strippedBlock = strippedLog;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility toolAction, boolean simulate) {
        if (toolAction == ItemAbilities.AXE_STRIP) {
            return getStrippedState(state, context.getLevel(), context.getClickedPos());
        }
        return super.getToolModifiedState(state, context, toolAction, simulate);
    }

    private BlockState getStrippedState(BlockState state, Level level, BlockPos clickedPos) {
        LogBlock strippedBlock = this.strippedBlock.get();
        return strippedBlock.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
    }
}
