package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public class WeaponOil extends ApplicableOil implements IWeaponOil {

    public WeaponOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    public boolean canBeApplied(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    @Override
    public float onHit(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        return 0;
    }

    @Override
    public float onDamage(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        return 0;
    }

    @Override
    public int getOilValuePerHit() {
        return 1;
    }
}
