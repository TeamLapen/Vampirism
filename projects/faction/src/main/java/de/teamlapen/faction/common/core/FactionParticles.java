package de.teamlapen.faction.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.util.REFERENCE;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class FactionParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Registries.PARTICLE_TYPE, REFERENCE.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ColorParticleOption>> FACTION_SURROUNDING = PARTICLE_TYPES.register("faction_surrounding", () -> create(false, ColorParticleOption::codec, ColorParticleOption::streamCodec));

    static void register(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }

    private static <T extends ParticleOptions> ParticleType<T> create(@SuppressWarnings("SameParameterValue") boolean overrideLimiter, final Function<ParticleType<T>, MapCodec<T>> codecGetter, final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
        return new ParticleType<T>(overrideLimiter) {
            @Override
            public MapCodec<T> codec() {
                return codecGetter.apply(this);
            }

            @Override
            public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodecGetter.apply(this);
            }
        };
    }


}
