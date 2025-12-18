package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.misc.mixin.accessor.ObjectiveCriteriaAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jetbrains.annotations.NotNull;

public class ScoreboardUtil {
    public final static ObjectiveCriteria VAMPIRE_LEVEL_CRITERIA = ObjectiveCriteriaAccessor.invokeRegisterCustom("vampirism:vampire");
    public final static ObjectiveCriteria HUNTER_LEVEL_CRITERIA = ObjectiveCriteriaAccessor.invokeRegisterCustom("vampirism:hunter");


    public static void updateScoreboard(@NotNull Player player, @NotNull ObjectiveCriteria crit, int value) {
        de.teamlapen.factions.common.util.ScoreboardUtil.updateScoreboard(player, crit, value);
    }

}
