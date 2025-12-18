package de.teamlapen.vampirism.misc.injection;

import de.teamlapen.vampirism.misc.extension.IItemStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

@Deprecated
public interface IItemStackVampirismMock extends IItemStack {
    @Override
    default <T> ItemStack vampirism$with(DataComponentType<T> type, T value) {
        throw new IllegalStateException("This class is only supported as injection class");
    }

    @Override
    default <T> ItemStack vampirism$with(Supplier<DataComponentType<T>> type, T value) {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
