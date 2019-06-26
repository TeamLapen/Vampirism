package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BlockMedChair;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Item which places the two med chair blocks
 */
public class ItemMedChair extends VampirismItem {
    private final static String regName = "item_med_chair";

    public ItemMedChair() {
        super(regName, new Properties().group(VampirismMod.creativeTab));
    }


    @Override
    public EnumActionResult onItemUse(ItemUseContext ctx) {
        if (ctx.getFace() != EnumFacing.UP) {
            return EnumActionResult.FAIL;
        }
        World world = ctx.getWorld();
        EntityPlayer player = ctx.getPlayer();
        if (world.isRemote)
            return EnumActionResult.SUCCESS;

        ItemStack stack = ctx.getItem();
        // Increasing y, so the chair is placed on top of the block that was
        // clicked at except if the block is replaceable
        IBlockState iblockstate = world.getBlockState(ctx.getPos());
        boolean replaceable = iblockstate.isReplaceable(new BlockItemUseContext(ctx));

        BlockPos pos = replaceable ? ctx.getPos() : ctx.getPos().up();

        // Direction the player is facing
        int direction = player == null ? 0 : MathHelper.floor((player.rotationYaw * 4F) / 360F + 0.5D) & 3;
        EnumFacing facing = EnumFacing.byHorizontalIndex(direction);
        BlockPos otherPos = pos.offset(facing);
        Block otherBlock = world.getBlockState(otherPos).getBlock();

        boolean other_replaceable = world.getBlockState(otherPos).isReplaceable(new BlockItemUseContext(ctx.getWorld(), ctx.getPlayer(), ctx.getItem(), otherPos, ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ()));
        boolean flag1 = world.isAirBlock(pos) || replaceable;
        boolean flag2 = world.isAirBlock(otherPos) || other_replaceable;

        if (player == null || player.canPlayerEdit(pos, ctx.getFace(), stack) && player.canPlayerEdit(otherPos, ctx.getFace(), stack)) {

            if (flag1 && flag2 && UtilLib.doesBlockHaveSolidTopSurface(world, pos.down()) && UtilLib.doesBlockHaveSolidTopSurface(world, otherPos.down())) {
                IBlockState state1 = ModBlocks.med_chair.getDefaultState().with(BlockMedChair.PART, BlockMedChair.EnumPart.BOTTOM).with(BlockMedChair.FACING, facing.getOpposite());
                if (world.setBlockState(pos, state1, 3)) {
                    IBlockState state2 = state1.with(BlockMedChair.PART, BlockMedChair.EnumPart.TOP).with(BlockMedChair.FACING, facing.getOpposite());
                    world.setBlockState(otherPos, state2, 3);
                }
                stack.shrink(1);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }


}
