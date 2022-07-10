package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.text.*;

import javax.annotation.Nonnull;
import java.util.List;
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

    @Override
    public void getDescription(ItemStack stack, List<ITextComponent> tooltips) {
        tooltips.add(StringTextComponent.EMPTY);
        tooltips.add(new TranslationTextComponent("text.vampirism.oil.effect_on_hit").withStyle(TextFormatting.DARK_PURPLE));
        tooltips.add(getEffectDescriptionWithDash(getEffectInstance()));
    }

    private ITextComponent getEffectDescriptionWithDash(EffectInstance instance) {
        IFormattableTextComponent component = new TranslationTextComponent(instance.getDescriptionId());
        if (instance.getAmplifier() > 0) {
            component = new TranslationTextComponent("potion.withAmplifier", component, new TranslationTextComponent("potion.potency." + instance.getAmplifier()));
        }

        if (instance.getDuration() > 20) {
            component = new TranslationTextComponent("potion.withDuration", component, EffectUtils.formatDuration(instance, 1.0f));
        }
        return new StringTextComponent("- ").append(component).withStyle(effect.getCategory().getTooltipFormatting());
    }
}
