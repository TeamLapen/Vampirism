package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IArmorOil;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class EvasionOil extends ApplicableOil implements IArmorOil {

    public EvasionOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    @Override
    public boolean canBeApplied(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @Override
    public boolean hasDuration() {
        return true;
    }

    @Override
    public int getDurationReduction() {
        return 1;
    }

    /**
     * the evasion chance per hit her armor item
     */
    public float evasionChance() {
        return 0.01f;
    }
}
