package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.block.BlockCoffin;
import de.teamlapen.vampirism.block.BlockCoffinSec;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffin;
import de.teamlapen.vampirism.tileEntity.TileEntityCoffinSec;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * 
 * @author Moritz
 *
 */
public class ItemCoffin extends BasicItem {

	private static final String TAG = "ItemCoffin";
	public static final String name = "coffin";
	private int[][] shiftArray = { { 0, 0, 1 }, { -1, 0, 0 }, { 0, 0, -1 },
			{ 1, 0, 0 } };

	public ItemCoffin() {
		super(name);
	}

	@Override
	public boolean onItemUse(ItemStack item, EntityPlayer player, World world,
			int x, int y, int z, int side, float xOffset, float yOffset,
			float zOffset) {
		if (world.isRemote || side > 1)
			return false;
		y++;
		int direction = MathHelper
				.floor_double((double) ((player.rotationYaw * 4F) / 360F) + 0.5D) & 3;
		Logger.i(TAG, "Direction = " + direction);
		if (world.isAirBlock(x, y, z)) {
			Logger.i(TAG, "Is air block, placing primary coffin block");
			if(world.setBlock(x, y, z, ModBlocks.coffin, direction, 3))
				Logger.i(TAG, "Primary block placement successfull");
			else
				Logger.e(TAG, "Primary block placement failed");
		}
		if (world.isAirBlock(x + shiftArray[direction][0], y
				+ shiftArray[direction][1], z + shiftArray[direction][2])) {
			Logger.i(TAG, "Secondary block air too, placing secondary block");
			if(world.setBlock(x + shiftArray[direction][0], y
					+ shiftArray[direction][1], z + shiftArray[direction][2],
					ModBlocks.coffinSec))
				Logger.i(TAG, "Secondary block placement successfull");
			else
				Logger.e(TAG, "Secondary block placement failed");
			TileEntityCoffin te = (TileEntityCoffin) world.getTileEntity(x,  y,  z);
			if(te != null) {
				te.secondary_x = x + shiftArray[direction][0];
				te.secondary_y = y + shiftArray[direction][1];
				te.secondary_z = z + shiftArray[direction][2];
			}
			TileEntityCoffinSec teSec = ((TileEntityCoffinSec) world
					.getTileEntity(x + shiftArray[direction][0], y
							+ shiftArray[direction][1], z
							+ shiftArray[direction][2]));
			if (teSec != null) {
				teSec.primary_x = x;
				teSec.primary_y = y;
				teSec.primary_z = z;
			}
		}
		return true;
	}
}
