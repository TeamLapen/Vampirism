package de.teamlapen.vampirism.misc.extension;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public interface IItemStack {

    <T> ItemStack vampirism$with(DataComponentType<T> type, T value);

    <T> ItemStack vampirism$with(Supplier<DataComponentType<T>> type, T value);
}
