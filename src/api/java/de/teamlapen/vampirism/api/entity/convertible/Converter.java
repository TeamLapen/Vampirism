package de.teamlapen.vampirism.api.entity.convertible;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface Converter {

    Codec<Converter> CODEC = ExtraCodecs.lazyInitializedCodec(() -> VampirismRegistries.ENTITY_CONVERTER.get().byNameCodec()).dispatch(Converter::codec, Function.identity());

    @Deprecated
    IConvertingHandler<?> createHandler();

    default IConvertingHandler<?> createHandler(@Nullable ResourceLocation texture){
        return createHandler();
    }

    Codec<? extends Converter> codec();
}
