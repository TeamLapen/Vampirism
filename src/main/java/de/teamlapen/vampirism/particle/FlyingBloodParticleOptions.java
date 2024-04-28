package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.util.ByteBufferCodecUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public record FlyingBloodParticleOptions(int maxAge, boolean direct, double targetX, double targetY, double targetZ, ResourceLocation texture, float scale) implements ParticleOptions {

    public FlyingBloodParticleOptions(int maxAge, boolean direct, Vector3d target, ResourceLocation texture, float scale) {
        this(maxAge, direct, target.x, target.y, target.z, texture, scale);
    }

    public static final MapCodec<FlyingBloodParticleOptions> CODEC = RecordCodecBuilder.mapCodec((inst) -> inst
            .group(
                    Codec.INT.fieldOf("maxAge").forGetter(FlyingBloodParticleOptions::maxAge),
                    Codec.BOOL.fieldOf("direct").forGetter(FlyingBloodParticleOptions::direct),
                    Codec.DOUBLE.fieldOf("targetX").forGetter(FlyingBloodParticleOptions::targetX),
                    Codec.DOUBLE.fieldOf("targetY").forGetter(FlyingBloodParticleOptions::targetY),
                    Codec.DOUBLE.fieldOf("targetZ").forGetter(FlyingBloodParticleOptions::targetZ),
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(FlyingBloodParticleOptions::texture),
                    Codec.FLOAT.fieldOf("scale").forGetter(FlyingBloodParticleOptions::scale)
            ).apply(inst, FlyingBloodParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlyingBloodParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FlyingBloodParticleOptions::maxAge,
            ByteBufCodecs.BOOL, FlyingBloodParticleOptions::direct,
            ByteBufferCodecUtil.VECTOR3D, p -> new Vector3d(p.targetX, p.targetY, p.targetZ),
            ByteBufCodecs.STRING_UTF8.map(ResourceLocation::new, Object::toString), FlyingBloodParticleOptions::texture,
            ByteBufCodecs.FLOAT, FlyingBloodParticleOptions::scale,
            FlyingBloodParticleOptions::new);


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

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return ModParticles.FLYING_BLOOD.get();
    }

}
