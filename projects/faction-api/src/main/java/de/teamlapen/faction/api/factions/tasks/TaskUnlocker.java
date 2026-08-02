package de.teamlapen.faction.api.factions.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public interface TaskUnlocker {

    Codec<TaskUnlocker> CODEC = Codec.lazyInitialized(() -> FactionRegistries.TASK_UNLOCKER.get().byNameCodec()).dispatch(TaskUnlocker::codec, Function.identity());

    Component getDescription();

    boolean isUnlocked(ITaskManager taskPlayer, IFactionPlayer<?> playerEntity);

    MapCodec<? extends TaskUnlocker> codec();
}
