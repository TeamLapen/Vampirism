package de.teamlapen.vampirism.common.world.entity.converted.converter;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.entity.convertible.Converter;
import de.teamlapen.vampirism.api.world.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.datamaps.ConverterEntry;
import de.teamlapen.vampirism.common.world.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.common.world.entity.converted.VampirismEntityRegistry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DefaultConverter implements Converter {

    public static final MapCodec<DefaultConverter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ConverterEntry.ConvertingAttributeModifier.CODEC.optionalFieldOf("attribute_helper", ConverterEntry.ConvertingAttributeModifier.DEFAULT).forGetter(i -> i.helper)
    ).apply(instance, DefaultConverter::new));

    protected final ConverterEntry.ConvertingAttributeModifier helper;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public DefaultConverter(Optional<ConverterEntry.ConvertingAttributeModifier> helper) {
        this.helper = helper.orElse(ConverterEntry.ConvertingAttributeModifier.DEFAULT);
    }

    public DefaultConverter() {
        this.helper = ConverterEntry.ConvertingAttributeModifier.DEFAULT;
    }

    private DefaultConverter(ConverterEntry.ConvertingAttributeModifier entry) {
        this.helper = entry;
    }

    @Override
    public IConvertingHandler<?> createHandler(@Nullable Identifier texture) {
        return new DefaultConvertingHandler<>(new VampirismEntityRegistry.DefaultHelper(this.helper), texture);
    }

    @Override
    public MapCodec<? extends Converter> codec() {
        return ModEntities.DEFAULT_CONVERTER.get();
    }
}
