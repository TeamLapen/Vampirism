package de.teamlapen.vampirism.entity.converted.converter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.data.reloadlistener.ConvertiblesReloadListener;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;

public class DefaultConverter implements Converter {

    public static final Codec<DefaultConverter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ConvertiblesReloadListener.EntityEntry.Attributes.CODEC.optionalFieldOf("attribute_helper", ConvertiblesReloadListener.EntityEntry.Attributes.DEFAULT).forGetter(i -> i.helper)
    ).apply(instance, DefaultConverter::new));

    protected final ConvertiblesReloadListener.EntityEntry.Attributes helper;

    public DefaultConverter(ConvertiblesReloadListener.EntityEntry.Attributes helper) {
        this.helper = helper;
    }

    public DefaultConverter() {
        this.helper = null;
    }

    @Override
    public IConvertingHandler<?> createHandler() {
        return new DefaultConvertingHandler<>(new VampirismEntityRegistry.DatapackHelper(this.helper));
    }

    @Override
    public Codec<? extends Converter> codec() {
        return ModEntities.DEFAULT_CONVERTER.get();
    }
}
