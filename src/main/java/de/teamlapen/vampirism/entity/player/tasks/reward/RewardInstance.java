package de.teamlapen.vampirism.entity.player.tasks.reward;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;

public class RewardInstance implements ITaskRewardInstance {


    @Override
    public void applyReward(IFactionPlayer<?> player) {

    }

    @Override
    public Codec<? extends ITaskRewardInstance> codec() {
        return null;
    }
}
