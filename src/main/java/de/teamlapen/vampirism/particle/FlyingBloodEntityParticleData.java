package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class FlyingBloodEntityParticleData implements IParticleData {
    public static final IParticleData.IDeserializer<FlyingBloodEntityParticleData> DESERIALIZER = new IParticleData.IDeserializer<FlyingBloodEntityParticleData>() {
        public FlyingBloodEntityParticleData deserialize(ParticleType<FlyingBloodEntityParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return null;//TODO 1.14 fill
        }

        public FlyingBloodEntityParticleData read(ParticleType<FlyingBloodEntityParticleData> particleTypeIn, PacketBuffer buffer) {
            return new FlyingBloodEntityParticleData(particleTypeIn, Minecraft.getInstance().world.getEntityByID(buffer.readVarInt()), buffer.readBoolean());//TODO 1.14 test
        }
    };

    private ParticleType<FlyingBloodEntityParticleData> particleType;
    private final Entity entity;
    private final boolean direct;

    public FlyingBloodEntityParticleData(ParticleType<FlyingBloodEntityParticleData> particleTypeIn, Entity entityIn, boolean directIn) {
        this.particleType = particleTypeIn;
        this.entity = entityIn;
        this.direct = directIn;
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(entity.getEntityId());
        buffer.writeBoolean(direct);
    }

    @Override
    public String getParameters() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + entity.toString() + " " + direct;
    }

    @OnlyIn(Dist.CLIENT)
    public Entity getEntity() {
        return this.entity;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean getDirect() {
        return this.direct;
    }
}
