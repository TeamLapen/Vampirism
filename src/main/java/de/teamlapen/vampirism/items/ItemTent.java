package de.teamlapen.vampirism.items;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.blocks.BlockTent;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.tileentity.TileTent;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


/**
 * Item used to place a tent
 */
public class ItemTent extends VampirismItem {
    private static final String name = "item_tent";

    public static boolean placeAt(World world, BlockPos pos, EnumFacing dir, boolean force, boolean spawner) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int x1 = x + (dir == EnumFacing.SOUTH ? 1 : (dir == EnumFacing.NORTH ? -1 : 0));
        int z1 = z + (dir == EnumFacing.WEST ? 1 : (dir == EnumFacing.EAST ? -1 : 0));
        int x2 = x + (dir == EnumFacing.WEST ? -1 : (dir == EnumFacing.NORTH ? -1 : 1));
        int z2 = z + (dir == EnumFacing.SOUTH || dir == EnumFacing.WEST ? 1 : -1);
        int x3 = x + (dir == EnumFacing.WEST ? -1 : (dir == EnumFacing.EAST ? 1 : 0));
        int z3 = z + (dir == EnumFacing.SOUTH ? 1 : (dir == EnumFacing.NORTH ? -1 : 0));

        Block tent = ModBlocks.tent;
        Block main = ModBlocks.tent_main;
        if (force || canPlaceAt(tent, world, x, y, z) && canPlaceAt(tent, world, x1, y, z1) && canPlaceAt(tent, world, x2, y, z2) && canPlaceAt(tent, world, x3, y, z3)) {
            boolean flag = world.setBlockState(pos, main.getDefaultState().withProperty(BlockTent.FACING, dir.getOpposite()), 3);
            if (flag) {
                world.setBlockState(new BlockPos(x1, y, z1), tent.getDefaultState().withProperty(BlockTent.FACING, dir).withProperty(BlockTent.POSITION, 1), 3);
                world.setBlockState(new BlockPos(x2, y, z2), tent.getDefaultState().withProperty(BlockTent.FACING, dir).withProperty(BlockTent.POSITION, 2), 3);
                world.setBlockState(new BlockPos(x3, y, z3), tent.getDefaultState().withProperty(BlockTent.FACING, dir.getOpposite()).withProperty(BlockTent.POSITION, 3), 3);
                if (spawner) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile instanceof TileTent) {
                        ((TileTent) tile).setSpawn(true);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static boolean canPlaceAt(Block block, World world, int x, int y, int z) {
        return block.canPlaceBlockAt(world, new BlockPos(x, y, z));
    }

    public ItemTent() {
        super(name);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, playerIn, tooltip, advanced);
        if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("spawner")) {
            tooltip.add(UtilLib.translate("tile.vampirism.tent.spawner"));
        }
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        super.getSubItems(itemIn, tab, subItems);
        ItemStack spawner = new ItemStack(this);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("spawner", true);
        spawner.setTagCompound(nbt);
        subItems.add(spawner);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

        if (facing != EnumFacing.UP)
            return EnumActionResult.PASS;
        if (world.isRemote) return EnumActionResult.PASS;

        ItemStack stack = player.getHeldItem(hand);

        EnumFacing dir = EnumFacing.fromAngle(player.rotationYaw);
        boolean flag = placeAt(world, pos.up(), dir, false, false);
        if (flag) {
            TileEntity tile = world.getTileEntity(pos.up());
            if (tile instanceof TileTent) {
                if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("spawner")) {
                    ((TileTent) tile).setSpawn(true);
                }
            }

            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }
        return flag ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
    }


}
