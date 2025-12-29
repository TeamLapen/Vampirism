package de.teamlapen.factions.common.world.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TotemBaseBlock extends Block {

    private static final VoxelShape SHAPE = Shapes.join(Block.box(1, 0, 1, 15, 2, 15), Block.box(4, 2, 4, 12, 16, 12), BooleanOp.OR);

    public TotemBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, ItemStack toolStack, boolean willHarvest, FluidState fluid) {
        BlockPos up = pos.above();
        BlockState upState = level.getBlockState(pos.above());
        if (upState.getBlock() instanceof TotemTopBlock) {
            BlockEntity upTE = level.getBlockEntity(pos.above());
            if (!upState.getBlock().onDestroyedByPlayer(upState, level, pos.above(), player, toolStack, willHarvest, fluid)) {
                return false;
            }
            if (willHarvest) {
                Block.dropResources(upState, level, up, upTE);
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, toolStack, willHarvest, fluid);
    }
}
