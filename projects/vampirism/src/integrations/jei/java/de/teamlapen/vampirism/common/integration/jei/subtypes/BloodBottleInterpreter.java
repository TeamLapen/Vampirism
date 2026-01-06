package de.teamlapen.vampirism.common.integration.jei.subtypes;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BloodBottleInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final BloodBottleInterpreter INSTANCE = new BloodBottleInterpreter();

    private BloodBottleInterpreter() {
    }

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, @NotNull UidContext context) {
        var bottle = ingredient.get(ModDataComponents.BOTTLE_BLOOD);
        if (bottle == null) {
            return null;
        }
        return bottle.blood();
    }
}
