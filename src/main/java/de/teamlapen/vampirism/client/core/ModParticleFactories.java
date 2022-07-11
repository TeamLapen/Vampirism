package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.render.particle.FlyingBloodEntityParticle;
import de.teamlapen.vampirism.client.render.particle.FlyingBloodParticle;
import de.teamlapen.vampirism.client.render.particle.GenericParticle;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;

@OnlyIn(Dist.CLIENT)
public class ModParticleFactories {

    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.register(ModParticles.FLYING_BLOOD.get(), new FlyingBloodParticle.Factory());
        event.register(ModParticles.FLYING_BLOOD_ENTITY.get(), new FlyingBloodEntityParticle.Factory());
        event.register(ModParticles.GENERIC.get(), new GenericParticle.Factory());
    }
}
