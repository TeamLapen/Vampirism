package de.teamlapen.vampirism.player.tasks;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.util.RegUtil;

import java.util.List;
import java.util.stream.Collectors;


public class TaskUtil {
    public static List<Task> getItemRewardTasks() {
        return RegUtil.values(VampirismRegistries.TASKS).stream().filter(task -> task.getReward() instanceof ItemReward).collect(Collectors.toList());
    }
}
