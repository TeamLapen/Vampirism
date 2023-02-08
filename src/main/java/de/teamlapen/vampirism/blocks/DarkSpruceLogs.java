package de.teamlapen.vampirism.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DarkSpruceLogs extends StrippableLogBlock {

    public static final BooleanProperty INVULNERABLE = BooleanProperty.create("invulnerable");

    public DarkSpruceLogs(@NotNull Supplier<? extends LogBlock> strippedLog) {
        super(MapColor.COLOR_BLACK, MapColor.COLOR_BLACK, strippedLog);
        registerDefaultState(defaultBlockState().setValue(INVULNERABLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(INVULNERABLE);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return !state.getValue(INVULNERABLE) && super.isFlammable(state, level, pos, direction);
    }
}
