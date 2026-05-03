package de.teamlapen.vampirism.common.integration.jei.subtypes;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jspecify.annotations.Nullable;

public class RitualKnifeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final RitualKnifeInterpreter INSTANCE = new RitualKnifeInterpreter();

    private RitualKnifeInterpreter() {

    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        return ingredient.getOrDefault(ModDataComponents.CHARGED_RITUAL_KNIFE, false);
    }
}
