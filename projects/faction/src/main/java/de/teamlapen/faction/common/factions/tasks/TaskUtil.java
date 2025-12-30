package de.teamlapen.faction.common.factions.tasks;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.common.factions.tasks.reward.ItemReward;
import net.minecraft.core.RegistryAccess;

import java.util.List;
import java.util.stream.Collectors;


public class TaskUtil {
    public static List<Task> getItemRewardTasks(RegistryAccess access) {
        return access.lookupOrThrow(FactionRegistries.Keys.TASK).stream().filter(obj -> obj.reward() instanceof ItemReward).collect(Collectors.toList());
    }
}
