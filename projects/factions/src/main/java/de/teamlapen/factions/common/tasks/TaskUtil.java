package de.teamlapen.factions.common.tasks;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.tasks.Task;
import de.teamlapen.factions.common.tasks.reward.ItemReward;
import net.minecraft.core.RegistryAccess;

import java.util.List;
import java.util.stream.Collectors;


public class TaskUtil {
    public static List<Task> getItemRewardTasks(RegistryAccess access) {
        return access.lookupOrThrow(FactionRegistries.Keys.TASK).stream().filter(obj -> obj.reward() instanceof ItemReward).collect(Collectors.toList());
    }
}
