package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class FlyingBloodParticleData implements IParticleData {
    public static final IParticleData.IDeserializer<FlyingBloodParticleData> DESERIALIZER = new IParticleData.IDeserializer<FlyingBloodParticleData>() {
        public FlyingBloodParticleData deserialize(ParticleType<FlyingBloodParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return null;//TODO 1.14 fill
        }

        public FlyingBloodParticleData read(ParticleType<FlyingBloodParticleData> particleTypeIn, PacketBuffer buffer) {
            return new FlyingBloodParticleData(particleTypeIn, buffer.readVarInt(), buffer.readVarInt());//TODO 1.14 test
        }
    };

    private ParticleType<FlyingBloodParticleData> particleType;
    private final int maxAge;
    private final int texturePos;

    public FlyingBloodParticleData(ParticleType<FlyingBloodParticleData> particleTypeIn, int maxAgeIn, int texturePosIn) {
        this.particleType = particleTypeIn;
        this.maxAge = maxAgeIn;
        this.texturePos = texturePosIn;
    }

    public FlyingBloodParticleData(ParticleType<FlyingBloodParticleData> particleTypeIn, int maxAgeIn) {
        this(particleTypeIn, maxAgeIn, 65);
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(maxAge);
        buffer.writeVarInt(texturePos);
    }

    @Override
    public String getParameters() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + maxAge + " " + texturePos;
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxAge() {
        return maxAge;
    }

    @OnlyIn(Dist.CLIENT)
    public int getTexturePos() {
        return texturePos;
    }
}
