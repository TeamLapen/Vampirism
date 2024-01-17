package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.core.ModStats;
import de.teamlapen.vampirism.sit.SitEntity;
import de.teamlapen.vampirism.sit.SitHandler;
import de.teamlapen.vampirism.sit.SitUtil;
import de.teamlapen.vampirism.util.BlockVoxelshapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;


public class ThroneBlock extends VampirismSplitBlock {

    public ThroneBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().pushReaction(PushReaction.DESTROY).strength(2, 3), BlockVoxelshapes.throneBottom, BlockVoxelshapes.throneTop, true);
        markDecorativeBlock();
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, BlockPos pos, Player player, InteractionHand p_225533_5_, BlockHitResult traceResult) {
        Part part = state.getValue(PART);
        Direction oppFacing = state.getValue(FACING).getOpposite();
        player.awardStat(ModStats.INTERACT_WITH_THRONE.get());
        if (part == Part.MAIN && (traceResult.getDirection() == Direction.UP || traceResult.getDirection() == oppFacing)) {
            SitHandler.startSitting(player, world, pos, 0.5);
            return InteractionResult.SUCCESS;
        } else if (part == Part.SUB && traceResult.getDirection() == oppFacing && world.getBlockState(pos.below()).is(this)) {
            SitHandler.startSitting(player, world, pos.below(), 0.5);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, p_225533_5_, traceResult);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        SitEntity entity = SitUtil.getSitEntity(pLevel, pPos);
        if (entity != null) {
            entity.discard();
        }
    }
}
