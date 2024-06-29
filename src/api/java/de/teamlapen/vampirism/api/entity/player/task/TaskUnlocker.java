package de.teamlapen.vampirism.api.entity.player.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.ITaskPlayer;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public interface TaskUnlocker {

    Codec<TaskUnlocker> CODEC = Codec.lazyInitialized(() -> VampirismRegistries.TASK_UNLOCKER.get().byNameCodec()).dispatch(TaskUnlocker::codec, Function.identity());

    Component getDescription();

    <T extends ITaskPlayer<T>> boolean isUnlocked(T playerEntity);

    MapCodec<? extends TaskUnlocker> codec();
}
