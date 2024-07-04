package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.particle.FlyingBloodEntityParticle;
import de.teamlapen.vampirism.client.particle.FlyingBloodParticle;
import de.teamlapen.vampirism.client.particle.GenericParticle;
import de.teamlapen.vampirism.client.particle.MistSmokeParticle;
import de.teamlapen.vampirism.core.ModParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ModParticleFactories {

    static void registerFactories(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticles.FLYING_BLOOD.get(), new FlyingBloodParticle.Factory());
        event.registerSpecial(ModParticles.FLYING_BLOOD_ENTITY.get(), new FlyingBloodEntityParticle.Factory());
        event.registerSpecial(ModParticles.GENERIC.get(), new GenericParticle.Factory());
        event.registerSpriteSet(ModParticles.MIST_SMOKE.get(), MistSmokeParticle.Provider::new);
    }
}
