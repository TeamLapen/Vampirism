package de.teamlapen.vampirism.client.gui.screens.radial;

import de.teamlapen.lib.lib.client.gui.screens.radialmenu.IRadialMenuSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class NoItemRadialMenuSlot<T> implements IRadialMenuSlot<ItemWrapper<T>> {

    private final Function<T, MutableComponent> slotNameFunction;
    private final ItemWrapper<T> primarySlotIcon;
    private final Function<T, Boolean> isEnabled;

    public NoItemRadialMenuSlot(Function<T, MutableComponent> slotNameFunction, ItemWrapper<T> primarySlotIcon, Function<T, Boolean> isEnabled) {
        this.slotNameFunction = slotNameFunction;
        this.primarySlotIcon = primarySlotIcon;
        this.isEnabled = isEnabled;
    }

    @Override
    public Component slotName() {
        return primarySlotIcon().getOptional().map(this.slotNameFunction).map(x -> {
            if (!primarySlotIcon.getOptional().map(isEnabled).orElse(true)) {
                x.withStyle(ChatFormatting.RED);
            }
            return x;
        }).orElse(Component.empty());
    }

    @Override
    public ItemWrapper<T> primarySlotIcon() {
        return this.primarySlotIcon;
    }

    @Override
    public List<ItemWrapper<T>> secondarySlotIcons() {
        return Collections.emptyList();
    }
}
