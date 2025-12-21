package de.teamlapen.factions.api;

import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.registries.factions.DeferredFaction;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.world.entities.player.INeutralPlayer;
import net.minecraft.resources.Identifier;

import static de.teamlapen.factions.api.registries.ApiRegistryProvider.retrieveFaction;

public class Factions {

    public static final DeferredFaction<INeutralPlayer, IPlayableFaction<INeutralPlayer>> NEUTRAL = retrieveFaction(Keys.NEUTRAL);

    public static class Keys {
        public static final Identifier NEUTRAL = FResourceLocation.mod("neutral");
    }
}
