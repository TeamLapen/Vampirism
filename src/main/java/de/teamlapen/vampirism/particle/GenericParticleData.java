package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class GenericParticleData implements ParticleOptions {

    /**
     * CODEC appears to be an alternative to De/Serializer. Not sure why both exist
     */
    public static final Codec<GenericParticleData> CODEC = RecordCodecBuilder.create((p_239803_0_) -> p_239803_0_
            .group(
                    Codec.STRING.fieldOf("t").forGetter((p_239807_0_) -> p_239807_0_.texture.toString()),
                    Codec.INT.fieldOf("a").forGetter((p_239806_0_) -> p_239806_0_.maxAge),
                    Codec.INT.fieldOf("c").forGetter((p_239805_0_) -> p_239805_0_.color),
                    Codec.FLOAT.fieldOf("s").forGetter((p_239804_0_) -> p_239804_0_.speed))
            .apply(p_239803_0_, (t, a, c, s) -> new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation(t), a, c, s)));


    public static final ParticleOptions.Deserializer<GenericParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Nonnull
        public GenericParticleData fromCommand(@Nonnull ParticleType<GenericParticleData> particleTypeIn, @Nonnull StringReader reader) throws CommandSyntaxException {
            return new GenericParticleData(particleTypeIn, ResourceLocation.read(reader), reader.readInt(), reader.readInt());
        }

        @Nonnull
        public GenericParticleData fromNetwork(@Nonnull ParticleType<GenericParticleData> particleTypeIn, FriendlyByteBuf buffer) {
            return new GenericParticleData(particleTypeIn, buffer.readResourceLocation(), buffer.readVarInt(), buffer.readVarInt(), buffer.readFloat());
        }
    };
    private final ResourceLocation texture;
    private final int color;
    private final int maxAge;
    private final float speed;
    private final ParticleType<GenericParticleData> particleType;

    public GenericParticleData(ParticleType<GenericParticleData> particleType, ResourceLocation texture, int maxAge, int color) {
        this(particleType, texture, maxAge, color, 1.0F);
    }

    public GenericParticleData(ParticleType<GenericParticleData> particleType, ResourceLocation texture, int maxAge, int color, float speed) {
        this.particleType = particleType;
        this.texture = texture;
        this.maxAge = maxAge;
        this.color = color;
        this.speed = speed;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeResourceLocation(texture);
        packetBuffer.writeVarInt(maxAge);
        packetBuffer.writeVarInt(color);
        packetBuffer.writeFloat(speed);
    }

    @OnlyIn(Dist.CLIENT)
    public float getSpeed() {
        return speed;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexturePos() {
        return texture;
    }

    @Nonnull
    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Nonnull
    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + texture + " " + maxAge + " " + color;
    }
}
