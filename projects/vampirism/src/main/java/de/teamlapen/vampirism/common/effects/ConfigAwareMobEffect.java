package de.teamlapen.vampirism.common.effects;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ConfigAwareMobEffect extends MobEffect {

    private final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();

    protected ConfigAwareMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    protected ConfigAwareMobEffect(MobEffectCategory category, int color, ParticleOptions particle) {
        super(category, color, particle);
    }

    public void addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, Supplier<Double> amount, AttributeModifier.Operation operation) {
        this.attributeModifiers.put(attribute, new AttributeTemplate(id, amount, operation));
    }

    public void createModifiers(int amplifier, BiConsumer<Holder<Attribute>, AttributeModifier> output) {
        this.attributeModifiers.forEach((attribute, template) -> output.accept(attribute, template.create(amplifier)));
    }

    public void removeAttributeModifiers(AttributeMap attributeMap) {
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeinstance = attributeMap.getInstance(entry.getKey());
            if (attributeinstance != null) {
                attributeinstance.removeModifier(entry.getValue().id());
            }
        }
    }

    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeinstance = attributeMap.getInstance(entry.getKey());
            if (attributeinstance != null) {
                attributeinstance.removeModifier(entry.getValue().id());
                attributeinstance.addPermanentModifier(entry.getValue().create(amplifier));
            }
        }
    }

    public record AttributeTemplate(ResourceLocation id, Supplier<Double> amount, AttributeModifier.Operation operation) {

        public AttributeModifier create(int amplifier) {
            return new AttributeModifier(this.id, this.amount.get() * (double) (amplifier + 1), this.operation);
        }
    }
}
