package de.teamlapen.vampirism.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jetbrains.annotations.NotNull;

public class ScoreboardUtil {
    public final static ObjectiveCriteria FACTION_CRITERIA = ObjectiveCriteria.registerCustom("vampirism:faction");
    public final static ObjectiveCriteria VAMPIRE_LEVEL_CRITERIA = ObjectiveCriteria.registerCustom("vampirism:vampire");
    public final static ObjectiveCriteria HUNTER_LEVEL_CRITERIA = ObjectiveCriteria.registerCustom("vampirism:hunter");


    public static void updateScoreboard(@NotNull Player player, @NotNull ObjectiveCriteria crit, int value) {
        if (!player.level().isClientSide) {
            player.getScoreboard().forAllObjectives(crit, player.getScoreboardName(), (obj) -> obj.setScore(value));
        }
    }

}
