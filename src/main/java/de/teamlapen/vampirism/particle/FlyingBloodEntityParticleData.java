package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import org.jetbrains.annotations.NotNull;

public class FlyingBloodEntityParticleData implements ParticleOptions {

    /**
     * CODEC appears to be an alternative to De/Serializer. Not sure why both exist
     */
    public static final Codec<FlyingBloodEntityParticleData> CODEC = RecordCodecBuilder.create((p_239803_0_) -> p_239803_0_
            .group(
                    Codec.INT.fieldOf("e").forGetter((p_239807_0_) -> p_239807_0_.entity),
                    Codec.BOOL.fieldOf("d").forGetter((p_239806_0_) -> p_239806_0_.direct))
            .apply(p_239803_0_, (e, d) -> new FlyingBloodEntityParticleData(ModParticles.FLYING_BLOOD_ENTITY.get(), e, d)));

    public static final ParticleOptions.Deserializer<FlyingBloodEntityParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @NotNull
        public FlyingBloodEntityParticleData fromCommand(@NotNull ParticleType<FlyingBloodEntityParticleData> particleTypeIn, @NotNull StringReader reader) throws CommandSyntaxException {
            return new FlyingBloodEntityParticleData(particleTypeIn, reader.readInt(), reader.readBoolean());
        }

        @NotNull
        public FlyingBloodEntityParticleData fromNetwork(@NotNull ParticleType<FlyingBloodEntityParticleData> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return new FlyingBloodEntityParticleData(particleTypeIn, buffer.readVarInt(), buffer.readBoolean());
        }
    };
    private final int entity;
    private final boolean direct;
    private final ParticleType<FlyingBloodEntityParticleData> particleType;

    public FlyingBloodEntityParticleData(ParticleType<FlyingBloodEntityParticleData> particleTypeIn, int entityID, boolean directIn) {
        this.particleType = particleTypeIn;
        this.entity = entityID;
        this.direct = directIn;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getDirect() {
        return this.direct;
    }

    @OnlyIn(Dist.CLIENT)
    public int getEntityID() {
        return this.entity;
    }

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buffer) {
        buffer.writeVarInt(entity);
        buffer.writeBoolean(direct);
    }

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @NotNull
    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + entity + "" + direct;
    }
}
