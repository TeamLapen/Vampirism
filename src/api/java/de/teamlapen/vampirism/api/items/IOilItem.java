package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public interface IOilItem {

    /**
     * @return the applied oil or empty oil
     */
    @Nonnull
    IOil getOil(ItemStack stack);

    /**
     * like the {@link net.minecraft.item.Item#getDefaultInstance()} with a refined oil instead of an empty oil
     *
     * @param oil oil for the new itemStack
     * @return a new itemStack with oil
     */
    ItemStack withOil(IOil oil);
}
