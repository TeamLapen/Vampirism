package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IOilItem {

    /**
     * @return the applied oil or empty oil
     */
    @NotNull
    IOil getOil(ItemStack stack);

    /**
     * like the {@link net.minecraft.world.item.Item#getDefaultInstance()} with a refined oil instead of an empty oil
     *
     * @param oil oil for the new itemStack
     * @return a new itemStack with oil
     */
    ItemStack withOil(IOil oil);
}
