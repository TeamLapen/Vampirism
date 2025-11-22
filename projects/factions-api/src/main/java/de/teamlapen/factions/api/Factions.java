package de.teamlapen.factions.api;

import de.teamlapen.factions.api.entities.player.INeutralPlayer;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.registries.factions.DeferredFaction;
import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.resources.ResourceLocation;

import static de.teamlapen.factions.api.registries.ApiRegistryProvider.retrieveFaction;

public class Factions {

    public static final DeferredFaction<INeutralPlayer, IPlayableFaction<INeutralPlayer>> NEUTRAL = retrieveFaction(Keys.NEUTRAL);

    public static class Keys {
        public static final ResourceLocation NEUTRAL = FResourceLocation.mod("neutral");
    }
}
