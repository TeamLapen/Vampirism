package de.teamlapen.vampirism.entity.converted.converter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.datamaps.ConverterEntry;
import de.teamlapen.vampirism.entity.converted.SpecialConvertingHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class SpecialConverter<T extends PathfinderMob, Z extends PathfinderMob & ICurableConvertedCreature<T>> implements Converter {

    public static final Codec<SpecialConverter<?, ?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("converted_type").forGetter(i -> i.convertedType),
            ExtraCodecs.strictOptionalField(ConverterEntry.ConvertingAttributeModifier.CODEC, "attribute_helper", ConverterEntry.ConvertingAttributeModifier.DEFAULT).forGetter(i -> i.helper)
    ).apply(instance, SpecialConverter::new));

    private final EntityType<Z> convertedType;
    private final ConverterEntry.ConvertingAttributeModifier helper;

    @SuppressWarnings({"unchecked"})
    private SpecialConverter(EntityType<?> convertedType, ConverterEntry.ConvertingAttributeModifier helper) {
        this.convertedType = (EntityType<Z>) convertedType;
        this.helper = helper;
    }

    public SpecialConverter(Supplier<? extends EntityType<Z>> convertedType, ConverterEntry.ConvertingAttributeModifier helper) {
        this.convertedType = convertedType.get();
        this.helper = helper;
    }

    public SpecialConverter(Supplier<? extends EntityType<Z>> convertedType) {
        this.convertedType = convertedType.get();
        this.helper = ConverterEntry.ConvertingAttributeModifier.DEFAULT;
    }

    @Override
    public IConvertingHandler<?> createHandler(@Nullable ResourceLocation texture) {
        return new SpecialConvertingHandler<>(() -> this.convertedType, texture, new VampirismEntityRegistry.DefaultHelper(this.helper));
    }

    @Override
    public Codec<? extends Converter> codec() {
        return ModEntities.SPECIAL_CONVERTER.get();
    }
}
