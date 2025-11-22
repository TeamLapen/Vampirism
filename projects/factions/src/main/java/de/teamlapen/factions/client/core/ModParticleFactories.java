package de.teamlapen.factions.client.core;

import de.teamlapen.factions.client.particles.FactionSurroundingParticle;
import de.teamlapen.factions.common.core.FactionParticles;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

public class ModParticleFactories {

    static void registerFactories(@NotNull RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FactionParticles.FACTION_SURROUNDING.get(), FactionSurroundingParticle.Provider::new);
    }
}
