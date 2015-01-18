package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.util.ModBlocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * 
 * @author WILLIAM
 *
 */
public class SpawnBloodAltar extends BasicItem {
	public static String name;
	
	public SpawnBloodAltar(String name) {
		super(name);
		SpawnBloodAltar.name = name;
	}
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{	
		if (world.getBlock(x, y, z) == Blocks.chest)
			return false;
		
		// TODO: adjust x & z so it doesn't spawn on player
		x += 1;
		z += 1;
		
		return world.setBlock(x, y + 1, z, ModBlocks.bloodAltar, 0, 3);
	}
}
