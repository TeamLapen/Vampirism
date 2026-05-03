package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.vampirism.misc.extension.IItemStack;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IItemStack {


    @Shadow
    @Nullable
    public abstract <T> T set(DataComponentType<T> type, @org.jetbrains.annotations.Nullable T value);

    @Override
    public <T> ItemStack vampirism$with(DataComponentType<T> type, T value) {
        set(type, value);
        return (ItemStack) (Object) this;
    }

    @Override
    public <T> ItemStack vampirism$with(Supplier<DataComponentType<T>> type, T value) {
        set(type.get(), value);
        return (ItemStack) (Object) this;
    }
}
