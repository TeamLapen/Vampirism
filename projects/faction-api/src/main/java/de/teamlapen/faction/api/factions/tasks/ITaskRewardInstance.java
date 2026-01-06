package de.teamlapen.faction.api.factions.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;

import java.util.function.Function;

public interface ITaskRewardInstance {

    Codec<ITaskRewardInstance> CODEC = Codec.lazyInitialized(() -> FactionRegistries.TASK_REWARD_INSTANCE.get().byNameCodec()).dispatch(ITaskRewardInstance::codec, Function.identity());

    /**
     * applies the reward to the player upon task completion
     *
     * @param player the player which completed the task
     */
    void applyReward(IFactionPlayer<?> player);

    MapCodec<? extends ITaskRewardInstance> codec();

}
