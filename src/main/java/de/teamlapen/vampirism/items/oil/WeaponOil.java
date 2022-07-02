package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class WeaponOil extends ApplicableOil implements IWeaponOil {

    public WeaponOil(int color, int maxDuration) {
        super(color, maxDuration);
    }

    public boolean canBeApplied(ItemStack stack) {
        return stack.getItem() instanceof SwordItem && stack.is(ModTags.Items.APPLICABLE_OIL_SWORD) == VampirismConfig.BALANCE.itApplicableOilSwordReverse.get();
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
    public int getDurationReduction() {
        return 1;
    }
}
