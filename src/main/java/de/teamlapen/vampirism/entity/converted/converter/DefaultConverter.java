package de.teamlapen.vampirism.entity.converted.converter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.data.reloadlistener.ConvertiblesReloadListener;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DefaultConverter implements Converter {

    public static final Codec<DefaultConverter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier.CODEC.optionalFieldOf("attribute_helper").forGetter(i -> Optional.ofNullable(i.helper))
    ).apply(instance, DefaultConverter::new));

    protected final ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier helper;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public DefaultConverter(Optional<ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier> helper) {
        this.helper = helper.orElse(ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier.DEFAULT);
    }

    public DefaultConverter() {
        this.helper = ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier.DEFAULT;
    }

    @Override
    public IConvertingHandler<?> createHandler() {
        return new DefaultConvertingHandler<>(new VampirismEntityRegistry.DatapackHelper(this.helper), null);
    }

    @Override
    public IConvertingHandler<?> createHandler(@Nullable ResourceLocation texture) {
        return new DefaultConvertingHandler<>(new VampirismEntityRegistry.DatapackHelper(this.helper), texture);
    }

    @Override
    public Codec<? extends Converter> codec() {
        return ModEntities.DEFAULT_CONVERTER.get();
    }
}
