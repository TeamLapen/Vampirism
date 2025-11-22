package de.teamlapen.factions.api.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public interface TaskReward {

    Codec<TaskReward> CODEC = Codec.lazyInitialized(() -> FactionRegistries.TASK_REWARD.get().byNameCodec()).dispatch(TaskReward::codec, Function.identity());

    ITaskRewardInstance createInstance(IFactionPlayer<?> player);

    MapCodec<? extends TaskReward> codec();

    Component description();
}
