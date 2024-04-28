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

public record FlyingBloodEntityParticleOptions(int entity, boolean direct) implements ParticleOptions {

    public static final MapCodec<FlyingBloodEntityParticleOptions> CODEC = RecordCodecBuilder.mapCodec((p_239803_0_) -> p_239803_0_
            .group(
                    Codec.INT.fieldOf("entity").forGetter(FlyingBloodEntityParticleOptions::entity),
                    Codec.BOOL.fieldOf("direct").forGetter(FlyingBloodEntityParticleOptions::direct))
            .apply(p_239803_0_, FlyingBloodEntityParticleOptions::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FlyingBloodEntityParticleOptions> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FlyingBloodEntityParticleOptions::entity,
            ByteBufCodecs.BOOL, FlyingBloodEntityParticleOptions::direct,
            FlyingBloodEntityParticleOptions::new);

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return ModParticles.FLYING_BLOOD_ENTITY.get();
    }

}
