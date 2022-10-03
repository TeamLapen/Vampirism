package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CursedSpruceBlock extends LogBlock {

    public CursedSpruceBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOD, (p_235431_2_) -> MaterialColor.CRIMSON_HYPHAE).strength(2.0F).sound(SoundType.WOOD).randomTicks());
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        List<Direction> directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
        if (state.getBlock() != ModBlocks.CURSED_SPRUCE_WOOD.get()) {
            switch (state.getValue(AXIS)) {
                case X -> {
                    directions.remove(Direction.WEST);
                    directions.remove(Direction.EAST);
                }
                case Y -> {
                    directions.remove(Direction.UP);
                    directions.remove(Direction.DOWN);
                }
                case Z -> {
                    directions.remove(Direction.NORTH);
                    directions.remove(Direction.SOUTH);
                }
            }
        }
        Direction mainsDirection = directions.get(random.nextInt(directions.size()));
        directions.remove(mainsDirection.getOpposite());
        Direction secondaryDirection = directions.get(random.nextInt(directions.size()));
        BlockPos pos1 = pos.relative(mainsDirection);
        if (mainsDirection != secondaryDirection) {
            if (level.getBlockState(pos1).getBlock() == ModBlocks.CURSED_BARK.get()) {
                pos1 = pos1.relative(secondaryDirection);
            } else {
                return;
            }
        }
        BlockState state1 = level.getBlockState(pos1);
        if (state1.isAir() || (state1.getBlock() == ModBlocks.CURSED_BARK.get() && state1.getValue(CursedBarkBlock.FACING) != state1.getValue(CursedBarkBlock.FACING2))) {
            level.setBlock(pos1, ModBlocks.CURSED_BARK.get().defaultBlockState().setValue(CursedBarkBlock.FACING, mainsDirection.getOpposite()).setValue(CursedBarkBlock.FACING2, secondaryDirection.getOpposite()).setValue(CursedBarkBlock.AXIS, state.getValue(AXIS)), 3);
        }
    }
}
