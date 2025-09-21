package de.teamlapen.vampirism.common.integration.jei.subtypes;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
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
        return Optional.ofNullable(ingredient.get(ModDataComponents.BLOOD_CONTAINER)).map(SimpleFluidContent::getAmount).map(String::valueOf).orElse("");
    }
}
