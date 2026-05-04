package de.teamlapen.vampirism.common.world.effects;

import de.teamlapen.vampirism.api.util.VIdentifier;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.BiConsumer;

public class GarlicEffect extends MobEffect {

    private static final Identifier DAMAGE_UUID = VIdentifier.mod("damage");
    private static final Identifier SPEED = VIdentifier.mod("speed");
    private final Object2IntMap<Identifier> minAttributeLevel;

    public GarlicEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_UUID, -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED, -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        this.minAttributeLevel = new Object2IntArrayMap<>() {{
            put(SPEED, 1);
        }};
    }

    @Override
    public void createModifiers(int pAmplifier, BiConsumer<Holder<Attribute>, AttributeModifier> pOutput) {
        super.createModifiers(pAmplifier, (holder, modifier) -> {
            var minLevel = this.minAttributeLevel.getOrDefault(holder.getKey(), 0);
            if (pAmplifier >= minLevel) {
                pOutput.accept(holder, modifier);
            }
        });
    }
}