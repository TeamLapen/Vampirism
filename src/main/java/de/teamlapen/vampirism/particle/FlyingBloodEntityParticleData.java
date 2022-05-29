package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class FlyingBloodEntityParticleData implements IParticleData {

    /**
     * CODEC appears to be an alternative to De/Serializer. Not sure why both exist
     */
    public static final Codec<FlyingBloodEntityParticleData> CODEC = RecordCodecBuilder.create((p_239803_0_) -> p_239803_0_
            .group(
                    Codec.INT.fieldOf("e").forGetter((p_239807_0_) -> p_239807_0_.entity),
                    Codec.BOOL.fieldOf("d").forGetter((p_239806_0_) -> p_239806_0_.direct))
            .apply(p_239803_0_, (e, d) -> new FlyingBloodEntityParticleData(ModParticles.FLYING_BLOOD_ENTITY.get(), e, d)));

    public static final IParticleData.IDeserializer<FlyingBloodEntityParticleData> DESERIALIZER = new IParticleData.IDeserializer<FlyingBloodEntityParticleData>() {
        public FlyingBloodEntityParticleData fromCommand(ParticleType<FlyingBloodEntityParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return new FlyingBloodEntityParticleData(particleTypeIn, reader.readInt(), reader.readBoolean());
        }

        public FlyingBloodEntityParticleData fromNetwork(ParticleType<FlyingBloodEntityParticleData> particleTypeIn, PacketBuffer buffer) {
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
    public void writeToNetwork(PacketBuffer buffer) {
        buffer.writeVarInt(entity);
        buffer.writeBoolean(direct);
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public String writeToString() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + entity + "" + direct;
    }
}
