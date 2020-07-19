package de.teamlapen.vampirism.player.tasks;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;

import java.util.List;
import java.util.stream.Collectors;


public class TaskUtil {
    public static List<Task> getItemRewardTasks() {
        return ModRegistries.TASKS.getValues().stream().filter(task -> task.getReward() instanceof ItemReward).collect(Collectors.toList());
    }
}
