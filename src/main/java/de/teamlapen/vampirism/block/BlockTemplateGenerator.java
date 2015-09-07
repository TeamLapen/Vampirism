package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.tileEntity.TileEntityTemplateGenerator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Simple block for {@link TileEntityTemplateGenerator}
 *
 */
public class BlockTemplateGenerator extends BlockContainer {
	public static final String name="templateGenerator";

	public BlockTemplateGenerator() {
		super(Material.iron);
	}

	@Override public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityTemplateGenerator();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(worldIn.isRemote)return false;
		ItemStack stack=playerIn.getCurrentEquippedItem();
		int minX=0;
		if(stack!=null&&stack.getItem().equals(Items.apple)){
			minX=-stack.stackSize;
		}
		else if(stack!=null&&stack.getItem().equals(Items.bone)){
			minX=stack.stackSize;
		}
		((TileEntityTemplateGenerator)worldIn.getTileEntity(pos)).onActivated(minX);
		return true;
	}

}
