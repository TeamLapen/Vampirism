package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.item.ItemStack;

public interface IOilItem {

    IOil getOil(ItemStack stack);
}
