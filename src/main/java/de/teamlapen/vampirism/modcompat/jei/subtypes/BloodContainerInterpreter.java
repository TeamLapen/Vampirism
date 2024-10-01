package de.teamlapen.vampirism.modcompat.jei.subtypes;

import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.items.component.ContainedFluid;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BloodContainerInterpreter implements ISubtypeInterpreter<ItemStack> {

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, @NotNull UidContext context) {
        return ingredient.get(ModDataComponents.BLOOD_CONTAINER);
    }

    @Override
    public @NotNull String getLegacyStringSubtypeInfo(ItemStack ingredient, @NotNull UidContext context) {
        return Optional.ofNullable(ingredient.get(ModDataComponents.BLOOD_CONTAINER)).map(ContainedFluid::fluid).map(FluidStack::getAmount).map(String::valueOf).orElse("");
    }
}
