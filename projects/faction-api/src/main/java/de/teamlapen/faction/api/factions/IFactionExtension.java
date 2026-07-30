package de.teamlapen.faction.api.factions;

import de.teamlapen.faction.api.factions.level.FactionUpdate;
import net.minecraft.world.entity.player.Player;

/**
 * Implemented by extension value instances (registered via {@link FactionProperties#extension}) that need to react
 * to the owning player leaving the faction that provided them.
 */
public interface IFactionExtension {

    /**
     * Called when the owning player leaves the faction that provided this extension.
     */
    void onLeaveFaction(Player player);

    default void setLevel(FactionUpdate change) {

    }
}
