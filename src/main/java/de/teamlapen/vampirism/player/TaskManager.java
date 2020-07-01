package de.teamlapen.vampirism.player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.network.TaskStatusPacket;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.Map;
import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

public class TaskManager implements ITaskManager {
    private final @Nonnull IPlayableFaction<?> faction;
    private final @Nonnull ServerPlayerEntity player;
    private final @Nonnull IFactionPlayer<?> factionPlayer;
    private final @Nonnull Map<Task.Variant, Set<Task>> completedTasks = Maps.newHashMap();
    private final @Nonnull Map<Task.Variant, Set<Task>> availableTasks = Maps.newHashMap();
    private final @Nonnull Map<Task, Integer> stats = Maps.newHashMap();

    public TaskManager(IFactionPlayer<?> factionPlayer, @Nonnull IPlayableFaction<?> faction) {
        this.faction = faction;
        this.player = (ServerPlayerEntity)factionPlayer.getRepresentingPlayer();
        this.factionPlayer = factionPlayer;
    }

    @Override
    public void completeTask(@Nonnull Task task) {
        if (addCompletedTask(task)) {
            this.removeRequirements(task);
            this.applyRewards(task);
            this.updateClient();
        }
    }

    @Override
    public void updateClient() {
        if(player.openContainer instanceof TaskMasterContainer) {
            Task.Variant taskType = ((TaskMasterContainer)player.openContainer).getVariant();
            VampirismMod.dispatcher.sendTo(new TaskStatusPacket(getCompletableTasks(taskType), getCompletedTasks(taskType), getAvailableTasks(taskType), player.openContainer.windowId), player);
        }
    }

    /**
     * @param task the task that should be checked
     * @return whether the task is unlocked my the player or not
     */
    public boolean isTaskUnlocked(Task task) {
        if(task.getFaction() != null && task.getFaction() != faction){
            return false;
        }
        for (TaskUnlocker taskUnlocker : task.getUnlocker()) {
            if(!taskUnlocker.isUnlocked(this.factionPlayer)){
                return false;
            }
        }
        return true;
    }

    /**
     * simply adds the given task to the completion list
     * or not if the task is not applicant to the {@link TaskManager#player}
     *
     * @return if the task got added
     */
    @Override
    public boolean addCompletedTask(@Nonnull Task task) {
        if (!isTaskUnlocked(task)) return false;
        this.getCompletedTasks(task.getVariant()).add(task);
        this.getAvailableTasks(task.getVariant()).remove(task);
        this.stats.remove(task);
        this.updateAvailableTasks();
        return true;
    }

    /**
     * remove the task's requirements from the player
     */
    public void removeRequirements(@Nonnull Task task) {
        task.getRequirement().removeRequirement(this.factionPlayer);
    }

    @Nonnull
    @Override
    public Set<Task> getCompletedTasks(@Nullable Task.Variant variant) {
        if(variant != null) {
            return this.completedTasks.computeIfAbsent(variant, variant1 -> Sets.newHashSet());
        }else {
            return this.completedTasks.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        }
    }

    @Nonnull
    @Override
    public Set<Task> getCompletedTasks() {
        return this.getCompletedTasks(null);
    }

    /**
     * applies the reward of the given task
     */
    public void applyRewards(Task task) {
        task.getReward().applyReward(this.factionPlayer);
    }

    @Override
    public boolean hasAvailableTasks(@Nullable Task.Variant variant) {
        if(variant != null) {
            return !this.availableTasks.isEmpty() && this.availableTasks.get(variant) != null && !this.availableTasks.get(variant).isEmpty();
        }else {
            return !this.availableTasks.isEmpty() && this.availableTasks.values().stream().anyMatch(set -> !set.isEmpty());
        }
    }

    @Nonnull
    @Override
    public Set<Task> getAvailableTasks(@Nullable Task.Variant variant) {
        if(variant != null) {
            return this.availableTasks.computeIfAbsent(variant, variant1 -> Sets.newHashSet());
        }else {
            return this.availableTasks.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        }
    }

    @Nonnull
    @Override
    public Set<Task> getAvailableTasks() {
        return this.getAvailableTasks(null);
    }

    @Override
    @Nonnull
    public Set<Task> getCompletableTasks() {
        return this.getCompletableTasks(null);
    }

    @Override
    @Nonnull
    public Set<Task> getCompletableTasks(@Nullable Task.Variant variant) {
        if(variant !=null) {
            return this.getAvailableTasks(variant).stream().filter(this::canCompleteTask).collect(Collectors.toSet());
        }else {
            return this.availableTasks.values().stream().flatMap(Collection::stream).filter(this::canCompleteTask).collect(Collectors.toSet());
        }
    }

