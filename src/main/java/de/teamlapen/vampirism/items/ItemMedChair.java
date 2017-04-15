package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blocks.BlockMedChair;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Item which places the two med chair blocks
 */
public class ItemMedChair extends VampirismItem {
    private final static String regName = "itemMedChair";

    public ItemMedChair() {
        super(regName);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos targetPos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (side != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote)
            return EnumActionResult.SUCCESS;

        ItemStack stack = player.getHeldItem(hand);
        // Increasing y, so the chair is placed on top of the block that was
        // clicked at except if the block is replaceable
        IBlockState iblockstate = world.getBlockState(targetPos);
        Block block = iblockstate.getBlock();
        boolean replaceable = block.isReplaceable(world, targetPos);

        BlockPos pos = replaceable ? targetPos : targetPos.up();

        // Direction the player is facing
        int direction = MathHelper.floor((player.rotationYaw * 4F) / 360F + 0.5D) & 3;
        EnumFacing facing = EnumFacing.getHorizontal(direction);
        BlockPos other = pos.offset(facing);
        boolean other_replaceable = block.isReplaceable(world, other);
        boolean flag1 = world.isAirBlock(pos) || replaceable;
        boolean flag2 = world.isAirBlock(other) || other_replaceable;
        if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(other, side, stack)) {
//            VampirismMod.log.t("%b %b %b %b", flag1, flag2, UtilLib.doesBlockHaveSolidTopSurface(world, pos.down()), UtilLib.doesBlockHaveSolidTopSurface(world, other.down()));

            if (flag1 && flag2 && UtilLib.doesBlockHaveSolidTopSurface(world, pos.down()) && UtilLib.doesBlockHaveSolidTopSurface(world, other.down())) {
                IBlockState state1 = ModBlocks.medChair.getDefaultState().withProperty(BlockMedChair.PART, BlockMedChair.EnumPart.BOTTOM).withProperty(BlockMedChair.FACING, facing.getOpposite());
                if (world.setBlockState(pos, state1, 3)) {
                    IBlockState state2 = state1.withProperty(BlockMedChair.PART, BlockMedChair.EnumPart.TOP).withProperty(BlockMedChair.FACING, facing.getOpposite());
                    world.setBlockState(other, state2, 3);
                }
                stack.setCount(stack.getCount() - 1);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }


}
