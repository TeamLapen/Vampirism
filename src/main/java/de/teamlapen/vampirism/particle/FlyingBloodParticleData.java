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

public class FlyingBloodParticleData implements ParticleOptions {

    /**
     * CODEC appears to be an alternative to De/Serializer. Not sure why both exist
     */
    public static final Codec<FlyingBloodParticleData> CODEC = RecordCodecBuilder.create((p_239803_0_) -> p_239803_0_
            .group(
                    Codec.INT.fieldOf("a").forGetter((p_239807_0_) -> p_239807_0_.maxAge),
                    Codec.BOOL.fieldOf("d").forGetter((p_239806_0_) -> p_239806_0_.direct),
                    Codec.DOUBLE.fieldOf("x").forGetter((p_239805_0_) -> p_239805_0_.targetX),
                    Codec.DOUBLE.fieldOf("y").forGetter((p_239804_0_) -> p_239804_0_.targetY),
                    Codec.DOUBLE.fieldOf("z").forGetter((p_239804_0_) -> p_239804_0_.targetZ),
                    Codec.STRING.fieldOf("t").forGetter((p_239804_0_) -> p_239804_0_.texture.toString()))
            .apply(p_239803_0_, (a, d, x, y, z, t) -> new FlyingBloodParticleData(ModParticles.flying_blood.get(), a, d, x, y, z, new ResourceLocation(t))));

    public static final ParticleOptions.Deserializer<FlyingBloodParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Nonnull
        public FlyingBloodParticleData fromCommand(@Nonnull ParticleType<FlyingBloodParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return new FlyingBloodParticleData(particleTypeIn, reader.readInt(), reader.readBoolean(), reader.readDouble(), reader.readDouble(), reader.readDouble(), ResourceLocation.read(reader));
        }

        @Nonnull
        public FlyingBloodParticleData fromNetwork(@Nonnull ParticleType<FlyingBloodParticleData> particleTypeIn, FriendlyByteBuf buffer) {
            return new FlyingBloodParticleData(particleTypeIn, buffer.readVarInt(), buffer.readBoolean(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readResourceLocation());
        }
    };
    private final int maxAge;
    private final ResourceLocation texture;
    private final boolean direct;
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private final ParticleType<FlyingBloodParticleData> particleType;

    public FlyingBloodParticleData(ParticleType<FlyingBloodParticleData> particleTypeIn, int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ, ResourceLocation textureIn) {
        this.particleType = particleTypeIn;
        this.maxAge = maxAgeIn;
        this.texture = textureIn;
        this.direct = direct;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public FlyingBloodParticleData(ParticleType<FlyingBloodParticleData> particleTypeIn, int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ) {
        this(particleTypeIn, maxAgeIn, direct, targetX, targetY, targetZ, new ResourceLocation("minecraft", "critical_hit"));
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeVarInt(maxAge);
        buffer.writeBoolean(direct);
        buffer.writeDouble(targetX);
        buffer.writeDouble(targetY);
        buffer.writeDouble(targetZ);
        buffer.writeResourceLocation(texture);
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getTargetZ() {
        return targetZ;
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

    @OnlyIn(Dist.CLIENT)
    public boolean isDirect() {
        return direct;
    }

    @Nonnull
    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + maxAge + " " + texture;
    }
}
