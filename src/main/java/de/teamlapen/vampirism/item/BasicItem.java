package de.teamlapen.vampirism.item;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.util.IItemRegistrable;
import de.teamlapen.vampirism.util.IVanillaExt;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class BasicItem extends Item implements IItemRegistrable{
	final String name;

	public BasicItem(String name) {
		this.name=name;
		setCreativeTab(VampirismMod.tabVampirism);
		this.setUnlocalizedName(((this instanceof IVanillaExt)?"":(REFERENCE.MODID + ".")) + name);
	}

	@Override
	public String getBaseName() {
		return name;
	}
}
