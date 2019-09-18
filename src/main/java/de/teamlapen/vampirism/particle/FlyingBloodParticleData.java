package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class FlyingBloodParticleData implements IParticleData {
    public static final IParticleData.IDeserializer<FlyingBloodParticleData> DESERIALIZER = new IParticleData.IDeserializer<FlyingBloodParticleData>() {
        public FlyingBloodParticleData deserialize(ParticleType<FlyingBloodParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return new FlyingBloodParticleData(particleTypeIn, reader.readInt(), reader.readBoolean(), reader.readDouble(), reader.readDouble(), reader.readDouble(), ResourceLocation.read(reader));
        }

        public FlyingBloodParticleData read(ParticleType<FlyingBloodParticleData> particleTypeIn, PacketBuffer buffer) {
            return new FlyingBloodParticleData(particleTypeIn, buffer.readVarInt(), buffer.readBoolean(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readResourceLocation());
        }
    };
    private final int maxAge;
    private final ResourceLocation texture;
    private final boolean direct;
    private final double targetX;
    private final double targetY;
    private final double targetZ;
    private ParticleType<FlyingBloodParticleData> particleType;

    public FlyingBloodParticleData(ParticleType<FlyingBloodParticleData> particleTypeIn, int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ, ResourceLocation textureIn) {
        this.particleType = particleTypeIn;
        this.maxAge = maxAgeIn;
        this.texture = textureIn;
        this.direct = direct;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public FlyingBloodParticleData(ParticleType<FlyingBloodParticleData> particleTypeIn, int maxAgeIn, boolean direct, double targetX, double targetY, double targetZ) {
        this(particleTypeIn, maxAgeIn, direct, targetX, targetY, targetZ, new ResourceLocation("minecraft", "critical_hit"));
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public String getParameters() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + maxAge + " " + texture;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getTargetZ() {
        return targetZ;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexturePos() {
        return texture;
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isDirect() {
        return direct;
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeVarInt(maxAge);
        buffer.writeBoolean(direct);
        buffer.writeDouble(targetX);
        buffer.writeDouble(targetY);
        buffer.writeDouble(targetZ);
        buffer.writeResourceLocation(texture);
    }
}
