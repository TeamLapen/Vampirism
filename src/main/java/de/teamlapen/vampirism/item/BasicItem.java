package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class BasicItem extends Item {

	public BasicItem(String name) {

		setCreativeTab(VampirismMod.tabVampirism);
		this.setUnlocalizedName(REFERENCE.MODID + "." + name);

		GameRegistry.registerItem(this, name);
	}
}
