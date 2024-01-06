package de.teamlapen.vampirism.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

public record FlyingBloodEntityParticleOptions(int entity, boolean direct) implements ParticleOptions {

    /**
     * CODEC appears to be an alternative to De/Serializer. Not sure why both exist
     */
    public static final Codec<FlyingBloodEntityParticleOptions> CODEC = RecordCodecBuilder.create((p_239803_0_) -> p_239803_0_
            .group(
                    Codec.INT.fieldOf("e").forGetter((p_239807_0_) -> p_239807_0_.entity),
                    Codec.BOOL.fieldOf("d").forGetter((p_239806_0_) -> p_239806_0_.direct))
            .apply(p_239803_0_, FlyingBloodEntityParticleOptions::new));

    public static final ParticleOptions.Deserializer<FlyingBloodEntityParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @NotNull
        public FlyingBloodEntityParticleOptions fromCommand(@NotNull ParticleType<FlyingBloodEntityParticleOptions> particleTypeIn, @NotNull StringReader reader) throws CommandSyntaxException {
            return new FlyingBloodEntityParticleOptions(reader.readInt(), reader.readBoolean());
        }

        @NotNull
        public FlyingBloodEntityParticleOptions fromNetwork(@NotNull ParticleType<FlyingBloodEntityParticleOptions> particleTypeIn, @NotNull FriendlyByteBuf buffer) {
            return new FlyingBloodEntityParticleOptions(buffer.readVarInt(), buffer.readBoolean());
        }
    };

    @Override
    public void writeToNetwork(@NotNull FriendlyByteBuf buffer) {
        buffer.writeVarInt(entity);
        buffer.writeBoolean(direct);
    }

    @NotNull
    @Override
    public ParticleType<?> getType() {
        return ModParticles.FLYING_BLOOD_ENTITY.get();
    }

    @NotNull
    @Override
    public String writeToString() {
        return BuiltInRegistries.PARTICLE_TYPE.getKey(this.getType()) + " " + entity + "" + direct;
    }
}
