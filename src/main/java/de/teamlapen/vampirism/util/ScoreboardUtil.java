package de.teamlapen.vampirism.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardUtil {
    public final static ObjectiveCriteria FACTION_CRITERIA = new ObjectiveCriteria("vampirism:faction");
    public final static ObjectiveCriteria VAMPIRE_LEVEL_CRITERIA = new ObjectiveCriteria("vampirism:vampire");
    public final static ObjectiveCriteria HUNTER_LEVEL_CRITERIA = new ObjectiveCriteria("vampirism:hunter");


    public static void updateScoreboard(Player player, ObjectiveCriteria crit, int value) {
        if (!player.level.isClientSide) {
            player.getScoreboard().forAllObjectives(crit, player.getScoreboardName(), (obj) -> {
                obj.setScore(value);
            });
        }
    }

}
