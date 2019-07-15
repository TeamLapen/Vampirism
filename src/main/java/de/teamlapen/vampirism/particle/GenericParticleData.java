package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class GenericParticleData implements IParticleData {
    public static final IParticleData.IDeserializer<GenericParticleData> DESERIALIZER = new IParticleData.IDeserializer<GenericParticleData>() {
        public GenericParticleData deserialize(ParticleType<GenericParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return null;//TODO 1.14 fill
        }

        public GenericParticleData read(ParticleType<GenericParticleData> particleTypeIn, PacketBuffer buffer) {
            return new GenericParticleData(particleTypeIn, buffer.readVarInt(), buffer.readVarInt(), buffer.readVarInt(), buffer.readFloat());//TODO 1.14 test
        }
    };

    private ParticleType<GenericParticleData> particleType;
    private final int texturePos;
    private final int color;
    private final int maxAge;
    private final float speed;

    public GenericParticleData(ParticleType<GenericParticleData> particleType, int texturePos, int maxAge, int color) {
        this(particleType, texturePos, maxAge, color, 1.0F);
    }

    public GenericParticleData(ParticleType<GenericParticleData> particleType, int texturePos, int maxAge, int color, float speed) {
        this.particleType = particleType;
        this.texturePos = texturePos;
        this.maxAge = maxAge;
        this.color = color;
        this.speed = speed;
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeVarInt(texturePos);
        packetBuffer.writeVarInt(maxAge);
        packetBuffer.writeVarInt(color);
        packetBuffer.writeFloat(speed);
    }

    @Override
    public String getParameters() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + texturePos + " " + maxAge + " " + color;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }

    @OnlyIn(Dist.CLIENT)
    public int getTexturePos() {
        return texturePos;
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxAge() {
        return maxAge;
    }

    @OnlyIn(Dist.CLIENT)
    public float getSpeed() {
        return speed;
    }
}
