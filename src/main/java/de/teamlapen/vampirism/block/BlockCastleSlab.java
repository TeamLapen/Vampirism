package de.teamlapen.vampirism.block;

import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * Slab block for Castle Block
 */
public class BlockCastleSlab extends BlockSlab {
	public final static String[] types ={"purpleSlab","darkSlab"};
	public final static String name="castleSlab";
	public final static String doubleName="doubleCastleSlab";
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	public BlockCastleSlab(boolean doubleS) {
		super(doubleS,Material.rock);
		this.setBlockName(name);
		this.setBlockTextureName(REFERENCE.MODID + ":" + BlockCastle.name);
		this.setCreativeTab(VampirismMod.tabVampirism);
		this.setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypePiston);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		meta=meta&7;
		if(meta<0||meta>=types.length){
			meta=0;
		}
		return icons[meta];
	}
	@Override public String func_150002_b(int p_150002_1_) {
		return getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list){
		for(int i=0;i<types.length;i++){
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

	@Override
	protected ItemStack createStackedBlock(int meta)
	{
		return new ItemStack(ModItems.castleSlabItem, 2, meta & 7);
	}

	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ModItems.castleSlabItem;
	}


		@Override
	public String getUnlocalizedName() {
		return String.format("block.%s%s", REFERENCE.MODID.toLowerCase() + ".", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
}
