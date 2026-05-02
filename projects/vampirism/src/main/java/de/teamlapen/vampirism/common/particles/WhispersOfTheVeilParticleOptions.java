package de.teamlapen.vampirism.common.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record WhispersOfTheVeilParticleOptions(int lifetime, double targetX, double targetY, double targetZ, float speed) implements ParticleOptions {

    public static final MapCodec<WhispersOfTheVeilParticleOptions> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("lifetime").forGetter(WhispersOfTheVeilParticleOptions::lifetime),
            Codec.DOUBLE.fieldOf("targetX").forGetter(WhispersOfTheVeilParticleOptions::targetX),
            Codec.DOUBLE.fieldOf("targetY").forGetter(WhispersOfTheVeilParticleOptions::targetY),
            Codec.DOUBLE.fieldOf("targetZ").forGetter(WhispersOfTheVeilParticleOptions::targetZ),
            Codec.FLOAT.fieldOf("speed").forGetter(WhispersOfTheVeilParticleOptions::speed)
    ).apply(inst, WhispersOfTheVeilParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WhispersOfTheVeilParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, WhispersOfTheVeilParticleOptions::lifetime,
            ByteBufCodecs.DOUBLE, WhispersOfTheVeilParticleOptions::targetX,
            ByteBufCodecs.DOUBLE, WhispersOfTheVeilParticleOptions::targetY,
            ByteBufCodecs.DOUBLE, WhispersOfTheVeilParticleOptions::targetZ,
            ByteBufCodecs.FLOAT, WhispersOfTheVeilParticleOptions::speed,
            WhispersOfTheVeilParticleOptions::new
    );

    @Override
    public @NotNull ParticleType<?> getType() {
        return ModParticles.WHISPERS_OF_THE_VEIL.get();
    }
}
