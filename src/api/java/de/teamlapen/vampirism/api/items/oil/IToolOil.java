package de.teamlapen.vampirism.api.items.oil;


import net.minecraft.item.ItemStack;

public interface IToolOil extends IApplicableOil {

    boolean isCorrectTool(ItemStack stack);

    boolean isOilAllowedOnTool(ItemStack stack);

    @Override
    default boolean canBeApplied(ItemStack stack) {
        return isCorrectTool(stack) && isOilAllowedOnTool(stack);
    }
}
