package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.sit.SitEntity;
import de.teamlapen.vampirism.sit.SitHandler;
import de.teamlapen.vampirism.sit.SitUtil;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;


public class ThroneBlock extends VampirismSplitBlock {
    public ThroneBlock() {
        super(AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.throneBottom, BlockVoxelshapes.throneTop, true);
        markDecorativeBlock();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult traceResult) {
        Part part = state.getValue(PART);
        Direction oppFacing = state.getValue(FACING).getOpposite();
        if (part == Part.MAIN && (traceResult.getDirection() == Direction.UP || traceResult.getDirection() == oppFacing)) {
            SitHandler.startSitting(player, world, pos, 0.5);
            return ActionResultType.SUCCESS;
        }
        else if(part == Part.SUB && traceResult.getDirection() == oppFacing && world.getBlockState(pos.below()).is(this)){
            SitHandler.startSitting(player, world, pos.below(), 0.5);
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, p_225533_5_, traceResult);
    }

    @Override
    public void onRemove(BlockState pState, World pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        SitEntity entity = SitUtil.getSitEntity(pLevel, pPos);
        if (entity != null) {
            entity.remove();
        }
    }
}
