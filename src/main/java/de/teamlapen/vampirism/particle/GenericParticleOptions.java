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

    /**
     * CODEC appears to be an alternative to De/Serializer. Not sure why both exist
     */
    public static final Codec<GenericParticleOptions> CODEC = RecordCodecBuilder.create((p_239803_0_) -> p_239803_0_
            .group(
                    Codec.STRING.fieldOf("t").forGetter((p_239807_0_) -> p_239807_0_.texture.toString()),
                    Codec.INT.fieldOf("a").forGetter((p_239806_0_) -> p_239806_0_.maxAge),
                    Codec.INT.fieldOf("c").forGetter((p_239805_0_) -> p_239805_0_.color),
                    Codec.FLOAT.fieldOf("s").forGetter((p_239804_0_) -> p_239804_0_.speed))
            .apply(p_239803_0_, (t, a, c, s) -> new GenericParticleOptions(new ResourceLocation(t), a, c, s)));


    public static final ParticleOptions.Deserializer<GenericParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @NotNull
        public GenericParticleOptions fromCommand(@NotNull ParticleType<GenericParticleOptions> particleTypeIn, @NotNull StringReader reader) throws CommandSyntaxException {
            return new GenericParticleOptions(ResourceLocation.read(reader), reader.readInt(), reader.readInt());
        }

        @NotNull
        public GenericParticleOptions fromNetwork(@NotNull ParticleType<GenericParticleOptions> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return new GenericParticleOptions(buffer.readResourceLocation(), buffer.readVarInt(), buffer.readVarInt(), buffer.readFloat());
        }
    };

    public GenericParticleOptions(ResourceLocation texture, int maxAge, int color) {
        this(texture, maxAge, color, 1.0F);
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf packetBuffer) {
        packetBuffer.writeResourceLocation(texture);
        packetBuffer.writeVarInt(maxAge);
        packetBuffer.writeVarInt(color);
        packetBuffer.writeFloat(speed);
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
