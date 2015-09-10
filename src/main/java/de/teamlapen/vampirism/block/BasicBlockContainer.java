package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class BasicBlockContainer extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BasicBlockContainer(Material material, String name) {
		super(material);
		setCreativeTab(VampirismMod.tabVampirism);
		this.setUnlocalizedName(REFERENCE.MODID + "." + name);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}


	@Override
	public int getRenderType() {
		return -1;
	}


	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		int l = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if (l == 0)
			worldIn.setBlockState(pos,this.getDefaultState().withProperty(FACING,EnumFacing.SOUTH));
		else if (l == 1)
			worldIn.setBlockState(pos, this.getDefaultState().withProperty(FACING, EnumFacing.EAST));
		else if (l == 2)
			worldIn.setBlockState(pos, this.getDefaultState().withProperty(FACING, EnumFacing.NORTH));
		else if (l == 3)
			worldIn.setBlockState(pos, this.getDefaultState().withProperty(FACING, EnumFacing.WEST));
	}


	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing enumfacing = EnumFacing.getFront(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
		{
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing)state.getValue(FACING)).getIndex();
	}

	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING});
	}

	public static EnumFacing getFacing(IBlockState state){
		return (EnumFacing) state.getValue(FACING);
	}
}
