package de.teamlapen.vampirism.effects;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class GarlicEffect extends MobEffect {

    private static final String DAMAGE_UUID = "cd20a795-04b7-4093-ac61-0eaaa63f65ee";
    private static final String SPEED = "b769af2a-ecb0-4441-9a9f-c9d92f9050b3";
    private final Object2IntMap<UUID> minAttributeLevel;

    public GarlicEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_UUID, -0.2, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED, -0.15, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.minAttributeLevel = new Object2IntArrayMap<>() {{
            put(UUID.fromString(SPEED), 1);
        }};
    }

    @Override
    public double getAttributeModifierValue(int pAmplifier, AttributeModifier pModifier) {
        if (minAttributeLevel.containsKey(pModifier.getId())) {
            int anInt = minAttributeLevel.getInt(pModifier.getId());
            if (pAmplifier >= anInt) {
                return super.getAttributeModifierValue(pAmplifier - anInt, pModifier);
            }
        }
        return super.getAttributeModifierValue(pAmplifier, pModifier);
    }
}
