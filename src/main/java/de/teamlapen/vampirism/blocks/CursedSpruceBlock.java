package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class CursedSpruceBlock extends RotatedPillarBlock {

    public CursedSpruceBlock() {
        super(AbstractBlock.Properties.of(Material.WOOD, (p_235431_2_) -> MaterialColor.COLOR_BLACK).strength(2.0F).sound(SoundType.WOOD).randomTicks());
    }

    @Override
    public void randomTick(@Nonnull BlockState state, @Nonnull ServerWorld level, @Nonnull BlockPos pos, @Nonnull Random random) {
        List<Direction> directions = Arrays.stream(Direction.values()).collect(Collectors.toList());
        boolean sideways = false;
        switch (state.getValue(AXIS)){
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
        Direction mainsDirection = directions.get(random.nextInt(directions.size()));
        directions.remove(mainsDirection.getOpposite());
        Direction secondaryDirection = directions.get(random.nextInt(directions.size()));
        BlockPos pos1 = pos.relative(mainsDirection);
        if (mainsDirection != secondaryDirection) {
            if (level.getBlockState(pos1).getBlock() == ModBlocks.cursed_bork) {
                pos1 = pos1.relative(secondaryDirection);
            } else {
                return;
            }
        }
        if (Feature.isAir(level, pos1)) {
            level.setBlock(pos1, ModBlocks.cursed_bork.defaultBlockState().setValue(CursedBorkBlock.FACING, mainsDirection.getOpposite()).setValue(CursedBorkBlock.FACING2, secondaryDirection.getOpposite()).setValue(CursedBorkBlock.AXIS, state.getValue(AXIS)), 3);
        }
    }
}
