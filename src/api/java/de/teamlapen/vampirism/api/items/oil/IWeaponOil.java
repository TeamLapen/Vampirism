package de.teamlapen.vampirism.api.items.oil;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IWeaponOil extends IApplicableOil {

    /**
     * This method is called before armor reduction
     * <br>
     * Return 0 for no damage modification
     * <br>
     * Called in {@link net.minecraftforge.common.ForgeHooks#onLivingHurt(net.minecraft.entity.LivingEntity, net.minecraft.util.DamageSource, float)}
     *
     * @return a damage increase that is added to the amount
     */
    float onHit(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source);

    /**
     * This method is called before the damage is applied
     * <br>
     * Return 0 for no damage modification
     * <br>
     * called in {@link net.minecraftforge.common.ForgeHooks#onLivingDamage(net.minecraft.entity.LivingEntity, net.minecraft.util.DamageSource, float)}
     *
     * @return a damage increase that is added to the amount
     */
    float onDamage(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source);

    @Override
    default boolean hasDuration() {
        return true;
    }
}
