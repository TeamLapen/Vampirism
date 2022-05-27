package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.MedChairBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * Item which places the two med chair blocks
 */
public class MedChairItem extends Item {

    public MedChairItem() {
        super(new Properties().tab(VampirismMod.creativeTab));
    }


    @Nonnull
    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getClickedFace() != Direction.UP) {
            return InteractionResult.FAIL;
        }
        Level world = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (world.isClientSide)
            return InteractionResult.SUCCESS;

        ItemStack stack = ctx.getItemInHand();
        // Increasing yDisplay, so the chair is placed on top of the block that was
        // clicked at except if the block is replaceable
        BlockState iblockstate = world.getBlockState(ctx.getClickedPos());
        boolean replaceable = iblockstate.canBeReplaced(new BlockPlaceContext(ctx));

        BlockPos pos = replaceable ? ctx.getClickedPos() : ctx.getClickedPos().above();

        // Direction the player is facing
        int direction = player == null ? 0 : Mth.floor((player.getYRot() * 4F) / 360F + 0.5D) & 3;
        Direction facing = Direction.from2DDataValue(direction);
        BlockPos otherPos = pos.relative(facing);
        Block otherBlock = world.getBlockState(otherPos).getBlock();

        boolean other_replaceable = world.getBlockState(otherPos).canBeReplaced(new BlockPlaceContext(ctx));
        boolean flag1 = world.isEmptyBlock(pos) || replaceable;
        boolean flag2 = world.isEmptyBlock(otherPos) || other_replaceable;

        if (player == null || player.mayUseItemAt(pos, ctx.getClickedFace(), stack) && player.mayUseItemAt(otherPos, ctx.getClickedFace(), stack)) {

            if (flag1 && flag2 && UtilLib.doesBlockHaveSolidTopSurface(world, pos.below()) && UtilLib.doesBlockHaveSolidTopSurface(world, otherPos.below())) {
                BlockState state1 = ModBlocks.MED_CHAIR.get().defaultBlockState().setValue(MedChairBlock.PART, MedChairBlock.EnumPart.BOTTOM).setValue(MedChairBlock.FACING, facing.getOpposite());
                if (world.setBlock(pos, state1, 3)) {
                    BlockState state2 = state1.setValue(MedChairBlock.PART, MedChairBlock.EnumPart.TOP).setValue(MedChairBlock.FACING, facing.getOpposite());
                    world.setBlock(otherPos, state2, 3);
                }
                stack.shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }


}
