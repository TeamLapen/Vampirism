package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.tileEntity.TileEntityTent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
    }

    public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
        if (world.isRemote || side > 1)
            return false;

        y++;
        int dir = MathHelper.floor_double((player.rotationYaw * 4F) / 360F + 0.5D) & 3;

        boolean flag = placeAt(world, x, y, z, dir, false);
        if (flag && !player.capabilities.isCreativeMode) {
            item.stackSize--;
        }
        return flag;
    }

    public static boolean placeAt(World world, int x, int y, int z, int dir, boolean force) {
        int x1 = x + (dir == 0 ? 1 : (dir == 2 ? -1 : 0));
        int z1 = z + (dir == 1 ? 1 : (dir == 3 ? -1 : 0));
        int x2 = x + (dir == 1 ? -1 : (dir == 2 ? -1 : 1));
        int z2 = z + (dir < 2 ? 1 : -1);
        int x3 = x + (dir == 1 ? -1 : (dir == 3 ? 1 : 0));
        int z3 = z + (dir == 0 ? 1 : (dir == 2 ? -1 : 0));

        Block tent = ModBlocks.blockTent;
        Block main = ModBlocks.blockMainTent;
        if (force || main.canPlaceBlockAt(world, x, y, z) && tent.canPlaceBlockAt(world, x1, y, z1) && tent.canPlaceBlockAt(world, x2, y, z2) && tent.canPlaceBlockAt(world, x3, y, z3)) {
            boolean flag = world.setBlock(x, y, z, main, dir, 3);
            if (flag) {
                world.setBlock(x1, y, z1, tent, dir + 4, 3);
                world.setBlock(x2, y, z2, tent, dir + 8, 3);
                world.setBlock(x3, y, z3, tent, dir + 12, 3);
                ((TileEntityTent) world.getTileEntity(x, y, z)).markAsSpawner();
                return true;
            }
        }
        return false;
    }
}
