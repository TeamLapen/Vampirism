package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileCoffin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created by Max on 12.03.2016.
 */
public class ItemCoffin extends VampirismItem {
    public static final String name = "itemCoffin";
    private static final String TAG = "ItemCoffin";

    public ItemCoffin() {
        super(name);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;
        if (side != EnumFacing.UP) {
            return false;
        }
        // Increasing y, so the coffin is placed on top of the block that was
        // clicked at except if the block is replaceable
        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();
        boolean replaceable = block.isReplaceable(world, pos);

        if (!replaceable) {
            pos = pos.up();
        }
        // Direction the player is facing
        int direction = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;
        EnumFacing facing = EnumFacing.getHorizontal(direction);
        BlockPos other = pos.offset(facing);
        boolean other_replaceable = block.isReplaceable(world, other);
        boolean flag1 = world.isAirBlock(pos) || replaceable;
        boolean flag2 = world.isAirBlock(other) || other_replaceable;

        if (player.canPlayerEdit(pos, side, stack) && player.canPlayerEdit(other, side, stack)) {
            if (flag1 && flag2 && World.doesBlockHaveSolidTopSurface(world, pos.down()) && World.doesBlockHaveSolidTopSurface(world, other.down())) {
                IBlockState state1 = ModBlocks.coffin.getDefaultState().withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false)).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT).withProperty(BlockDirectional.FACING, facing);
                if (world.setBlockState(pos, state1, 3)) {
                    IBlockState state2 = state1.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
                    if (world.setBlockState(other, state2, 3)) {
                        ((TileCoffin) world.getTileEntity(pos)).otherPos = other;
                        ((TileCoffin) world.getTileEntity(other)).otherPos = pos;
                    }
                }
                --stack.stackSize;
                return true;
            }
        }
        return false;
    }

}
