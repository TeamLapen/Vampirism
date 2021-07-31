package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.MedChairBlock;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Item which places the two med chair blocks
 */
public class MedChairItem extends VampirismItem {
    private final static String regName = "item_med_chair";

    public MedChairItem() {
        super(regName, new Properties().tab(VampirismMod.creativeTab));
    }


    @Override
    public ActionResultType useOn(ItemUseContext ctx) {
        if (ctx.getClickedFace() != Direction.UP) {
            return ActionResultType.FAIL;
        }
        World world = ctx.getLevel();
        PlayerEntity player = ctx.getPlayer();
        if (world.isClientSide)
            return ActionResultType.SUCCESS;

        ItemStack stack = ctx.getItemInHand();
        // Increasing yDisplay, so the chair is placed on top of the block that was
        // clicked at except if the block is replaceable
        BlockState iblockstate = world.getBlockState(ctx.getClickedPos());
        boolean replaceable = iblockstate.canBeReplaced(new BlockItemUseContext(ctx));

        BlockPos pos = replaceable ? ctx.getClickedPos() : ctx.getClickedPos().above();

        // Direction the player is facing
        int direction = player == null ? 0 : MathHelper.floor((player.yRot * 4F) / 360F + 0.5D) & 3;
        Direction facing = Direction.from2DDataValue(direction);
        BlockPos otherPos = pos.relative(facing);
        Block otherBlock = world.getBlockState(otherPos).getBlock();

        boolean other_replaceable = world.getBlockState(otherPos).canBeReplaced(new BlockItemUseContext(ctx));
        boolean flag1 = world.isEmptyBlock(pos) || replaceable;
        boolean flag2 = world.isEmptyBlock(otherPos) || other_replaceable;

        if (player == null || player.mayUseItemAt(pos, ctx.getClickedFace(), stack) && player.mayUseItemAt(otherPos, ctx.getClickedFace(), stack)) {

            if (flag1 && flag2 && UtilLib.doesBlockHaveSolidTopSurface(world, pos.below()) && UtilLib.doesBlockHaveSolidTopSurface(world, otherPos.below())) {
                BlockState state1 = ModBlocks.med_chair.defaultBlockState().setValue(MedChairBlock.PART, MedChairBlock.EnumPart.BOTTOM).setValue(MedChairBlock.FACING, facing.getOpposite());
                if (world.setBlock(pos, state1, 3)) {
                    BlockState state2 = state1.setValue(MedChairBlock.PART, MedChairBlock.EnumPart.TOP).setValue(MedChairBlock.FACING, facing.getOpposite());
                    world.setBlock(otherPos, state2, 3);
                }
                stack.shrink(1);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }


}
