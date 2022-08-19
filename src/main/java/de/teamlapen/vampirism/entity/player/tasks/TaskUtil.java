package de.teamlapen.vampirism.entity.player.tasks;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.util.RegUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


public class TaskUtil {
    public static @NotNull List<Task> getItemRewardTasks() {
        return RegUtil.values(ModRegistries.TASKS).stream().filter(task -> task.getReward() instanceof ItemReward).collect(Collectors.toList());
    }
}
