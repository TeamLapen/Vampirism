package de.teamlapen.vampirism.api.entity.player.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public interface TaskReward {

    Codec<TaskReward> CODEC = Codec.lazyInitialized(() -> VampirismRegistries.TASK_REWARD.get().byNameCodec()).dispatch(TaskReward::codec, Function.identity());

    ITaskRewardInstance createInstance(IFactionPlayer<?> player);

    MapCodec<? extends TaskReward> codec();

    Component description();
}
