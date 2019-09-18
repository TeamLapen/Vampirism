package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class FlyingBloodEntityParticleData implements IParticleData {
    public static final IParticleData.IDeserializer<FlyingBloodEntityParticleData> DESERIALIZER = new IParticleData.IDeserializer<FlyingBloodEntityParticleData>() {
        public FlyingBloodEntityParticleData deserialize(ParticleType<FlyingBloodEntityParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return new FlyingBloodEntityParticleData(particleTypeIn, reader.readInt(), reader.readBoolean());
        }

        public FlyingBloodEntityParticleData read(ParticleType<FlyingBloodEntityParticleData> particleTypeIn, PacketBuffer buffer) {
            return new FlyingBloodEntityParticleData(particleTypeIn, buffer.readVarInt(), buffer.readBoolean());
        }
    };
    private final int entity;
    private final boolean direct;
    private ParticleType<FlyingBloodEntityParticleData> particleType;

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
    public String getParameters() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + entity + "" + direct;
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(entity);
        buffer.writeBoolean(direct);
    }
}
