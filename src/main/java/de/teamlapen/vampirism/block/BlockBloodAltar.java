package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockBloodAltar extends Block {

	public BlockBloodAltar(Material material) {
		super(material);
		this.setBlockName("bloodAltar");
		this.setBlockTextureName("bloodAltar");
	}
	
	public TileEntity createTileEntity(World world, int metadata) {
	   return new TileEntityBloodAltar();
	}

}
