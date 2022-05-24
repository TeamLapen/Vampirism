package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class EffectWeaponOil extends WeaponOil {

    @Nonnull
    private final Effect effect;
    private final Supplier<Integer> effectDuration;

    public EffectWeaponOil(@Nonnull Effect effect, @Nonnull Supplier<Integer> effectDuration, int maxDuration) {
        super(effect.getEffect().getColor(), maxDuration);
        this.effect = Objects.requireNonNull(effect);
        this.effectDuration = Objects.requireNonNull(effectDuration);
    }

    public EffectWeaponOil(@Nonnull Effect effect, int effectDuration, int maxDuration) {
        this(effect, ()-> effectDuration, maxDuration);
    }

    @Nonnull
    public Effect getEffect() {
        return effect;
    }

    @Nonnull
    public EffectInstance getEffectInstance() {
        return new EffectInstance(this.effect, this.effectDuration.get());
    }

    @Override
    public float onHit(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        target.addEffect(getEffectInstance());
        return 0;
    }
}
