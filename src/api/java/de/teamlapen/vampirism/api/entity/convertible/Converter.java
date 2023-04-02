package de.teamlapen.vampirism.api.entity.convertible;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public interface Converter {

    Codec<Converter> CODEC = ExtraCodecs.lazyInitializedCodec(() -> VampirismRegistries.ENTITY_CONVERTER.get().getCodec()).dispatch(Converter::codec, Function.identity());

    IConvertingHandler<?> createHandler();

    Codec<? extends Converter> codec();
}
