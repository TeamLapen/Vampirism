package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.sit.SitHandler;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;


public class ThroneBlock extends VampirismSplitBlock {

    public ThroneBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.throneBottom, BlockVoxelshapes.throneTop, true);
        markDecorativeBlock();
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand p_225533_5_, BlockHitResult traceResult) {
        Part part = state.getValue(PART);
        Direction oppFacing = state.getValue(FACING).getOpposite();
        if (part == Part.MAIN && (traceResult.getDirection() == Direction.UP || traceResult.getDirection() == oppFacing)) {
            SitHandler.startSitting(player, world, pos, 0.5);
            return InteractionResult.SUCCESS;
        }
        else if(part == Part.SUB && traceResult.getDirection() == oppFacing && world.getBlockState(pos.below()).is(this)){
            SitHandler.startSitting(player, world, pos.below(), 0.5);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, p_225533_5_, traceResult);
    }
}
