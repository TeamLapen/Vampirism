package de.teamlapen.faction.api.world.entities.minion.tasks;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Groups multiple {@link IMinionTask}s together.
 * <p>
 * In selection menus a category is displayed as a single entry. Its sub tasks are only revealed once the category
 * entry is hovered. The task that is marked with {@link MinionTaskProperties#categoryDefault(net.minecraft.core.Holder)}
 * is activated when the category itself is selected, all other tasks of the category have to be selected explicitly.
 * <p>
 * A category is a registry object and therefore a "Singleton" class.
 */
public interface IMinionTaskCategory {

    String getDescriptionId();

    default MutableComponent getName() {
        return Component.translatable(getDescriptionId());
    }
}
