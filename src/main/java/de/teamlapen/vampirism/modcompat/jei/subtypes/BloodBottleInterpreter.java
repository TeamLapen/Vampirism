package de.teamlapen.vampirism.modcompat.jei.subtypes;

import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.items.component.BottleBlood;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BloodBottleInterpreter implements ISubtypeInterpreter<ItemStack> {

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, @NotNull UidContext context) {
        return ingredient.get(ModDataComponents.BOTTLE_BLOOD);
    }

    @Override
    public @NotNull String getLegacyStringSubtypeInfo(ItemStack ingredient, @NotNull UidContext context) {
        return Optional.ofNullable(ingredient.get(ModDataComponents.BOTTLE_BLOOD)).map(BottleBlood::blood).map(Object::toString).orElse("");
    }
}
