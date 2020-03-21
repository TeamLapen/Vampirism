package de.teamlapen.vampirism.player;

import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskManager implements ITaskManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private final @Nonnull IPlayableFaction<? extends IFactionPlayer> faction;
    private final Set<Task> completedTasks = Sets.newHashSet();
    private final Set<Task> availableTasks = Sets.newHashSet();

    public TaskManager(@Nonnull IPlayableFaction<? extends IFactionPlayer> faction) {
        this.faction = faction;
        this.reset();
    }

    @Override
    public void completeTask(@Nonnull Task task) {
        if (!(this.faction.equals(task.getFaction()) || task.getFaction() == null)) return;
        this.completedTasks.add(task);
        this.availableTasks.remove(task);
    }

    public Set<Task> getCompletedTasks() {
        return completedTasks;
    }

    public Set<Task> getAvailableTasks() {
        return availableTasks;
    }

    @Override
    public void reset() {
        this.completedTasks.clear();
        this.availableTasks.addAll(ModRegistries.TASKS.getValues().stream().filter(task -> faction.equals(task.getFaction()) || task.getFaction() == null).collect(Collectors.toList()));
    }

    public void writeNBT(CompoundNBT compoundNBT) {
        if (this.completedTasks.isEmpty()) return;
        CompoundNBT tasks = new CompoundNBT();
        this.completedTasks.forEach(task -> tasks.putBoolean(task.getRegistryName().toString(), true));
        compoundNBT.put("tasks", tasks);
    }

    public void readNBT(CompoundNBT compoundNBT) {
        if (!compoundNBT.contains("tasks")) return;
        compoundNBT.getCompound("tasks").keySet().forEach(taskId -> {
            Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
            if (task != null) {
                this.completeTask(task);
            }
        });
    }
}
