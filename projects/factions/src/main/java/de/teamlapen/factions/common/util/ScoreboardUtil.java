package de.teamlapen.factions.common.util;

import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardUtil {
    public final static ObjectiveCriteria FACTION_CRITERIA = ObjectiveCriteria.registerCustom(FResourceLocation.modString("faction"));

    public static void updateScoreboard(Player player, ObjectiveCriteria crit, int value) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.level().getScoreboard().forAllObjectives(crit, serverPlayer, (obj) -> obj.set(value));
        }
    }
}
