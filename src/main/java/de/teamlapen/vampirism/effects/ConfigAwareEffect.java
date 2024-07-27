package de.teamlapen.vampirism.effects;

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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ConfigAwareEffect extends MobEffect {

    private final Map<Holder<Attribute>, AttributeTemplate> attributeModifiers = new Object2ObjectOpenHashMap<>();


    protected ConfigAwareEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    protected ConfigAwareEffect(MobEffectCategory pCategory, int pColor, ParticleOptions pParticle) {
        super(pCategory, pColor, pParticle);
    }

    public MobEffect addAttributeModifier(Holder<Attribute> pAttribute, ResourceLocation pId, Supplier<Double> pAmount, AttributeModifier.Operation pOperation) {
        this.attributeModifiers.put(pAttribute, new AttributeTemplate(pId, pAmount, pOperation));
        return this;
    }

    public void createModifiers(int pAmplifier, @NotNull BiConsumer<Holder<Attribute>, AttributeModifier> pOutput) {
        this.attributeModifiers
                .forEach((attribute, template) -> pOutput.accept(attribute, template.create(this.getDescriptionId(), pAmplifier)));
    }

    public void removeAttributeModifiers(@NotNull AttributeMap pAttributeMap) {
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeinstance = pAttributeMap.getInstance(entry.getKey());
            if (attributeinstance != null) {
                attributeinstance.removeModifier(entry.getValue().id());
            }
        }
    }

    public void addAttributeModifiers(@NotNull AttributeMap pAttributeMap, int pAmplifier) {
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeinstance = pAttributeMap.getInstance(entry.getKey());
            if (attributeinstance != null) {
                attributeinstance.removeModifier(entry.getValue().id());
                attributeinstance.addPermanentModifier(entry.getValue().create(this.getDescriptionId(), pAmplifier));
            }
        }
    }

    record AttributeTemplate(ResourceLocation id, Supplier<Double> amount, AttributeModifier.Operation operation) {
        public AttributeModifier create(String pDescription, int pAmplifier) {
            return new AttributeModifier(this.id, this.amount.get() * (double) (pAmplifier + 1), this.operation);
        }
    }
}
