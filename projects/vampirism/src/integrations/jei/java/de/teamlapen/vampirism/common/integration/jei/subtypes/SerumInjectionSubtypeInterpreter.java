package de.teamlapen.vampirism.common.integration.jei.subtypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jspecify.annotations.Nullable;

public class SerumInjectionSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final SerumInjectionSubtypeInterpreter INSTANCE = new SerumInjectionSubtypeInterpreter();

    private SerumInjectionSubtypeInterpreter() {

    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        PotionContents contents = ingredient.get(DataComponents.POTION_CONTENTS);
        if (contents == null) {
            return null;
        }
        return contents.potion()
                .orElse(null);
    }
}
