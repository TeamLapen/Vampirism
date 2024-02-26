package de.teamlapen.vampirism.api.entity.player.task;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public interface TaskReward {

    Codec<TaskReward> CODEC = ExtraCodecs.lazyInitializedCodec(() -> VampirismRegistries.TASK_REWARD.get().byNameCodec()).dispatch(TaskReward::codec, Function.identity());

    ITaskRewardInstance createInstance(IFactionPlayer<?> player);

    Codec<? extends TaskReward> codec();

    Component description();
}
