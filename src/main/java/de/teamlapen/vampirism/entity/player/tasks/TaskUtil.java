package de.teamlapen.vampirism.entity.player.tasks;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


public class TaskUtil {
    public static @NotNull List<Task> getItemRewardTasks(RegistryAccess access) {
        return access.registryOrThrow(VampirismRegistries.TASK_ID).stream().filter(obj -> obj.getReward() instanceof ItemReward).collect(Collectors.toList());
    }
}
