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

public record FlyingBloodParticleOptions(int maxAge, boolean direct, double targetX, double targetY, double targetZ, ResourceLocation texture, float scale) implements ParticleOptions {

    public static final Codec<FlyingBloodParticleOptions> CODEC = RecordCodecBuilder.create((inst) -> inst
            .group(
                    Codec.INT.fieldOf("maxAge").forGetter(FlyingBloodParticleOptions::maxAge),
                    Codec.BOOL.fieldOf("direct").forGetter(FlyingBloodParticleOptions::direct),
                    Codec.DOUBLE.fieldOf("targetX").forGetter(FlyingBloodParticleOptions::targetX),
                    Codec.DOUBLE.fieldOf("targetY").forGetter(FlyingBloodParticleOptions::targetY),
                    Codec.DOUBLE.fieldOf("targetZ").forGetter(FlyingBloodParticleOptions::targetZ),
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(FlyingBloodParticleOptions::texture),
                    Codec.FLOAT.fieldOf("scale").forGetter(FlyingBloodParticleOptions::scale)
            ).apply(inst, FlyingBloodParticleOptions::new));


    @Deprecated
    public static final ParticleOptions.Deserializer<FlyingBloodParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @NotNull
        public FlyingBloodParticleOptions fromCommand(@NotNull ParticleType<FlyingBloodParticleOptions> particleTypeIn, @NotNull StringReader reader) throws CommandSyntaxException {
            return new FlyingBloodParticleOptions(reader.readInt(), reader.readBoolean(), reader.readDouble(), reader.readDouble(), reader.readDouble(), ResourceLocation.read(reader), reader.readFloat());
        }

        @NotNull
        public FlyingBloodParticleOptions fromNetwork(@NotNull ParticleType<FlyingBloodParticleOptions> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return buffer.readJsonWithCodec(CODEC);
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
        buffer.writeJsonWithCodec(CODEC, this);
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
