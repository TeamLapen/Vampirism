package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * Slab block for Castle Block
 */
public abstract class BlockCastleSlab extends BlockSlab {


	public static final PropertyEnum VARIANT = PropertyEnum.create("variant",EnumType.class);
	public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");

	@Override
	public String getUnlocalizedName(int meta) {
		return super.getUnlocalizedName();
	}


	@Override
	public IProperty getVariantProperty() {
		return VARIANT;
	}

	@Override
	public Object getVariant(ItemStack stack) {
		return EnumType.byMetadata(stack.getMetadata()&7);
	}

	public enum EnumType implements IStringSerializable{
		PURPLE(0,"castleBlock_purpleBrick"),
		DARK(1,"castleBlock_darkBrick");
		private int meta;
		private String name;

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];

		static {
			EnumType[] values = values();
			for(int i=0;i<values.length;i++){
				META_LOOKUP[i]=values[i];
			}
		}

		EnumType(int meta, String name) {
			this.meta = meta;
			this.name = name;
		}

		public String getName(){
			return this.name;
		}

		public String toString(){
			return getName();
		}

		public int getMetadata(){
			return meta;
		}
		public static EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= META_LOOKUP.length)
			{
				meta = 0;
			}

			return META_LOOKUP[meta];
		}


	}

	public final static String name="castleBlock_slab";
	public final static String doubleName="castleBlock_double_slab";

	public BlockCastleSlab() {
		super(Material.rock);
		this.setUnlocalizedName(REFERENCE.MODID + "." + BlockCastle.name);

		this.setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypePiston);
		IBlockState blockState = this.blockState.getBaseState();
		if(isDouble()){
			blockState=blockState.withProperty(SEAMLESS,Boolean.valueOf(false));
		}
		else{
			blockState=blockState.withProperty(HALF, EnumBlockHalf.BOTTOM);
			this.setCreativeTab(VampirismMod.tabVampirism);
		}
		this.useNeighborBrightness = !this.isDouble();
		this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.PURPLE));
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ModBlocks.castleSlab);
	}


	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta & 7));
		if(isDouble()){
			iblockstate=iblockstate.withProperty(SEAMLESS,Boolean.valueOf((meta&8)!=0));
		}
		else{
			iblockstate=iblockstate.withProperty(HALF,(meta&8)==0? EnumBlockHalf.BOTTOM:EnumBlockHalf.TOP);
		}
		return iblockstate;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		byte b=0;
		int i=b|((EnumType)state.getValue(VARIANT)).getMetadata();
		if(isDouble()){
			if((Boolean)state.getValue(SEAMLESS)){
				i |=8;
			}
		}
		else if(state.getValue(HALF)==EnumBlockHalf.TOP){
			i |=8;
		}
		return i;
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list){
		for(int i=0;i<EnumType.values().length;i++){
			list.add(new ItemStack(item,1,i));
		}
	}

	@Override
	protected BlockState createBlockState() {
		return isDouble()?new BlockState(this,new IProperty[]{SEAMLESS, VARIANT}):new BlockState(this,new IProperty[]{HALF, VARIANT});
	}

	@Override
	public int damageDropped(IBlockState state) {
		return ((EnumType)state.getValue(VARIANT)).getMetadata();
	}
}
