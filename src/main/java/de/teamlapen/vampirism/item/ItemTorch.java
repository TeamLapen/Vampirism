package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemTorch extends BasicItem {
	public IIcon bigIcon;
	public static final String name = "torch";
	
	public ItemTorch() {
		super("torch");
		this.setNoRepair();
		this.maxStackSize = 1;
		setCreativeTab(VampirismMod.tabVampirism);
	}

}
