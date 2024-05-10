package de.teamlapen.vampirism.api.components;

import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import net.minecraft.core.Holder;

/**
 * Interface for item components. An item with this component may have an oil applied to it.
 * <p>
 * For storing {@link de.teamlapen.vampirism.api.items.oil.IOil} in an item (use as an oil bottle) use {@link IOilContent}
 *
 */
public interface IAppliedOilContent {

    /**
     * The oil applied to the item
     */
    Holder<IApplicableOil> oil();

    /**
     * The remaining duration the oil is applied to the item
     */
    int duration();
}
