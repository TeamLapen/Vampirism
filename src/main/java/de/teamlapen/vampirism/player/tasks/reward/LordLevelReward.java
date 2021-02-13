package de.teamlapen.vampirism.player.tasks.reward;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;


/**
 * Reward to level up (1 level) as lord
 */
public class LordLevelReward implements TaskReward {

    public final int targetLevel;

    public LordLevelReward(int targetLevel) {
        this.targetLevel = targetLevel;
    }


    @Override
    public void applyReward(IFactionPlayer<?> p) {
        FactionPlayerHandler.getOpt(p.getRepresentingPlayer()).ifPresent(fph -> {
            if (fph.getLordLevel() == targetLevel - 1) {
                fph.setLordLevel(targetLevel);
            }
        });
    }
}
