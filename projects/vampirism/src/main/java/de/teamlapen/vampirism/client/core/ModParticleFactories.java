package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.particles.DarkSpruceParticleProvider;
import de.teamlapen.vampirism.client.particles.FlyingBloodEntityParticle;
import de.teamlapen.vampirism.client.particles.FlyingBloodParticle;
import de.teamlapen.vampirism.client.particles.GenericParticle;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.minecraft.client.particle.SpellParticle;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

public class ModParticleFactories {

    public static void registerFactories(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SWORD_CHARGE.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SWORD_CHARGED.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.PEDESTAL.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.MOTHER.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.ALTAR_INFUSION.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.FLYING_BLOOD_ENTITY.get(), FlyingBloodEntityParticle.Provider::new);
        event.registerSpriteSet(ModParticles.GENERIC.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SANGUINARE.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticles.DARK_SPRUCE_OAK_LEAVES.get(), DarkSpruceParticleProvider::new);
    }
}
