package de.teamlapen.vampirism.common.integration.jei.subtypes;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.world.items.component.RitualKnifeType;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class RitualKnifeInterpreter implements ISubtypeInterpreter<ItemStack> {
    public static final RitualKnifeInterpreter INSTANCE = new RitualKnifeInterpreter();

    private RitualKnifeInterpreter() {

    }

    @Override
    @Nullable
    public Object getSubtypeData(ItemStack ingredient, UidContext context) {
        boolean charged = ingredient.getOrDefault(ModDataComponents.CHARGED_RITUAL_KNIFE, false);
        RitualKnifeType type = ingredient.get(ModDataComponents.RITUAL_KNIFE_TYPE);
        return charged + "|" + (type == null ? "none" : type.getSerializedName());
    }
}
