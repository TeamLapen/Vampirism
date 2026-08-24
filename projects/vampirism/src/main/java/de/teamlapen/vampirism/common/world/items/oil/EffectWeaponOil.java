package de.teamlapen.vampirism.common.world.items.oil;

import de.teamlapen.vampirism.api.world.items.oil.IWeaponOil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EffectWeaponOil extends WeaponOil {

    private final Holder<MobEffect> effect;
    private final Supplier<Integer> effectDuration;
    private final int amplifier;

    public EffectWeaponOil(Holder<MobEffect> effect, Supplier<Integer> effectDuration, int maxDuration, int amplifier) {
        super(0, maxDuration);
        this.effect = Objects.requireNonNull(effect);
        this.effectDuration = Objects.requireNonNull(effectDuration);
        this.amplifier = amplifier;
    }

    public EffectWeaponOil(Holder<MobEffect> effect, Supplier<Integer> effectDuration, int maxDuration) {
        this(effect, effectDuration, maxDuration, 0);
    }

    public EffectWeaponOil(Holder<MobEffect> effect, int effectDuration, int maxDuration, int amplifier) {
        this(effect, () -> effectDuration, maxDuration, amplifier);
    }

    public EffectWeaponOil(Holder<MobEffect> effect, int effectDuration, int maxDuration) {
        this(effect, () -> effectDuration, maxDuration, 0);
    }

    @Override
    public int getColor() {
        return this.effect.value().getColor();
    }

    public MobEffect getEffect() {
        return effect.value();
    }

    public MobEffectInstance getEffectInstance() {
        return new MobEffectInstance(this.effect, this.effectDuration.get(), this.amplifier);
    }

    @Override
    public float onHit(ItemStack stack, float amount, IWeaponOil oil, LivingEntity target, LivingEntity source) {
        target.addEffect(getEffectInstance());
        return 0;
    }

    @Override
    public void getDescription(ItemStack stack, @Nullable Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltips) {
        super.getDescription(stack, context, display, tooltips);
        tooltips.accept(Component.empty());
        tooltips.accept(Component.translatable("tooltip.vampirism.oil.weapon_effect_on_hit").withStyle(ChatFormatting.DARK_PURPLE));
        tooltips.accept(getEffectDescriptionWithSpace(getEffectInstance(), context));
    }

    private Component getEffectDescriptionWithSpace(MobEffectInstance instance, @Nullable Item.TooltipContext context) {
        MutableComponent component = Component.translatable(instance.getDescriptionId());
        if (instance.getAmplifier() > 0) {
            component = Component.translatable("potion.withAmplifier", component, Component.translatable("potion.potency." + instance.getAmplifier()));
        }

        if (instance.getDuration() > 20 && context != null) {
            component = Component.translatable("potion.withDuration", component, MobEffectUtil.formatDuration(instance, 1.0f, context.tickRate()));
        }
        return Component.literal(" ").append(component).withStyle(getEffect().getCategory().getTooltipFormatting());
    }
}
