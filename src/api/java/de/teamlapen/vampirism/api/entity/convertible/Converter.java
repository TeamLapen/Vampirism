package de.teamlapen.vampirism.api.entity.convertible;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Converter for converting entities into vampiric creatures
 */
public interface Converter {

    Codec<Converter> CODEC = Codec.lazyInitialized(() -> VampirismRegistries.ENTITY_CONVERTER.get().byNameCodec().dispatch(Converter::codec, Function.identity()));

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
    MapCodec<? extends Converter> codec();
}
