package de.teamlapen.vampirism.api.world.items.components;

import de.teamlapen.vampirism.api.world.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import net.minecraft.core.Holder;

/**
 * Interface for item components. An item with this component may have an oil applied to it.
 * <p>
 * For storing {@link IOil} in an item (use as an oil bottle) use {@link IOilContent}
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
