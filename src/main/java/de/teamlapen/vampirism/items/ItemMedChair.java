package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
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
    private final static String regName = "item_med_chair";

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
        BlockPos otherPos = pos.offset(facing);
        Block otherBlock = world.getBlockState(otherPos).getBlock();
        boolean other_replaceable = otherBlock.isReplaceable(world, otherPos);
        boolean flag1 = world.isAirBlock(pos) || replaceable;
        boolean flag2 = world.isAirBlock(otherPos) || other_replaceable;
        VampirismMod.log.d(regName, "Trying to place itemMedChair. Targeted Block %s (at %s, replaceable %b) -> Final Target %s (at %s, %b air) ", block, targetPos, replaceable, world.getBlockState(pos).getBlock(), pos, world.isAirBlock(pos));
        VampirismMod.log.d(regName, "Looking to %s so adjacent block is %s (at %s, replaceable %b, air %b)", facing, otherBlock, otherPos, other_replaceable, world.isAirBlock(otherPos));
        if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(otherPos, side, stack)) {

            VampirismMod.log.d(regName, "F1 %b, F2 %b, S1 %b, S2 %b", flag1, flag2, UtilLib.doesBlockHaveSolidTopSurface(world, pos.down()), UtilLib.doesBlockHaveSolidTopSurface(world, otherPos.down()));
            if (flag1 && flag2 && UtilLib.doesBlockHaveSolidTopSurface(world, pos.down()) && UtilLib.doesBlockHaveSolidTopSurface(world, otherPos.down())) {
                IBlockState state1 = ModBlocks.med_chair.getDefaultState().withProperty(BlockMedChair.PART, BlockMedChair.EnumPart.BOTTOM).withProperty(BlockMedChair.FACING, facing.getOpposite());
                if (world.setBlockState(pos, state1, 3)) {
                    IBlockState state2 = state1.withProperty(BlockMedChair.PART, BlockMedChair.EnumPart.TOP).withProperty(BlockMedChair.FACING, facing.getOpposite());
                    world.setBlockState(otherPos, state2, 3);
                }
                ItemStackUtil.decr(stack);
                return EnumActionResult.SUCCESS;
            } else {
                VampirismMod.log.d(regName, "Failed. First down %s, Second down %s  ", world.getBlockState(pos.down()).getBlock(), world.getBlockState(otherPos.down()).getBlock());
            }
        } else {
            VampirismMod.log.d(regName, "Player cannot edit %b %b", player.canPlayerEdit(pos, side, stack), player.canPlayerEdit(otherPos, side, stack));
        }
        return EnumActionResult.FAIL;
    }


}
