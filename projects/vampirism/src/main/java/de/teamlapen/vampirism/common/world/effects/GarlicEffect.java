package de.teamlapen.vampirism.common.world.effects;

import de.teamlapen.vampirism.api.util.VIdentifier;
import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
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
import java.util.function.IntToDoubleFunction;

public class GarlicEffect extends MobEffect {

    private static final Identifier DAMAGE_UUID = VIdentifier.mod("damage");
    private static final Identifier SPEED = VIdentifier.mod("speed");

    public GarlicEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFFFFF);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, DAMAGE_UUID, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, key -> key * -0.1);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, key -> Math.max(key-1, 0) * -0.15);
    }

}