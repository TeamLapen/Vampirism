package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.particle.FlyingBloodEntityParticle;
import de.teamlapen.vampirism.client.particle.FlyingBloodParticle;
import de.teamlapen.vampirism.client.particle.GenericParticle;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ModParticleFactories {

    static void registerFactories(@NotNull RegisterParticleProvidersEvent event) {
        event.register(ModParticles.FLYING_BLOOD.get(), new FlyingBloodParticle.Factory());
        event.register(ModParticles.FLYING_BLOOD_ENTITY.get(), new FlyingBloodEntityParticle.Factory());
        event.register(ModParticles.GENERIC.get(), new GenericParticle.Factory());
    }
}
