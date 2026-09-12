package de.teamlapen.faction.api;

import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import net.minecraft.world.entity.player.Player;

import java.util.ServiceLoader;

/**
 * All interaction with the faction api should go through {@link #services()}
 */
public class FactionsApi {

    public static IFactionPlayerHandler factionPlayerHandler(Player player) {
        return player.getData(FactionAttachments.FACTION_PLAYER_HANDLER);
    }

    /**
     * @return the {@link IFactionServices} implementation provided by Factions, discovered via {@link ServiceLoader}
     */
    public static IFactionServices services() {
        return Holder.SERVICES;
    }

    /**
     * Lazily loads the service implementation on first access, so the api can be used
     * without Factions having to explicitly hand its implementation over.
     */
    private static final class Holder {

        private static final IFactionServices SERVICES = load();

        private static IFactionServices load() {
            return ServiceLoader.load(IFactionServices.class, FactionsApi.class.getClassLoader())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No implementation of " + IFactionServices.class.getName() + " found. Is Factions installed correctly?"));
        }
    }
}
