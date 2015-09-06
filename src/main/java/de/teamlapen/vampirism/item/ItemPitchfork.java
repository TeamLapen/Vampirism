package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ItemPitchfork extends ItemSword {

	public static final String name = "pitchfork";

	public ItemPitchfork() {
		super(Item.ToolMaterial.IRON);
		this.setNoRepair();
		setUnlocalizedName(name);
		this.maxStackSize = 1;
		this.setCreativeTab(null);
	}

	@Override
	public String getUnlocalizedName() {
		return String.format("item.%s%s", REFERENCE.MODID.toLowerCase() + ".", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack) {
		return this.getUnlocalizedName();
	}

	protected String getUnwrappedUnlocalizedName(String unlocalizedName) {
		return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
	}
}