    @Override
    public boolean canCompleteTask(@Nonnull Task task) {
        if (!isTaskUnlocked(task))
            return false;
        switch (task.getRequirement().getType()) {
            case STATS:
                if (this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) task.getRequirement().getStat(this.factionPlayer))) < this.stats.get(task) + task.getRequirement().getAmount(this.factionPlayer))
                    return false;
                break;
            case ENTITY:
                int actualStat = this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) task.getRequirement().getStat(this.factionPlayer)));
                int neededStat = this.stats.get(task) + task.getRequirement().getAmount(this.factionPlayer);
                if (actualStat < neededStat)
                    return false;
                break;
            case ENTITY_TAG:
                int actualStats = 0;
                //noinspection unchecked
                for(EntityType<?> type : ((Tag<EntityType<?>>) task.getRequirement().getStat()).getAllElements()) {
                    actualStats += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
                }
                int neededStats = this.stats.get(task) + task.getRequirement().getAmount(this.factionPlayer);
                if (actualStats < neededStats)
                    return false;
                break;
            case ITEMS:
                ItemStack stack = ((ItemRequirement) task.getRequirement()).getItemStack();
                if (this.player.inventory.count(stack.getItem()) < stack.getCount()) return false;
                break;
            case BOOLEAN:
                if (!(Boolean) task.getRequirement().getStat(this.factionPlayer)) return false;
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void reset() {
        this.completedTasks.clear();
        this.availableTasks.clear();
        this.stats.clear();
    }

    @Override
    public void init() {
        this.updateAvailableTasks();
    }

    /**
     * if there are less that 3 available tasks
     * re enables a completed task 1 time per day as available task
     */
    public void tick() {
        if(this.player.getEntityWorld().getGameTime() % 24000 == 0 && getAvailableTasks(Task.Variant.REPEATABLE).size() < 3) {
            Set<Task> completed = this.getCompletedTasks(Task.Variant.REPEATABLE);
            if(!completed.isEmpty()) {
                completed.stream().skip(new Random().nextInt(completed.size())).findFirst().ifPresent(task -> this.getAvailableTasks(Task.Variant.REPEATABLE).add(task));
            }
        }
    }

    /**
     * sets starting stat if a task has none
     */
    private void initStats() {
        for (Set<Task> tasks : this.availableTasks.values()) {
            for (Task task : tasks) {
                if (this.stats.containsKey(task)) continue;
                switch (task.getRequirement().getType()) {
                    case STATS:
                        this.stats.put(task, this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) task.getRequirement().getStat(this.factionPlayer))));
                        break;
                    case ENTITY:
                        this.stats.put(task, this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) task.getRequirement().getStat(this.factionPlayer))));
                        break;
                    case ENTITY_TAG:
                        int amount = 0;
                        //noinspection unchecked
                        for(EntityType<?> type : ((Tag<EntityType<?>>) task.getRequirement().getStat()).getAllElements()) {
                            amount += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
                        }
                        this.stats.put(task, amount);
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public boolean isTaskCompleted(@Nonnull Task task) {
        return this.getCompletedTasks(task.getVariant()).contains(task);
    }

    /**
     * adds new tasks to {@link #availableTasks} and initialize their stats
     */
    private void updateAvailableTasks() {
        Collection<Task> tasks = ModRegistries.TASKS.getValues();
        tasks.stream().filter(this::isTaskUnlocked).filter(task -> !isTaskCompleted(task)).forEach(task -> this.getAvailableTasks(task.getVariant()).add(task));
        this.initStats();
    }

    public void writeNBT(CompoundNBT compoundNBT) {
        //completed tasks
        if (!this.completedTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.completedTasks.forEach((variant, tasks) -> tasks.forEach(task -> tasksNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true)));
            compoundNBT.put("tasks", tasksNBT);
        }
        //stats
        if (!this.stats.isEmpty()) {
            CompoundNBT stats = new CompoundNBT();
            this.stats.forEach((task, statAmount) -> stats.putInt(Objects.requireNonNull(task.getRegistryName()).toString(), statAmount));
            compoundNBT.put("stats", stats);
        }
    }

    public void readNBT(CompoundNBT compoundNBT) {
        this.updateAvailableTasks();
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
                    this.stats.put(task, tasks.getInt(taskId));
                }
            });
        }
    }

}
