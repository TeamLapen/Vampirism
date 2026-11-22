package de.teamlapen.faction.common.world.refinements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Data driven implementation of {@link IRefinement}
 *
 * @param description the description of the refinement
 * @param modifier    an optional attribute modifier that is applied while the refinement is equipped
 * @param detrimental whether the refinement makes things worse instead of better
 */
public record Refinement(Component description, Optional<Modifier> modifier, boolean detrimental) implements IRefinement {

    public static final Codec<Refinement> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ComponentSerialization.CODEC.fieldOf("description").forGetter(Refinement::description),
            Modifier.CODEC.optionalFieldOf("modifier").forGetter(Refinement::modifier),
            Codec.BOOL.optionalFieldOf("detrimental", false).forGetter(Refinement::detrimental)
    ).apply(inst, Refinement::new));

    public static final Codec<IRefinement> DIRECT_CODEC = CODEC.xmap(r -> r, r -> (Refinement) r);

    /**
     * Creates a refinement without an attribute modifier with a translatable description derived from the key
     */
    public static Refinement of(ResourceKey<IRefinement> key) {
        return new Refinement(Component.translatable(descriptionId(key)), Optional.empty(), false);
    }

    /**
     * Creates a refinement that modifies the given attribute with a translatable description derived from the key
     */
    public static Refinement attribute(ResourceKey<IRefinement> key, Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        return new Refinement(Component.translatable(descriptionId(key)), Optional.of(new Modifier(attribute, amount, operation)), amount < 0);
    }

    public static String descriptionId(ResourceKey<IRefinement> key) {
        return Util.makeDescriptionId("refinement", key.identifier()) + ".desc";
    }

    @Override
    public @Nullable Holder<Attribute> getAttribute() {
        return this.modifier.map(Modifier::attribute).orElse(null);
    }

    @Override
    public @Nullable AttributeModifier createAttributeModifier(Identifier id) {
        return this.modifier.map(m -> new AttributeModifier(id, m.amount(), m.operation())).orElse(null);
    }

    @Override
    public Component getDescription() {
        if (this.detrimental) {
            return this.description.copy().withStyle(ChatFormatting.RED);
        }
        return this.description;
    }

    public record Modifier(Holder<Attribute> attribute, double amount, AttributeModifier.Operation operation) {
        public static final Codec<Modifier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Attribute.CODEC.fieldOf("attribute").forGetter(Modifier::attribute),
                Codec.DOUBLE.fieldOf("amount").forGetter(Modifier::amount),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(Modifier::operation)
        ).apply(inst, Modifier::new));
    }
}
