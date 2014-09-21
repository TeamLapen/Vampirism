package de.teamlapen.vampirism.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.util.Logger;

public class BlockBloodAltar extends BlockContainerVampirism {

	public BlockBloodAltar() {
		super(Material.rock);
		this.setBlockName("bloodAltar");
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		Logger.i("BlockBloodAltar", "createNewTileEntity called");
		return new TileEntityBloodAltar();
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        if (l == 0) 
        	world.setBlockMetadataWithNotify(x, y, z, 0, 2);
        else if (l == 1)   
        	world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        else if (l == 2)
        	world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        else if (l == 3)   
        	world.setBlockMetadataWithNotify(x, y, z, 1, 2);
    }

}
