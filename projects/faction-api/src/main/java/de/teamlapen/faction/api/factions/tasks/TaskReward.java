package de.teamlapen.faction.api.factions.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public interface TaskReward {

    Codec<TaskReward> CODEC = Codec.lazyInitialized(() -> FactionRegistries.TASK_REWARD.get().byNameCodec()).dispatch(TaskReward::codec, Function.identity());

    ITaskRewardInstance createInstance(IFactionPlayer<?> player);

    MapCodec<? extends TaskReward> codec();

    Component description();
}
