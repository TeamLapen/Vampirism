package de.teamlapen.vampirism.api.world.items.oil;

import net.minecraft.world.item.ItemInstance;

public interface IToolOil extends IApplicableOil {

    boolean isCorrectTool(ItemInstance stack);

    boolean isOilAllowedOnTool(ItemInstance stack);

    @Override
    default boolean canBeApplied(ItemInstance stack) {
        return isCorrectTool(stack) && isOilAllowedOnTool(stack);
    }
}
