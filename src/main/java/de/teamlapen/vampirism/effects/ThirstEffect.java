package de.teamlapen.vampirism.effects;

import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;


public class ThirstEffect extends VampirismEffect {
    public ThirstEffect(String name, EffectType effectType, int potionColor) {
        super(name, effectType, potionColor);
        addAttributesModifier(ModAttributes.blood_exhaustion, "f6d9889e-dfdc-11e5-b86d-9a79f06e9478", 0.5F, AttributeModifier.Operation.ADDITION);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity && entity.isAlive()) {
            VampirePlayer.getOpt((PlayerEntity) entity).ifPresent(v -> v.addExhaustion(0.005F * (amplifier + 1)));
        }
    }
}
