package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;

public class FactionLootContextParams {

    /**
     * The lord of the minion. Only present if the lord is online.
     */
    public static final ContextKey<Player> LORD_PLAYER = new ContextKey<>(FIdentifier.mod("lord_player"));
}
