package de.teamlapen.vampirism.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.ModItems;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

/**
 * Simple block for castles similar to stone bricks
 */
public class BlockCastle extends BasicBlock {
	private static final String[]  types={"purpleBrick","darkBrick"};
	public final static String name="castleBlock";
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	public BlockCastle() {
		super(Material.rock, name);
		this.setBlockTextureName(REFERENCE.MODID + ":" + BlockCastle.name);
		this.setHardness(2.0F);
		setResistance(10.0F);
		setStepSound(soundTypePiston);
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
		return p_149692_1_;
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list)
	{
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
}
