package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

public interface TaskReward {

    ITaskRewardInstance createInstance(IFactionPlayer<?> player);
}
