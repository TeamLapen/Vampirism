package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.item.ItemStack;

public interface IOilItem {

    IOil getOil(ItemStack stack);

    /**
     * like the {@link net.minecraft.item.Item#getDefaultInstance()} with a refined oil instead of an empty oil
     *
     * @param oil oil for the new itemStack
     * @return itemStack with oil
     */
    ItemStack withOil(IOil oil);
}
