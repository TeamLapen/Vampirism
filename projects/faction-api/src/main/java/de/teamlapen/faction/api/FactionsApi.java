package de.teamlapen.faction.api;

import com.google.common.base.Preconditions;
import de.teamlapen.IFactionServices;
import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

/**
 * All interaction with the faction api should go through {@link #services()}
 */
public class FactionsApi {

    @UnknownNullability
    private static IFactionServices factionServices;

    public static IFactionPlayerHandler factionPlayerHandler(Player player) {
        return player.getData(FactionAttachments.FACTION_PLAYER_HANDLER);
    }

    public static IFactionServices services() {
        return factionServices;
    }

    @ApiStatus.Internal
    public static void init(IFactionServices services) {
        Preconditions.checkArgument(factionServices == null, "Services are already initialized");
        factionServices = services;
    }
}
