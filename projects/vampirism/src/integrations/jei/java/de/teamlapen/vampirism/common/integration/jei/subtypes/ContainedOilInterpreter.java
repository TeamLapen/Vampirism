package de.teamlapen.vampirism.common.integration.jei.subtypes;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class ContainedOilInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final ContainedOilInterpreter INSTANCE = new ContainedOilInterpreter();

    private ContainedOilInterpreter() {
    }

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, @NonNull UidContext context) {
        var oil = ingredient.get(ModDataComponents.OIL);
        if (oil == null) {
            return null;
        }
        return oil.oil();
    }
}
