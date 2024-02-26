package de.teamlapen.vampirism.api.entity.player.task;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public interface ITaskRewardInstance {

    Codec<ITaskRewardInstance> CODEC = ExtraCodecs.lazyInitializedCodec(() -> VampirismRegistries.TASK_REWARD_INSTANCE.get().byNameCodec()).dispatch(ITaskRewardInstance::codec, Function.identity());

    /**
     * applies the reward to the player upon task completion
     *
     * @param player the player which completed the task
     */
    void applyReward(IFactionPlayer<?> player);

    Codec<? extends ITaskRewardInstance> codec();

}
