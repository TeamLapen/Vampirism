package de.teamlapen.vampirism.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {

    @Invoker("canBurn")
    boolean canBurn_vampirism(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> availableItems, int maxStackSize);

    @Invoker("getTotalCookTime")
    static int getTotalCookTime(Level pLevel, AbstractFurnaceBlockEntity pBlockEntity) {
        throw new IllegalStateException("Mixin failed to apply");
    }
}
