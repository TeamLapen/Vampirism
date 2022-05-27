package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;


public class ThirstEffect extends VampirismEffect {
    public ThirstEffect(MobEffectCategory effectType, int potionColor) {
        super(effectType, potionColor);
        addAttributeModifier(ModAttributes.BLOOD_EXHAUSTION.get(), "f6d9889e-dfdc-11e5-b86d-9a79f06e9478", 0.5F, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
        if (entity instanceof Player && entity.isAlive()) {
            VampirePlayer.getOpt((Player) entity).ifPresent(v -> v.addExhaustion(0.005F * (amplifier + 1)));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
