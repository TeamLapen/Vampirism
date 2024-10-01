package de.teamlapen.vampirism.modcompat.jei.subtypes;

import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.items.component.OilContent;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ContainedOilInterpreter implements ISubtypeInterpreter<ItemStack> {

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.get(ModDataComponents.OIL);
    }

    @Override
    public @NotNull String getLegacyStringSubtypeInfo(ItemStack ingredient, @NotNull UidContext context) {
        return Optional.ofNullable(ingredient.get(ModDataComponents.OIL)).map(OilContent::oil).flatMap(Holder::unwrapKey).map(ResourceKey::location).map(ResourceLocation::toString).orElse("");
    }
}
