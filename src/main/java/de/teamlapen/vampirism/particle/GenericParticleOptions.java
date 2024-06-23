package de.teamlapen.vampirism.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GenericParticleOptions(ResourceLocation texture, int maxAge, int color, float speed) implements ParticleOptions {

    public static final MapCodec<GenericParticleOptions> CODEC = RecordCodecBuilder.mapCodec((p_239803_0_) -> p_239803_0_
            .group(
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(GenericParticleOptions::texture),
                    Codec.INT.fieldOf("maxAge").forGetter(GenericParticleOptions::maxAge),
                    Codec.INT.fieldOf("color").forGetter(GenericParticleOptions::color),
                    Codec.FLOAT.fieldOf("speed").forGetter(GenericParticleOptions::speed))
            .apply(p_239803_0_, GenericParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, GenericParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, GenericParticleOptions::texture,
            ByteBufCodecs.VAR_INT, GenericParticleOptions::maxAge,
            ByteBufCodecs.VAR_INT, GenericParticleOptions::color,
            ByteBufCodecs.FLOAT, GenericParticleOptions::speed,
            GenericParticleOptions::new);


    public GenericParticleOptions(ResourceLocation texture, int maxAge, int color) {
        this(texture, maxAge, color, 1.0F);
    }


    @NotNull
    @Override
    public ParticleType<?> getType() {
        return ModParticles.GENERIC.get();
    }

}
