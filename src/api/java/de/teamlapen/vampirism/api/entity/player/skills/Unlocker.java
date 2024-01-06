package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;

public interface Unlocker {
    boolean test(IFactionPlayerHandler player);
}
