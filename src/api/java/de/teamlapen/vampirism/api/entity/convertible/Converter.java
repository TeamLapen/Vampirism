package de.teamlapen.vampirism.api.entity.convertible;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Converter for converting entities into vampiric creatures
 */
public interface Converter {

    Codec<Converter> CODEC = ExtraCodecs.lazyInitializedCodec(() -> VampirismRegistries.ENTITY_CONVERTER.get().byNameCodec()).dispatch(Converter::codec, Function.identity());

    /**
     * Creates the actual converting handler that does the converting
     *
     * @param texture an optional texture overlay for the converted entity
     * @return the converting handler
     */
    IConvertingHandler<?> createHandler(@Nullable ResourceLocation texture);

    /**
     * @return Codec for this converter
     */
    Codec<? extends Converter> codec();
}
