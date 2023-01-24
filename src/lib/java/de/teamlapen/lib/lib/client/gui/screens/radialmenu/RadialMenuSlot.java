package de.teamlapen.lib.lib.client.gui.screens.radialmenu;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public record RadialMenuSlot<T>(Component slotName, T primarySlotIcon, List<T> secondarySlotIcons) implements IRadialMenuSlot<T> {

    public RadialMenuSlot(Component slotName, T primarySlotIcon){
        this(slotName, primarySlotIcon, new ArrayList<>());
    }
}
