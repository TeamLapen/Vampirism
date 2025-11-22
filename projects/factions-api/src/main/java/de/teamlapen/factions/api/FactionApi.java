package de.teamlapen.factions.api;

import de.teamlapen.factions.api.factions.IFactionPlayerHandler;
import net.minecraft.world.entity.player.Player;

public class FactionApi {

    public static IFactionPlayerHandler factionPlayerHandler(Player player) {
        return player.getData(FactionAttachments.FACTION_PLAYER_HANDLER);
    }
}
