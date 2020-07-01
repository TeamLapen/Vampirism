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
        this.reset();
    }

    /**
     * completes task and informs the other Dist about the completion
     */
    @Override
    public void completeTask(@Nonnull Task task) {
        if (addCompletedTask(task)) {
            this.removeRequirements(task);
            this.applyRewards(task);
            this.updateClient();
        }
    }

    public void updateClient() {
        VampirismMod.dispatcher.sendTo(new TaskStatusPacket(getCompletableTasks(), getCompletedTasks(), getAvailableTasks(), player.openContainer.windowId), player);
    }

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

    @Override
    public void removeRequirements(@Nonnull Task task) {
        if (task.getRequirement().getType().equals(TaskRequirement.Type.ITEMS)) {
            this.player.inventory.clearMatchingItems(itemStack -> itemStack.getItem() == task.getRequirement().getStat(), task.getRequirement().getAmount());
        }
    }

    @Nonnull
    @Override
    public Set<Task> getCompletedTasks(Task.Variant variant) {
        return this.completedTasks.computeIfAbsent(variant, variant1 -> Sets.newHashSet());
    }

    @Nonnull
    @Override
    public Set<Task> getCompletedTasks() {
        return this.completedTasks.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Override
    public void applyRewards(Task task) {
        task.getReward().applyReward(this.factionPlayer);
    }

    @Override
    public boolean hasAvailableTasks(Task.Variant variant) {
        return !this.availableTasks.isEmpty() && this.availableTasks.get(variant) != null && !this.availableTasks.get(variant).isEmpty();
    }

    /**
     * returns {@link TaskManager#availableTasks} and initiate the Stats on first call
     */
    @Nonnull
    @Override
    public Set<Task> getAvailableTasks(Task.Variant variant) {
        return this.availableTasks.computeIfAbsent(variant, variant1 -> Sets.newHashSet());
    }

    @Nonnull
    @Override
    public Set<Task> getAvailableTasks() {
        return this.availableTasks.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    @Override
    @Nonnull
    public Set<Task> getCompletableTasks() {
        return this.availableTasks.values().stream().flatMap(Collection::stream).filter(this::canCompleteTask).collect(Collectors.toSet());
    }

    @Override
    @Nonnull
    public Set<Task> getCompletableTasks(Task.Variant variant) {
        return this.getAvailableTasks(variant).stream().filter(this::canCompleteTask).collect(Collectors.toSet());
    }

    @Override
    @Nonnull
    public IPlayableFaction<?> getFaction() {
        return faction;
    }

    @Override
    public boolean canCompleteTask(Task task) {
        if (!isTaskUnlocked(task))
            return false;
        switch (task.getRequirement().getType()) {
            case STATS:
                if (this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) task.getRequirement().getStat())) < this.stats.get(task) + task.getRequirement().getAmount())
                    return false;
                break;
            case ENTITY:
                int actualStat = this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) task.getRequirement().getStat()));
                int neededStat = this.stats.get(task) + task.getRequirement().getAmount();
                if (actualStat < neededStat)
                    return false;
                break;
            case ENTITY_TYPE:
                int actualStats = 0;
                //noinspection unchecked
                for(EntityType<?> type : ((Tag<EntityType<?>>) task.getRequirement().getStat()).getAllElements()) {
                    actualStats += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
                }
                int neededStats = this.stats.get(task) + task.getRequirement().getAmount();
                if (actualStats < neededStats)
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
        this.availableTasks.clear();
        this.stats.clear();
    }

    @Override
    public void init() {
        this.updateAvailableTasks();
    }

    public void update() {
        if(this.player.getEntityWorld().getGameTime() % 24000 == 0 && getAvailableTasks(Task.Variant.REPEATABLE).size() < 3) {
            Set<Task> completed = this.getCompletedTasks(Task.Variant.REPEATABLE);
            if(!completed.isEmpty()) {
                completed.stream().skip(new Random().nextInt(completed.size())).findFirst().ifPresent(task -> this.getAvailableTasks(Task.Variant.REPEATABLE).add(task));
            }
        }
    }

    /**
     * cannot be called in constructor, because player is not constructed yet
     */
    private void updateStats() {
        for (Set<Task> tasks : this.availableTasks.values()) {
            for (Task task : tasks) {
                if (this.stats.containsKey(task)) continue;
                switch (task.getRequirement().getType()) {
                    case STATS:
                        this.stats.put(task, this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) task.getRequirement().getStat())));
                        break;
                    case ENTITY:
                        this.stats.put(task, this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) task.getRequirement().getStat())));
                        break;
                    case ENTITY_TYPE:
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
    public boolean isTaskCompleted(Task task) {
        return this.getCompletedTasks(task.getVariant()).contains(task);
    }

    private void updateAvailableTasks() {
        Collection<Task> tasks = ModRegistries.TASKS.getValues();
        tasks.stream().filter(this::isTaskUnlocked).filter(task -> !isTaskCompleted(task)).forEach(task -> this.getAvailableTasks(task.getVariant()).add(task));
        this.updateStats();
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
            this.stats.forEach((task, statAmount) -> stats.putInt(task.getRegistryName().toString(), statAmount));
            compoundNBT.put("stats", stats);
        }
    }

    public <T> void readNBT(CompoundNBT compoundNBT) {
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
