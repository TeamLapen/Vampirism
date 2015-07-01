package de.teamlapen.vampirism.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * 
 * @author WILLIAM
 *
 */
public class BlockCursedEarth extends BasicBlock {

	public final static String name = "cursedEarth";

	public BlockCursedEarth() {
		super(Material.ground, name);
		this.setHardness(0.5F);
		this.setResistance(2.0F);
		this.setHarvestLevel("shovel", 0);
		this.setStepSound(soundTypeGravel);
		this.setBlockTextureName(REFERENCE.MODID + ":" + name);
	}

	/**
	 * Determines if this block can support the passed in plant, allowing it to be planted and grow. Some examples: Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water Cacti
	 * checks if its a cacti, or if its sand Nether types check for soul sand Crops check for tilled soil Caves check if it's a solid surface Plains check if its grass or dirt Water check if its still
	 * water
	 *
	 * @param world
	 *            The current world
	 * @param x
	 *            X Position
	 * @param y
	 *            Y Position
	 * @param z
	 *            Z position
	 * @param direction
	 *            The direction relative to the given position the plant wants to be, typically its UP
	 * @param plantable
	 *            The plant that wants to check
	 * @return True to allow the plant to be planted/stay.
	 */
	// Added so plants can grow on this block (trees too)
	@Override
	public boolean canSustainPlant(IBlockAccess world, int x, int y, int z, ForgeDirection direction, IPlantable plantable) {
		return plantable instanceof BlockBush || plantable instanceof BlockFlower;
	}
}
