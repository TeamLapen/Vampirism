package de.teamlapen.vampirism.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Part of the Altar of Infusion structure
 */
public class AltarTipBlock extends Block {

    protected static final VoxelShape SHAPE = Shapes.or(
            Block.box(3, 0, 3, 13, 3, 13),
            Block.box(4, 3, 4, 12, 4, 12),
            Block.box(5, 4, 5, 11, 5, 11),
            Block.box(6, 5, 6, 10, 6, 10),
            Block.box(7, 6, 7, 9, 7, 9)
    );

    public AltarTipBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getShape(state, level, pos, context);
    }
}
