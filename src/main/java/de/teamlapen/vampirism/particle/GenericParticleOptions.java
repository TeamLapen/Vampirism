package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record GenericParticleOptions(ResourceLocation texture, int maxAge, int color, float speed) implements ParticleOptions {

    public static final Codec<GenericParticleOptions> CODEC = RecordCodecBuilder.create((p_239803_0_) -> p_239803_0_
            .group(
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(GenericParticleOptions::texture),
                    Codec.INT.fieldOf("maxAge").forGetter(GenericParticleOptions::maxAge),
                    Codec.INT.fieldOf("color").forGetter(GenericParticleOptions::color),
                    Codec.FLOAT.fieldOf("speed").forGetter(GenericParticleOptions::speed))
            .apply(p_239803_0_, GenericParticleOptions::new));


    @Deprecated
    public static final ParticleOptions.Deserializer<GenericParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @NotNull
        public GenericParticleOptions fromCommand(@NotNull ParticleType<GenericParticleOptions> particleTypeIn, @NotNull StringReader reader) throws CommandSyntaxException {
            return new GenericParticleOptions(ResourceLocation.read(reader), reader.readInt(), reader.readInt());
        }

        @NotNull
        public GenericParticleOptions fromNetwork(@NotNull ParticleType<GenericParticleOptions> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return buffer.readJsonWithCodec(CODEC);
        }
    };

    public GenericParticleOptions(ResourceLocation texture, int maxAge, int color) {
        this(texture, maxAge, color, 1.0F);
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buffer) {
        buffer.writeJsonWithCodec(CODEC, this);
    }

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return ModParticles.GENERIC.get();
    }

    @NotNull
    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()) + " " + texture + " " + maxAge + " " + color;
    }
}
