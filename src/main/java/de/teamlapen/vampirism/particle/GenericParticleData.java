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

public class GenericParticleData implements IParticleData {
    public static final IParticleData.IDeserializer<GenericParticleData> DESERIALIZER = new IParticleData.IDeserializer<GenericParticleData>() {
        public GenericParticleData deserialize(ParticleType<GenericParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
            return new GenericParticleData(particleTypeIn, ResourceLocation.read(reader), reader.readInt(), reader.readInt());
        }

        public GenericParticleData read(ParticleType<GenericParticleData> particleTypeIn, PacketBuffer buffer) {
            return new GenericParticleData(particleTypeIn, buffer.readResourceLocation(), buffer.readVarInt(), buffer.readVarInt(), buffer.readFloat());
        }
    };
    private final ResourceLocation texture;
    private final int color;
    private final int maxAge;
    private final float speed;
    private ParticleType<GenericParticleData> particleType;

    public GenericParticleData(ParticleType<GenericParticleData> particleType, ResourceLocation texture, int maxAge, int color) {
        this(particleType, texture, maxAge, color, 1.0F);
    }

    public GenericParticleData(ParticleType<GenericParticleData> particleType, ResourceLocation texture, int maxAge, int color, float speed) {
        this.particleType = particleType;
        this.texture = texture;
        this.maxAge = maxAge;
        this.color = color;
        this.speed = speed;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return color;
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public String getParameters() {
        return ForgeRegistries.PARTICLE_TYPES.getKey(this.getType()) + " " + texture + " " + maxAge + " " + color;
    }

    @OnlyIn(Dist.CLIENT)
    public float getSpeed() {
        return speed;
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getTexturePos() {
        return texture;
    }

    @Override
    public ParticleType<?> getType() {
        return particleType;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeResourceLocation(texture);
        packetBuffer.writeVarInt(maxAge);
        packetBuffer.writeVarInt(color);
        packetBuffer.writeFloat(speed);
    }
}
