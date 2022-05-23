package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import javax.annotation.Nonnull;
import java.util.Objects;

public class EffectWeaponOil extends WeaponOil {

    @Nonnull
    private final EffectInstance effectIns;
    @Nonnull
    private final Effect effect;

    public EffectWeaponOil(@Nonnull EffectInstance effect, int maxDuration) {
        super(effect.getEffect().getColor(), maxDuration);
        this.effectIns = effect;
        this.effect = Objects.requireNonNull(effect.getEffect());
    }

    @Nonnull
    public Effect getEffect() {
        return effect;
    }

    @Nonnull
    public EffectInstance getEffectInstance() {
        return new EffectInstance(effectIns);
    }

    @Override
    public float onHit(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        target.addEffect(getEffectInstance());
        return 0;
    }
}
