package de.teamlapen.vampirism.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreCriteria;

public class ScoreboardUtil {
    public final static ScoreCriteria FACTION_CRITERIA = new ScoreCriteria("vampirism:faction", true, ScoreCriteria.RenderType.INTEGER);
    public final static ScoreCriteria VAMPIRE_LEVEL_CRITERIA = new ScoreCriteria("vampirism:vampire", true, ScoreCriteria.RenderType.INTEGER);
    public final static ScoreCriteria HUNTER_LEVEL_CRITERIA = new ScoreCriteria("vampirism:hunter", true, ScoreCriteria.RenderType.INTEGER);


    public static void updateScoreboard(EntityPlayer player, ScoreCriteria crit, int value) {
        if (!player.world.isRemote) {
            player.getWorldScoreboard().forAllObjectives(crit, player.getScoreboardName(), (obj) -> {
                obj.setScorePoints(value);
            });
        }
    }

}
