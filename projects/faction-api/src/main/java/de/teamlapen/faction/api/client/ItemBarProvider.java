package de.teamlapen.faction.api.client;

import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

@FunctionalInterface
public interface ItemBarProvider {

    void addBars(ItemStack stack, Consumer<ItemBar> bars);
}
