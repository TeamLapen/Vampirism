package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blocks.BlockCoffin;
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
 * Used to place coffings
 */
public class ItemCoffin extends VampirismItem {
    public static final String name = "item_coffin";

    public ItemCoffin() {
        super(name);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos targetPos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {

        if (side != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        if (world.isRemote)
            return EnumActionResult.PASS;

        ItemStack stack = player.getHeldItem(hand);
        // Increasing y, so the coffin is placed on top of the block that was
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
            if (flag1 && flag2 && UtilLib.doesBlockHaveSolidTopSurface(world, pos.down()) && UtilLib.doesBlockHaveSolidTopSurface(world, other.down())) {
                IBlockState state1 = ModBlocks.coffin.getDefaultState().withProperty(BlockCoffin.OCCUPIED, Boolean.FALSE).withProperty(BlockCoffin.PART, BlockCoffin.EnumPartType.FOOT).withProperty(BlockCoffin.FACING, facing);
                if (world.setBlockState(pos, state1, 3)) {
                    IBlockState state2 = state1.withProperty(BlockCoffin.PART, BlockCoffin.EnumPartType.HEAD).withProperty(BlockCoffin.FACING, facing);
                    world.setBlockState(other, state2, 3);


                }
                --stack.stackSize;
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }


}
