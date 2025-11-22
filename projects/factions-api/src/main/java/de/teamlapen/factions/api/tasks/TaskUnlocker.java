package de.teamlapen.factions.api.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.factions.api.FactionRegistries;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public interface TaskUnlocker {

    Codec<TaskUnlocker> CODEC = Codec.lazyInitialized(() -> FactionRegistries.TASK_UNLOCKER.get().byNameCodec()).dispatch(TaskUnlocker::codec, Function.identity());

    Component getDescription();

    <T extends ITaskPlayer<T>> boolean isUnlocked(T playerEntity);

    MapCodec<? extends TaskUnlocker> codec();
}
