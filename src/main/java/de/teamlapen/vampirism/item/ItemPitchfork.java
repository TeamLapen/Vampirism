package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.util.IItemRegistrable;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public class ItemPitchfork extends ItemSword implements IItemRegistrable{

	public static final String name = "pitchfork";

	public ItemPitchfork() {
		super(Item.ToolMaterial.IRON);
		this.setNoRepair();
		setUnlocalizedName(REFERENCE.MODID+":"+name);
		this.maxStackSize = 1;
		this.setCreativeTab(null);
	}

	@Override
	public String getBaseName() {
		return name;
	}
}
