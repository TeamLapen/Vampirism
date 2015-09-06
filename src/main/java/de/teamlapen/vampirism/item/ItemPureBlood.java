package de.teamlapen.vampirism.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

public class ItemPureBlood extends BasicItem {

	public static final String name = "pureBlood";
	public static final int COUNT = 5;

	public ItemPureBlood() {
		super(name);
		this.setHasSubtypes(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("text.vampirism.purity") + ": " + (itemStack.getItemDamage() + 1) + "/" + COUNT);
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < COUNT; i++) {
			list.add(new ItemStack(item, 1, i));
		}
	}


}
