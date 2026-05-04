package de.teamlapen.vampirism.api.world.items.oil;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IWeaponOil extends IApplicableOil {

    /**
     * This method is called before armor reduction
     * <br>
     * Return 0 for no damage modification
     * <br>
     *
     * @return a damage increase that is added to the amount
     */
    float onHit(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source);

    /**
     * This method is called before the damage is applied
     * <br>
     * Return 0 for no damage modification
     * <br>
     *
     * @return a damage increase that is added to the amount
     */
    float onDamage(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source);

    @Override
    default boolean hasDuration() {
        return true;
    }
}
