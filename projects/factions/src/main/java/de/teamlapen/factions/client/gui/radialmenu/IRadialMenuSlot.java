package de.teamlapen.factions.client.gui.radialmenu;

import net.minecraft.network.chat.Component;

import java.util.List;

public interface IRadialMenuSlot<T> {

    Component slotName();

    T primarySlotIcon();

    List<T> secondarySlotIcons();
}
