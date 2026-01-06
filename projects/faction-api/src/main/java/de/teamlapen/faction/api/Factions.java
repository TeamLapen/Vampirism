package de.teamlapen.faction.api;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.INeutralPlayer;
import net.minecraft.resources.Identifier;

import static de.teamlapen.faction.api.registries.ApiRegistryProvider.retrieveFaction;

public class Factions {

    public static final DeferredFaction<INeutralPlayer, IPlayableFaction<INeutralPlayer>> NEUTRAL = retrieveFaction(Keys.NEUTRAL);

    public static class Keys {
        public static final Identifier NEUTRAL = FIdentifier.mod("neutral");
    }
}
