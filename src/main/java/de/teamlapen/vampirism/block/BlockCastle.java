package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.item.ItemMetaBlock;
import de.teamlapen.vampirism.util.IBlockRegistrable;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
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
public class BlockCastle extends BasicBlock implements ItemMetaBlock.IMetaBlockName,IBlockRegistrable{

	public static final PropertyEnum TYPE= PropertyEnum.create("type",EnumType.class);

	@Override
	public String getSpecialName(ItemStack stack) {
		return null;//EnumType.fromID(stack.getItemDamage()).getName();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
		return new ItemStack(Item.getItemFromBlock(this),1,this.getMetaFromState(world.getBlockState(pos)));
	}

	@Override
	public String[] getVariantsToRegister() {
		String[] variants=new String[EnumType.ID_LOOKUP.length];
		for(int i=0;i<variants.length;i++){
			variants[i]=name+"_"+EnumType.ID_LOOKUP[i].getName();
		}
		return variants;
	}

	@Override
	public boolean shouldRegisterSimpleItem() {
		return false;
	}


	public enum EnumType implements IStringSerializable{
		PURPLE(0,"purpleBrick"),
		DARK(1,"darkBrick"),
		DARK_BLOODY(2,"darkBrickBloody");

		private final static EnumType[] ID_LOOKUP;
		static {
			ID_LOOKUP=values();
		}
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
			if(id<ID_LOOKUP.length){
				return ID_LOOKUP[id];
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


	public BlockCastle(){
		super(Material.rock, name);
		this.setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypePiston);
		this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE,EnumType.PURPLE));
	}


	@Override
	public int damageDropped(IBlockState state) {
		if(state.getValue(TYPE)==EnumType.DARK_BLOODY)return EnumType.DARK.id;
		return ((EnumType)state.getValue(TYPE)).id;
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list)
	{
		for(int i=0;i<EnumType.ID_LOOKUP.length;i++){
			list.add(new ItemStack(item, 1, i));
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
