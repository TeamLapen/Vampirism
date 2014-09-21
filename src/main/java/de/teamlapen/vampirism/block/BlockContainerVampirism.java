package de.teamlapen.vampirism.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import de.teamlapen.vampirism.util.REFERENCE;

public abstract class BlockContainerVampirism extends BlockContainer {

	public BlockContainerVampirism(Material material) {
		super(material);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override 
	public boolean hasTileEntity() {
		return true;
	}
	
	 @Override
     public int getRenderType() {
             return -1;
     }
}
