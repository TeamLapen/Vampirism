package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.tileEntity.TileEntityTent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Handles tent placement
 */
public class ItemTent extends BasicItem {
    private static final String TAG = "ItemTent";
    public static final String name = "item_tent";

    public ItemTent() {
        super(name);
        this.setFull3D();
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote || side!=EnumFacing.UP)
            return false;

        pos.add(0,1,0);
        int dir = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;

        boolean flag = placeAt(world, pos, dir, false,false);
        if (flag && !player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
        return flag;
    }




    public static boolean placeAt(World world, BlockPos pos, int dir, boolean force,boolean spawner) {
        int x=pos.getX();
        int y=pos.getY();
        int z=pos.getZ();
        int x1 = x + (dir == 0 ? 1 : (dir == 2 ? -1 : 0));
        int z1 = z + (dir == 1 ? 1 : (dir == 3 ? -1 : 0));
        int x2 = x + (dir == 1 ? -1 : (dir == 2 ? -1 : 1));
        int z2 = z + (dir < 2 ? 1 : -1);
        int x3 = x + (dir == 1 ? -1 : (dir == 3 ? 1 : 0));
        int z3 = z + (dir == 0 ? 1 : (dir == 2 ? -1 : 0));

        Block tent = ModBlocks.blockTent;
        Block main = ModBlocks.blockMainTent;
        if (force || canPlaceAt(main,world,x,y,z) && canPlaceAt(tent, world, x1, y, z1) && canPlaceAt(tent, world, x2, y, z2) && canPlaceAt(tent, world, x3, y, z3)) {
            boolean flag = world.setBlockState(pos, main.getStateFromMeta(dir), 3);
            if (flag) {
                world.setBlockState(new BlockPos(x1, y, z1), tent.getStateFromMeta(dir+4), 3);
                world.setBlockState(new BlockPos(x2, y, z2), tent.getStateFromMeta(dir+8), 3);
                world.setBlockState(new BlockPos(x3, y, z3), tent.getStateFromMeta(dir+12), 3);
                if (spawner)((TileEntityTent) world.getTileEntity(pos)).markAsSpawner();
                return true;
            }
        }
        return false;
    }

    private static boolean canPlaceAt(Block block,World world,int x,int y,int z){
        return block.canPlaceBlockAt(world,new BlockPos(x,y,z));
    }
}
