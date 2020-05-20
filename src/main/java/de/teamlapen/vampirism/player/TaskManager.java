package de.teamlapen.vampirism.player;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskFinishedPacket;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class TaskManager implements ITaskManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private final @Nonnull PlayerEntity player;
    private final @Nonnull IPlayableFaction<?> faction;
    private final @Nonnull Map<Task.Variant, Set<Task>> completedTasks = Maps.newHashMap();
    private final @Nonnull Map<Task.Variant, Set<Task>> availableTasks = Maps.newHashMap();
    private final @Nonnull Map<Task, Map<ResourceLocation, Integer>> stats = Maps.newHashMap();
    private final @Nonnull Map<Task, Map<EntityType<?>, Integer>> entityStats = Maps.newHashMap();
    private boolean init;
    private long taskUpdateLast;

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
            this.removeRequirements(task);
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
        this.completedTasks.computeIfAbsent(task.getVariant(), variant -> Sets.newHashSet()).add(task);
        this.availableTasks.get(task.getVariant()).remove(task);
        this.getStats().remove(task);
        this.getEntityStats().remove(task);
        ModRegistries.TASKS.getValues().stream().filter(task1 -> task1.requireParent() && task == task1.getParentTask()).forEach(task1 -> this.availableTasks.computeIfAbsent(task1.getVariant(), variant -> Sets.newHashSet()).add(task1));
        this.updateStats();
        return true;
    }

    @Override
    public void removeRequirements(@Nonnull Task task) {
        if (task.getRequirement().getType().equals(TaskRequirement.Type.ITEMS)) {
            this.player.inventory.clearMatchingItems(itemStack -> itemStack.getItem() == task.getRequirement().getStat(), task.getRequirement().getAmount());
        }
    }

    @Nonnull
    @Override
    public Set<Task> getCompletedTasks(Task.Variant variant) {
        return completedTasks.getOrDefault(variant, ImmutableSet.of());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void setCompletedTasks(@Nonnull Collection<Task> tasks) {
        this.completedTasks.clear();
        ModRegistries.TASKS.getValues().stream().filter(task -> (faction.equals(task.getFaction()) || task.getFaction() == null) && !task.requireParent()).forEach(task1 -> this.availableTasks.computeIfAbsent(task1.getVariant(), variant -> Sets.newHashSet()).add(task1));
        tasks.forEach(task -> this.completedTasks.computeIfAbsent(task.getVariant(), variant -> Sets.newHashSet()).add(task));
        tasks.forEach(task -> this.availableTasks.get(task.getVariant()).remove(task));
    }

    @Override
    public void applyRewards(Task task) {
        task.getReward().applyReward(this.player);
    }

    @Override
    public boolean hasAvailableTasks(Task.Variant variant) {
        this.updateTasks();
        return !this.availableTasks.isEmpty() && this.availableTasks.get(variant) != null && !this.availableTasks.get(variant).isEmpty();
    }

    /**
     * returns {@link TaskManager#availableTasks} and initiate the Stats on first call
     */
    @Nonnull
    @Override
    public Set<Task> getAvailableTasks(Task.Variant variant) {
        return this.availableTasks.get(variant);
    }

    @Nonnull
    public Set<Task> getCompletableTasks(Task.Variant variant) {
        return this.availableTasks.get(variant).stream().filter(this::canCompleteTask).collect(Collectors.toSet());
    }

    @Override
    @Nonnull
    public IPlayableFaction<?> getFaction() {
        return faction;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean canCompleteTask(Task task) {
        if (task.requireParent() && this.completedTasks.values().stream().noneMatch(task1 -> task1.contains(task.getParentTask())))
            return false;
        switch (task.getRequirement().getType()) {
            case STATS:
                if (this.player.getEntityWorld().isRemote()) return false;
                if (((ServerPlayerEntity) this.player).getStats().getValue(Stats.CUSTOM.get((ResourceLocation) task.getRequirement().getStat())) < this.getStats().get(task).get(task.getRequirement().getStat()) + task.getRequirement().getAmount())
                    return false;
                break;
            case ENTITY:
                if (this.player.getEntityWorld().isRemote()) return false;
                if (((ServerPlayerEntity) this.player).getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) task.getRequirement().getStat())) < this.getEntityStats().get(task).get(task.getRequirement().getStat()) + task.getRequirement().getAmount())
                    return false;
                break;
            case ITEMS:
                ItemStack stack = ((ItemRequirement) task.getRequirement()).getItemStack();
                if (this.player.inventory.count(stack.getItem()) < stack.getCount()) return false;
                break;
            case BOOLEAN:
                if (!(Boolean) task.getRequirement().getStat()) return false;
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void reset() {
        this.completedTasks.clear();
        ModRegistries.TASKS.getValues().stream().filter(task -> (faction.equals(task.getFaction()) || task.getFaction() == null) && !task.requireParent()).forEach(task1 -> this.availableTasks.computeIfAbsent(task1.getVariant(), variant -> Sets.newHashSet()).add(task1));
        this.stats.clear();
        this.entityStats.clear();
        this.init = true;
    }

    public void updateTasks() {
        if (this.taskUpdateLast < this.player.getEntityWorld().getGameTime() / 24000) {
            Set<Task> completed = this.completedTasks.get(Task.Variant.REPEATABLE);
            Set<Task> available = this.availableTasks.get(Task.Variant.REPEATABLE);
            if (completed != null && available != null) {
                for (int i = 0; i < this.taskUpdateLast - this.player.getEntityWorld().getGameTime() / 24000; i++) {
                    if (!this.completedTasks.isEmpty()) {
                        Task task = completed.stream().skip(new Random().nextInt(completed.size())).findFirst().orElse(null);
                        if (task != null) {
                            completed.remove(task);
                            available.add(task);
                        }
                    }
                }
                this.taskUpdateLast = this.player.getEntityWorld().getGameTime() / 24000;
            }
        }
    }

    /**
     * cannot be called in constructor, because player is not constructed yet
     */
    private void updateStats() {
        for (Set<Task> tasks : this.availableTasks.values()) {
            for (Task task : tasks) {
                if (this.getStats().containsKey(task) || this.getEntityStats().containsKey(task)) continue;
                switch (task.getRequirement().getType()) {
                    case STATS:
                        if (!player.getEntityWorld().isRemote()) {
                            this.getStats().computeIfAbsent(task, task1 -> Maps.newHashMap()).put((ResourceLocation) task.getRequirement().getStat(), ((ServerPlayerEntity) this.player).getStats().getValue(Stats.CUSTOM.get((ResourceLocation) task.getRequirement().getStat())));
                        }
                        break;
                    case ENTITY:
                        if (!player.getEntityWorld().isRemote()) {
                            this.getEntityStats().computeIfAbsent(task, task1 -> Maps.newHashMap()).put((EntityType<?>) task.getRequirement().getStat(), ((ServerPlayerEntity) this.player).getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) task.getRequirement().getStat())));
                        }
                        break;
                    default:
                }
            }
        }
        this.getStats().entrySet().removeIf(entry -> !this.availableTasks.get(entry.getKey().getVariant()).contains(entry.getKey()));
        this.getEntityStats().entrySet().removeIf(entry -> !this.availableTasks.get(entry.getKey().getVariant()).contains(entry.getKey()));
    }

    @Nonnull
    private Map<Task, Map<ResourceLocation, Integer>> getStats() {
        if (this.init) {
            this.init = false;
            this.updateStats();
        }
        return stats;
    }

    @Nonnull
    private Map<Task, Map<EntityType<?>, Integer>> getEntityStats() {
        if (this.init) {
            this.init = false;
            this.updateStats();
        }
        return entityStats;
    }

    public void writeNBT(CompoundNBT compoundNBT) {
        //completed tasks
        if (!this.completedTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.completedTasks.forEach((variant, tasks) -> tasks.forEach(task -> tasksNBT.putBoolean(task.getRegistryName().toString(), true)));
            compoundNBT.put("tasks", tasksNBT);
        }
        //stats
        if (!this.stats.isEmpty()) {
            CompoundNBT stats = new CompoundNBT();
            this.stats.forEach((task, requirementStats) -> {
                CompoundNBT statsNBT = new CompoundNBT();
                requirementStats.forEach((resourceLocation, amount) -> statsNBT.putInt(resourceLocation.toString(), amount));
                stats.put(task.getRegistryName().toString(), statsNBT);
            });
            compoundNBT.put("stats", stats);
        }
        //entities
        if (!this.entityStats.isEmpty()) {
            CompoundNBT entity = new CompoundNBT();
            this.entityStats.forEach((task, requirementStats) -> {
                CompoundNBT statsNBT = new CompoundNBT();
                requirementStats.forEach((entityType, amount) -> statsNBT.putInt(entityType.getRegistryName().toString(), amount));
                entity.put(task.getRegistryName().toString(), statsNBT);
            });
            compoundNBT.put("entities", entity);
        }
    }

    public <T> void readNBT(CompoundNBT compoundNBT) {
        //completed tasks
        if (compoundNBT.contains("tasks")) {
            compoundNBT.getCompound("tasks").keySet().forEach(taskId -> {
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                if (task != null) {
                    this.addCompletedTask(task);
                }
            });
        }
        //stats
        if (compoundNBT.contains("stats")) {
            CompoundNBT tasks = compoundNBT.getCompound("stats");
            tasks.keySet().forEach(taskId -> {
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                if (task != null) {
                    this.stats.put(task, Maps.newHashMap());
                    tasks.getCompound(taskId).keySet().forEach(stat -> this.stats.computeIfAbsent(task, task1 -> Maps.newHashMap()).put(new ResourceLocation(stat), tasks.getCompound(taskId).getInt(stat)));
                }
            });
        }
        //entities
        if (compoundNBT.contains("entities")) {
            CompoundNBT tasks = compoundNBT.getCompound("entities");
            tasks.keySet().forEach(taskId -> {
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                if (task != null) {
                    this.entityStats.put(task, Maps.newHashMap());
                    tasks.getCompound(taskId).keySet().forEach(stat -> this.entityStats.computeIfAbsent(task, task1 -> Maps.newHashMap()).put(ForgeRegistries.ENTITIES.getValue(new ResourceLocation(stat)), tasks.getCompound(taskId).getInt(stat)));
                }
            });
        }
    }

}
