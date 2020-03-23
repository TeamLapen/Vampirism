package de.teamlapen.vampirism.player;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskFinishedPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TaskManager implements ITaskManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private final @Nonnull PlayerEntity player;
    private final @Nonnull IPlayableFaction<?> faction;
    private final @Nonnull Set<Task> completedTasks = Sets.newHashSet();
    private final @Nonnull Set<Task> availableTasks = Sets.newHashSet();
    private final @Nonnull Map<Task, Map<EntityType<?>, Integer>> killStats = Maps.newHashMap();
    private boolean init = true;

    public TaskManager(@Nonnull PlayerEntity player, @Nonnull IPlayableFaction<?> faction) {
        this.faction = faction;
        this.player = player;
        this.reset();
    }

    @Nonnull
    public static Set<Task> getTasks(PlayerEntity player, NonNullFunction<? super ITaskManager, Set<Task>> mapper) {
        return FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(IFactionPlayer::getTaskManager).map(mapper).orElse(ImmutableSet.of());
    }

    /**
     * completes task and informs the other Dist about the completion
     */
    @Override
    public void completeTask(@Nonnull Task task) {
        if (addCompletedTask(task)) {
            if (player.world.isRemote()) {
                VampirismMod.dispatcher.sendToServer(new TaskFinishedPacket(task));
            } else {
                VampirismMod.dispatcher.sendTo(new TaskFinishedPacket(task), (ServerPlayerEntity) player);
            }
        }
    }

    /**
     * simply adds the given task to the completion list
     * or not if the task is not applicant to the {@link TaskManager#player}
     *
     * @return if the task got added
     */
    @Override
    public boolean addCompletedTask(@Nonnull Task task) {
        if (!(this.faction.equals(task.getFaction()) || task.getFaction() == null)) return false;
        this.completedTasks.add(task);
        this.getAvailableTasks().remove(task);
        return true;
    }

    @Nonnull
    @Override
    public Set<Task> getCompletedTasks() {
        return completedTasks;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setCompletedTasks(@Nonnull Collection<Task> tasks) {
        this.completedTasks.clear();
        this.completedTasks.addAll(tasks);
        this.availableTasks.removeAll(tasks);
    }

    /**
     * returns {@link TaskManager#availableTasks} and initiate the Stats on first call
     */
    @Nonnull
    @Override
    public Set<Task> getAvailableTasks() {
        if (this.init) {
            this.init = false;
            this.updateKillStats();
        }
        return this.availableTasks;
    }

    @Nonnull
    public Set<Task> getCompletableTasks() {
        return this.getAvailableTasks().stream().filter(this::canCompleteTask).collect(Collectors.toSet());
    }

    public boolean canCompleteTask(Task task) {
        if (this.player.getEntityWorld().isRemote()) return false;
        for (TaskRequirement requirement : task.getRequirements()) {
            if (requirement.getType().equals(TaskRequirement.Type.KILLS)) {
                EntityType<?> entityType = ((KillRequirement) requirement).getEntityType();
                int amount = ((KillRequirement) requirement).getAmount();
                if (((ServerPlayerEntity) this.player).getStats().getValue(Stats.ENTITY_KILLED.get(entityType)) < killStats.get(task).get(entityType) + amount)
                    return false;
            } else if (requirement.getType().equals(TaskRequirement.Type.ITEMS)) {
                ItemStack stack = ((ItemRequirement) requirement).getItemRequirement();
                if (this.player.inventory.count(stack.getItem()) < stack.getCount()) return false;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void reset() {
        this.completedTasks.clear();
        this.availableTasks.addAll(ModRegistries.TASKS.getValues().stream().filter(task -> faction.equals(task.getFaction()) || task.getFaction() == null).collect(Collectors.toList()));
        this.killStats.clear();
    }

    /**
     * cannot be called in constructor, because player is not constructed jet
     */
    private void updateKillStats() {
        if (player.getEntityWorld().isRemote()) return;
        for (Task task : this.availableTasks) {
            if (this.killStats.containsKey(task)) continue;
            Map<EntityType<?>, Integer> stats = null;
            for (TaskRequirement requirement : task.getRequirements()) {
                if (!requirement.getType().equals(TaskRequirement.Type.KILLS)) continue;
                EntityType<?> entityType = ((KillRequirement) requirement).getEntityType();
                if (stats == null) {
                    stats = Maps.newHashMap();
                }
                Stat<EntityType<?>> stat = Stats.ENTITY_KILLED.get(entityType);
                stats.put(entityType, ((ServerPlayerEntity) this.player).getStats().getValue(stat));
            }
            if (stats != null) {
                this.killStats.put(task, stats);
            }
        }
    }

    public void writeNBT(CompoundNBT compoundNBT) {
        if (this.completedTasks.isEmpty()) return;
        CompoundNBT tasks = new CompoundNBT();
        this.completedTasks.forEach(task -> tasks.putBoolean(task.getRegistryName().toString(), true));
        CompoundNBT stats = new CompoundNBT();
        this.killStats.forEach((key, value) -> {
            CompoundNBT statNBT = new CompoundNBT();
            value.forEach((key1, value1) -> statNBT.putInt(key1.getRegistryName().toString(), value1));
            stats.put(key.getRegistryName().toString(), statNBT);
        });
        compoundNBT.put("stats", stats);
        compoundNBT.put("tasks", tasks);
    }

    public void readNBT(CompoundNBT compoundNBT) {
        if (compoundNBT.contains("tasks")) {
            compoundNBT.getCompound("tasks").keySet().forEach(taskId -> {
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                if (task != null) {
                    this.addCompletedTask(task);
                }
            });
        }
        if (compoundNBT.contains("stats")) {
            CompoundNBT stats = compoundNBT.getCompound("stats");
            stats.keySet().forEach(taskId -> {
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                if (task != null) {
                    this.killStats.put(task, Maps.newHashMap());
                    CompoundNBT stat = stats.getCompound(taskId);
                    stat.keySet().forEach(entityTypeId -> {
                        EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityTypeId));
                        if (entityType != null) {
                            this.killStats.get(task).put(entityType, stat.getInt(entityTypeId));
                        }
                    });
                }
            });
        }
    }

}
