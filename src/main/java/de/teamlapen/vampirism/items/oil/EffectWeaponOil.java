package de.teamlapen.vampirism.items.oil;

import de.teamlapen.vampirism.api.items.oil.IWeaponOil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class EffectWeaponOil extends WeaponOil {

    @Nonnull
    private final MobEffect effect;
    private final Supplier<Integer> effectDuration;

    public EffectWeaponOil(@Nonnull MobEffect effect, @Nonnull Supplier<Integer> effectDuration, int maxDuration) {
        super(effect.getColor(), maxDuration);
        this.effect = Objects.requireNonNull(effect);
        this.effectDuration = Objects.requireNonNull(effectDuration);
    }

    public EffectWeaponOil(@Nonnull MobEffect effect, int effectDuration, int maxDuration) {
        this(effect, ()-> effectDuration, maxDuration);
    }

    @Nonnull
    public MobEffect getEffect() {
        return effect;
    }

    @Nonnull
    public MobEffectInstance getEffectInstance() {
        return new MobEffectInstance(this.effect, this.effectDuration.get());
    }

    @Override
    public float onHit(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        target.addEffect(getEffectInstance());
        return 0;
    }

    @Override
    public void getDescription(ItemStack stack, List<Component> tooltips) {
        tooltips.add(Component.empty());
        tooltips.add(Component.translatable("text.vampirism.oil.effect_on_hit").withStyle(ChatFormatting.DARK_PURPLE));
        tooltips.add(getEffectDescriptionWithDash(getEffectInstance()));
    }

    private Component getEffectDescriptionWithDash(MobEffectInstance instance) {
        MutableComponent component = Component.translatable(instance.getDescriptionId());
        if (instance.getAmplifier() > 0) {
            component = Component.translatable("potion.withAmplifier", component, Component.translatable("potion.potency." + instance.getAmplifier()));
        }

        if (instance.getDuration() > 20) {
            component = Component.translatable("potion.withDuration", component, MobEffectUtil.formatDuration(instance, 1.0f));
        }
        return Component.literal("- ").append(component).withStyle(effect.getCategory().getTooltipFormatting());
    }
}
