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
        super(regName, new Properties().group(VampirismMod.creativeTab));
    }


    @Override
    public ActionResultType onItemUse(ItemUseContext ctx) {
        if (ctx.getFace() != Direction.UP) {
            return ActionResultType.FAIL;
        }
        World world = ctx.getWorld();
        PlayerEntity player = ctx.getPlayer();
        if (world.isRemote)
            return ActionResultType.SUCCESS;

        ItemStack stack = ctx.getItem();
        // Increasing yDisplay, so the chair is placed on top of the block that was
        // clicked at except if the block is replaceable
        BlockState iblockstate = world.getBlockState(ctx.getPos());
        boolean replaceable = iblockstate.isReplaceable(new BlockItemUseContext(ctx));

        BlockPos pos = replaceable ? ctx.getPos() : ctx.getPos().up();

        // Direction the player is facing
        int direction = player == null ? 0 : MathHelper.floor((player.rotationYaw * 4F) / 360F + 0.5D) & 3;
        Direction facing = Direction.byHorizontalIndex(direction);
        BlockPos otherPos = pos.offset(facing);
        Block otherBlock = world.getBlockState(otherPos).getBlock();

        boolean other_replaceable = world.getBlockState(otherPos).isReplaceable(new BlockItemUseContext(ctx));
        boolean flag1 = world.isAirBlock(pos) || replaceable;
        boolean flag2 = world.isAirBlock(otherPos) || other_replaceable;

        if (player == null || player.canPlayerEdit(pos, ctx.getFace(), stack) && player.canPlayerEdit(otherPos, ctx.getFace(), stack)) {

            if (flag1 && flag2 && UtilLib.doesBlockHaveSolidTopSurface(world, pos.down()) && UtilLib.doesBlockHaveSolidTopSurface(world, otherPos.down())) {
                BlockState state1 = ModBlocks.med_chair.getDefaultState().with(MedChairBlock.PART, MedChairBlock.EnumPart.BOTTOM).with(MedChairBlock.FACING, facing.getOpposite());
                if (world.setBlockState(pos, state1, 3)) {
                    BlockState state2 = state1.with(MedChairBlock.PART, MedChairBlock.EnumPart.TOP).with(MedChairBlock.FACING, facing.getOpposite());
                    world.setBlockState(otherPos, state2, 3);
                }
                stack.shrink(1);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.FAIL;
    }


}
