package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CursedSpruceBlock extends StrippableLogBlock {

    public CursedSpruceBlock(@Nullable Supplier<? extends LogBlock> strippedBlock) {
        super(AbstractBlock.Properties.of(Material.WOOD, (p_235431_2_) -> MaterialColor.CRIMSON_HYPHAE).strength(2.0F).sound(SoundType.WOOD).randomTicks(), strippedBlock);
    }

    @Override
    public void randomTick(@Nonnull BlockState state, @Nonnull ServerWorld level, @Nonnull BlockPos pos, @Nonnull Random random) {
        List<Direction> directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
        if (state.getBlock() != ModBlocks.CURSED_SPRUCE_WOOD.get()) {
            switch (state.getValue(AXIS)) {
                case X:
                    directions.remove(Direction.WEST);
                    directions.remove(Direction.EAST);
                    break;
                case Y:
                    directions.remove(Direction.UP);
                    directions.remove(Direction.DOWN);
                    break;
                case Z:
                    directions.remove(Direction.NORTH);
                    directions.remove(Direction.SOUTH);
                    break;
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
        boolean air = Feature.isAir(level, pos1);
        BlockState state1 = level.getBlockState(pos1);
        if (air || (state1.getBlock() == ModBlocks.CURSED_BARK.get() && state1.getValue(CursedBarkBlock.FACING) != state1.getValue(CursedBarkBlock.FACING2))) {
            level.setBlock(pos1, ModBlocks.CURSED_BARK.get().defaultBlockState().setValue(CursedBarkBlock.FACING, mainsDirection.getOpposite()).setValue(CursedBarkBlock.FACING2, secondaryDirection.getOpposite()).setValue(CursedBarkBlock.AXIS, state.getValue(AXIS)), 3);
        }
    }
}
