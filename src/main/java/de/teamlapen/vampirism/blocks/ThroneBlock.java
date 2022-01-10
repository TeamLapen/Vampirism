package de.teamlapen.vampirism.blocks;

import de.teamlapen.vampirism.sit.SitHandler;
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
        super("throne", AbstractBlock.Properties.of(Material.WOOD).strength(2, 3), BlockVoxelshapes.throneBottom, BlockVoxelshapes.throneTop, true);
        markDecorativeBlock();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand p_225533_5_, BlockRayTraceResult traceResult) {
        if (state.getValue(VampirismSplitBlock.PART) == Part.MAIN && traceResult.getDirection() == Direction.UP) {
            SitHandler.startSitting(player, world, pos, 0.5);
            return ActionResultType.SUCCESS;
        }
        return super.use(state, world, pos, player, p_225533_5_, traceResult);
    }
}
