package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.particles.*;
import de.teamlapen.vampirism.common.core.ModParticles;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

public class ModParticleFactories {

    public static void registerFactories(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SWORD_CHARGE.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SWORD_CHARGED.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.MOTHER.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.ALTAR_INFUSION.get(), FlyingBloodParticle.Provider::new);
        event.registerSpriteSet(ModParticles.FLYING_BLOOD_ENTITY.get(), FlyingBloodEntityParticle.Provider::new);
        event.registerSpriteSet(ModParticles.BLOOD_SHRED.get(), BloodShredParticle.Provider::new);
        event.registerSpriteSet(ModParticles.GENERIC.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SANGUINARE.get(), SanguinareParticle.Provider::new);
        event.registerSpriteSet(ModParticles.DARK_SPRUCE_OAK_LEAVES.get(), DarkSpruceParticleProvider::new);
        event.registerSpriteSet(ModParticles.WHISPERS_OF_THE_VEIL.get(), WhispersOfTheVeilParticle.Factory::new);
    }
}
