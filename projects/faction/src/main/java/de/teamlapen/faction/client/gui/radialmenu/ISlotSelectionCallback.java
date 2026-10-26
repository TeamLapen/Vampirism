package de.teamlapen.faction.client.gui.radialmenu;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ISlotSelectionCallback<T> {
    /**
     * @param slot          the selected item
     * @param parentSlot if a slot in the second row is selected, the parentSlot holds the parent item
     */
    void accept(T slot, @Nullable T parentSlot);
}
