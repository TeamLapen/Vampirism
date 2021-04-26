package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

public interface TaskReward {

    /**
     * applies the reward to the player upon task completion
     *
     * @param player the player which completed the task
     */
    @Deprecated
    void applyReward(IFactionPlayer<?> player);

    ITaskRewardInstance createInstance(IFactionPlayer<?> player);
}
