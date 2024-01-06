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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public record FlyingBloodParticleOptions(int maxAge, boolean direct, double targetX, double targetY, double targetZ, ResourceLocation texture, float scale) implements ParticleOptions {

    /**
     * CODEC appears to be an alternative to De/Serializer. Not sure why both exist
     */
    public static final Codec<FlyingBloodParticleOptions> CODEC = RecordCodecBuilder.create((p_239803_0_) -> p_239803_0_
            .group(
                    Codec.INT.fieldOf("a").forGetter((p_239807_0_) -> p_239807_0_.maxAge),
                    Codec.BOOL.fieldOf("d").forGetter((p_239806_0_) -> p_239806_0_.direct),
                    Codec.DOUBLE.fieldOf("x").forGetter((p_239805_0_) -> p_239805_0_.targetX),
                    Codec.DOUBLE.fieldOf("y").forGetter((p_239804_0_) -> p_239804_0_.targetY),
                    Codec.DOUBLE.fieldOf("z").forGetter((p_239804_0_) -> p_239804_0_.targetZ),
                    Codec.STRING.fieldOf("t").forGetter((p_239804_0_) -> p_239804_0_.texture.toString()),
                    Codec.FLOAT.fieldOf("s").forGetter((p_239804_0_) -> p_239804_0_.scale)
            ).apply(p_239803_0_, (a, d, x, y, z, t, s) -> new FlyingBloodParticleOptions(a, d, x, y, z, new ResourceLocation(t), s)));

    public static final ParticleOptions.Deserializer<FlyingBloodParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @NotNull
        public FlyingBloodParticleOptions fromCommand(@NotNull ParticleType<FlyingBloodParticleOptions> particleTypeIn, @NotNull StringReader reader) throws CommandSyntaxException {
            return new FlyingBloodParticleOptions(reader.readInt(), reader.readBoolean(), reader.readDouble(), reader.readDouble(), reader.readDouble(), ResourceLocation.read(reader), reader.readFloat());
        }

        @NotNull
        public FlyingBloodParticleOptions fromNetwork(@NotNull ParticleType<FlyingBloodParticleOptions> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return new FlyingBloodParticleOptions(buffer.readVarInt(), buffer.readBoolean(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readResourceLocation(), buffer.readFloat());
        }
    };

    public FlyingBloodParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ) {
        this(maxAgeIn, direct, targetX, targetY, targetZ, 1f);
    }

    public FlyingBloodParticleOptions(int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ, float scale) {
        this(maxAgeIn, direct, targetX, targetY, targetZ, new ResourceLocation("minecraft", "critical_hit"), scale);
    }

    public FlyingBloodParticleOptions(int maxAge, boolean direct, double targetX, double targetY, double targetZ, ResourceLocation texture) {
        this(maxAge, direct, targetX, targetY, targetZ, texture, 1f);
    }

    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buffer) {
        buffer.writeVarInt(maxAge);
        buffer.writeBoolean(direct);
        buffer.writeDouble(targetX);
        buffer.writeDouble(targetY);
        buffer.writeDouble(targetZ);
        buffer.writeResourceLocation(texture);
        buffer.writeFloat(scale);
    }

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return ModParticles.FLYING_BLOOD.get();
    }

    @NotNull
    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()) + " " + maxAge + " " + texture;
    }
}
