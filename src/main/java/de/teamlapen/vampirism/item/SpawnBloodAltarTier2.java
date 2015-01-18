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
public class SpawnBloodAltarTier2 extends BasicItem {
	public static String name;

	public SpawnBloodAltarTier2(String name) {
		super(name);
		SpawnBloodAltarTier2.name = name;
	}
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{	
		if (world.getBlock(x, y, z) == Blocks.chest)
			return false;
		
		// TODO: adjust x & z so it doesn't spawn on player
		x += 1;
		z += 1;
		
		return world.setBlock(x + 0, y + 0, z + 0, ModBlocks.bloodAltarTier2, 0, 3);
	}
}
