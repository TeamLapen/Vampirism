package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.tileEntity.TileEntityTemplateGenerator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Simple block for {@link TileEntityTemplateGenerator}
 *
 */
public class BlockTemplateGenerator extends BlockContainer {
	public static final String name="templateGenerator";

	public BlockTemplateGenerator() {
		super(Material.iron);
		this.setBlockTextureName("stone");
	}

	@Override public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityTemplateGenerator();
	}

	@Override public boolean onBlockActivated(World p_149727_1_, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_,
			float p_149727_9_) {
		if(p_149727_1_.isRemote)return false;
		ItemStack stack=player.getCurrentEquippedItem();
		int minX=0;
		if(stack!=null&&stack.getItem().equals(Items.apple)){
			minX=-stack.stackSize;
		}
		else if(stack!=null&&stack.getItem().equals(Items.bone)){
			minX=stack.stackSize;
		}
		 ((TileEntityTemplateGenerator)p_149727_1_.getTileEntity(x,y,z)).onActivated(minX);
		return true;
	}
}
