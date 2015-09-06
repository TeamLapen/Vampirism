package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.item.ItemMetaBlock;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IIcon;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * Simple block for castles similar to stone bricks
 */
public class BlockCastle extends BasicBlock implements ItemMetaBlock.IMetaBlockName{

	public static final PropertyEnum TYPE= PropertyEnum.create("type",EnumType.class);

	@Override
	public String getSpecialName(ItemStack stack) {
		return EnumType.fromID(stack.getItemDamage()).getName();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
		return new ItemStack(Item.getItemFromBlock(this),1,this.getMetaFromState(world.getBlockState(pos)));
	}

	public enum EnumType implements IStringSerializable{
		PURPLE(0,"purpleBrick"),
		DARK(1,"darkBrick"),
		DARK_BLOODY(2,"darkBrickBloody");

		private int id;
		private String name;

		EnumType(int id, String name) {
			this.id = id;
			this.name = name;
		}


		@Override
		public String toString() {
			return getName();
		}

		@Override
		public String getName() {
			return name;
		}

		public static EnumType fromID(int id){
			for(EnumType t:EnumType.values()){
				if(t.id==id)return t;
			}
			return PURPLE;
		}
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE,EnumType.fromID(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumType)state.getValue(TYPE)).id;
	}

	public final static String name="castleBlock";
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;


	public BlockCastle(){
		super(Material.rock, name);
		this.setBlockTextureName(REFERENCE.MODID + ":" + BlockCastle.name);
		this.setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypePiston);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE,EnumType.PURPLE));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if (meta < 0 || meta >= icons.length)
		{
			meta = 0;
		}

		return this.icons[meta];
	}

	public int damageDropped(int p_149692_1_)
	{
		//Do not drop the bloody stone
		if(p_149692_1_==2)return 1;
		return p_149692_1_;
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list)
	{
		for(int i=0;i<EnumType.values().length;i++){
			list.add(new ItemStack(item,1,i));
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.icons = new IIcon[types.length];

		for (int i = 0; i < this.icons.length; ++i)
		{
			String s = this.getTextureName();
				s = s + "_" + types[i];

			this.icons[i] = p_149651_1_.registerIcon(s);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override public void randomDisplayTick(World world, BlockPos pos,IBlockState state, Random rand) {
		if(state.getValue(TYPE).equals(EnumType.DARK_BLOODY)){
			if(rand.nextInt(180)==0){
				world.playSound(pos.getX(),pos.getY(),pos.getZ(),"vampirism:ambient.castle",0.8F,1.0F,false);
			}

		}
	}
}
