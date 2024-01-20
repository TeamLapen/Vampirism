package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.mixin.ObjectiveCriteriaAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.jetbrains.annotations.NotNull;

public class ScoreboardUtil {
    public final static ObjectiveCriteria FACTION_CRITERIA = ObjectiveCriteriaAccessor.registerCustom("vampirism:faction");
    public final static ObjectiveCriteria VAMPIRE_LEVEL_CRITERIA = ObjectiveCriteriaAccessor.registerCustom("vampirism:vampire");
    public final static ObjectiveCriteria HUNTER_LEVEL_CRITERIA = ObjectiveCriteriaAccessor.registerCustom("vampirism:hunter");


    public static void updateScoreboard(@NotNull Player player, @NotNull ObjectiveCriteria crit, int value) {
        if (!player.level().isClientSide) {
            player.getScoreboard().forAllObjectives(crit, player, (obj) -> obj.set(value));
        }
    }

}
