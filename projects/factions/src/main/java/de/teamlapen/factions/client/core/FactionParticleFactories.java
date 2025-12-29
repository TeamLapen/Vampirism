package de.teamlapen.factions.client.core;

import de.teamlapen.factions.client.particles.FactionSurroundingParticle;
import de.teamlapen.factions.common.core.FactionParticles;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.ApiStatus;

public class FactionParticleFactories {

    @ApiStatus.Internal
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FactionParticles.FACTION_SURROUNDING.get(), FactionSurroundingParticle.Provider::new);
    }
}
