package de.teamlapen.vampirism.datamaps;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.entity.converted.converter.DefaultConverter;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public record ConverterEntry(Converter converter, Optional<ResourceLocation> overlay) implements IConverterEntry {
    public static final Codec<IConverterEntry> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                Converter.CODEC.optionalFieldOf("handler", new DefaultConverter()).forGetter(IConverterEntry::converter),
                ResourceLocation.CODEC.optionalFieldOf("overlay").forGetter(IConverterEntry::overlay)
        ).apply(inst, ConverterEntry::new);
    });

    public ConverterEntry(Converter converter, ResourceLocation overlay) {
        this(converter, Optional.of(overlay));
    }

    public ConverterEntry(@Nullable ResourceLocation overlay) {
        this(new DefaultConverter(), Optional.ofNullable(overlay));
    }

    public ConverterEntry(Converter converter) {
        this(converter, Optional.empty());
    }


    public record ConvertingAttributeModifier(Map<Holder<Attribute>, Pair<FloatProvider, Double>> attributeModifier) {
        public static ConvertingAttributeModifier DEFAULT = new ConvertingAttributeModifier(
                Map.of(
                        Attributes.ATTACK_DAMAGE, com.mojang.datafixers.util.Pair.of(ConstantFloat.of(1.3f), 1d),
                        Attributes.KNOCKBACK_RESISTANCE, com.mojang.datafixers.util.Pair.of(ConstantFloat.of(1.3f), 0d),
                        Attributes.MAX_HEALTH, com.mojang.datafixers.util.Pair.of(ConstantFloat.of(1.5f), 1d),
                        Attributes.MOVEMENT_SPEED, com.mojang.datafixers.util.Pair.of(ConstantFloat.of(1.2f), 1d)
                ));

        public ConvertingAttributeModifier(List<Pair<Holder<Attribute>, Pair<FloatProvider, Double>>> values) {
            this(values.stream().collect(Collectors.toMap(com.mojang.datafixers.util.Pair::getFirst, com.mojang.datafixers.util.Pair::getSecond, (a, b) -> b)));
        }

        private static final Codec<com.mojang.datafixers.util.Pair<Holder<Attribute>, com.mojang.datafixers.util.Pair<FloatProvider, Double>>> CODEC_PAIR = RecordCodecBuilder.create(inst ->
                inst.group(
                        BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(Pair::getFirst),
                        FloatProvider.CODEC.fieldOf("modifier").forGetter(s -> s.getSecond().getFirst()),
                        Codec.DOUBLE.optionalFieldOf("fallback_base", 1d).forGetter(s -> s.getSecond().getSecond())
                ).apply(inst, ((attribute, floatProvider, aDouble) -> Pair.of(attribute, Pair.of(floatProvider, aDouble)))));
        public static final Codec<ConvertingAttributeModifier> CODEC = CODEC_PAIR.listOf().xmap(
                ConvertingAttributeModifier::new,
                x -> x.attributeModifier.entrySet().stream().map(s -> com.mojang.datafixers.util.Pair.of(s.getKey(), com.mojang.datafixers.util.Pair.of(s.getValue().getFirst(), s.getValue().getSecond()))).collect(Collectors.toList())
        );

        public com.mojang.datafixers.util.Pair<FloatProvider, Double> modifier(Attribute attribute) {
            return attributeModifier.get(attribute);
        }

    }
}