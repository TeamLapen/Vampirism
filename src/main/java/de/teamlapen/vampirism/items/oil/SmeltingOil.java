package de.teamlapen.vampirism.items.oil;

import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;

public class SmeltingOil extends ApplicableOil {

    public SmeltingOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean canBeApplied(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem;
    }

    @Override
    public boolean hasDuration() {
        return true;
    }

    @Override
    public int getDurationReduction() {
        return 1;
    }
}
