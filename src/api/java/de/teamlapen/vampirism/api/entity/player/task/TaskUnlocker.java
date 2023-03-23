package de.teamlapen.vampirism.api.entity.player.task;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public interface TaskUnlocker {

    Codec<TaskUnlocker> CODEC = ExtraCodecs.lazyInitializedCodec(() -> VampirismRegistries.TASK_UNLOCKER.get().getCodec()).dispatch(TaskUnlocker::codec, Function.identity());

    Component getDescription();

    boolean isUnlocked(IFactionPlayer<?> playerEntity);

    Codec<? extends TaskUnlocker> codec();
}
