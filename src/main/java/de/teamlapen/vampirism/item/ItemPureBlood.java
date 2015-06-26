package de.teamlapen.vampirism.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.VampirismMod;

public class ItemPureBlood extends BasicItem {

	public static final String name = "pureBlood";
	private static final int COUNT = 5;
	public IIcon[] icons = new IIcon[COUNT];

	public ItemPureBlood() {
		super(name);
		this.setHasSubtypes(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumChatFormatting.RED + VampirismMod.proxy.translateToLocal("text.vampirism:purity") + ": " + (itemStack.getItemDamage() + 1) + "/" + COUNT);
	}

	@Override
	public IIcon getIconFromDamage(int meta) {
		if (meta >= COUNT)
			meta = 0;

		return this.icons[meta];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < COUNT; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		for (int i = 0; i < COUNT; i++) {
			icons[i] = iconRegister.registerIcon(getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1) + "_" + i);
		}
	}

}
