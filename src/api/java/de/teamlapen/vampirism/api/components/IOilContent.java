package de.teamlapen.vampirism.api.components;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.core.Holder;

/**
 * Item Component to hold stored oil.
 * <p>
 * Used by the oil bottle
 */
public interface IOilContent {

    /**
     * The oil contained in the item
     */
    Holder<IOil> oil();

    IOilContent withOil(Holder<IOil> oil);
}
