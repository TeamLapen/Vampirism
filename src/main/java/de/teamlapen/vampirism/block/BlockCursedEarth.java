package de.teamlapen.vampirism.block;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.IPlantable;

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
		this.setUnlocalizedName(name);
	}


	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		return plantable instanceof BlockBush || plantable instanceof BlockFlower;
	}
}
