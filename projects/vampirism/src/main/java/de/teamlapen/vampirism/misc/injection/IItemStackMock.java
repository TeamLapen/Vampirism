package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IItemStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public interface IItemStackMock extends IItemStack {
    @Override
    default <T> ItemStack vampirism$with(DataComponentType<T> type, T value) {
        return null;
    }

    @Override
    default <T> ItemStack vampirism$with(Supplier<DataComponentType<T>> type, T value) {
        return null;
    }
}
