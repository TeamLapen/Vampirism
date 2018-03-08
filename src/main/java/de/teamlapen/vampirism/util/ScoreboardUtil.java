package de.teamlapen.vampirism.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteriaReadOnly;
import net.minecraft.scoreboard.ScoreObjective;

public class ScoreboardUtil {
    public final static IScoreCriteria FACTION_CRITERIA = new ScoreCriteriaReadOnly("vampirism:faction");
    public final static IScoreCriteria VAMPIRE_LEVEL_CRITERIA = new ScoreCriteriaReadOnly("vampirism:vampire");
    public final static IScoreCriteria HUNTER_LEVEL_CRITERIA = new ScoreCriteriaReadOnly("vampirism:hunter");

    private static boolean init = false;

    public static void init() {
        if (!init) {
            IScoreCriteria.INSTANCES.put(FACTION_CRITERIA.getName(), FACTION_CRITERIA);
            IScoreCriteria.INSTANCES.put(VAMPIRE_LEVEL_CRITERIA.getName(), VAMPIRE_LEVEL_CRITERIA);
            IScoreCriteria.INSTANCES.put(HUNTER_LEVEL_CRITERIA.getName(), HUNTER_LEVEL_CRITERIA);
            init = true;
        }

    }

    public static void updateScoreboard(EntityPlayer player, IScoreCriteria crit, int value) {
        if (!player.world.isRemote) {
            for (ScoreObjective scoreobjective : player.getWorldScoreboard().getObjectivesFromCriteria(crit)) {
                Score score = player.getWorldScoreboard().getOrCreateScore(player.getName(), scoreobjective);
                score.setScorePoints(value);
            }
        }
    }

}
