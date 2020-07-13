package de.teamlapen.vampirism.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.config.VampirismConfig;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class TaskManager implements ITaskManager {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nonnull
    private final IPlayableFaction<?> faction;
    @Nonnull
    private final ServerPlayerEntity player;
    @Nonnull
    private final IFactionPlayer<?> factionPlayer;

    @Nonnull
    private final Set<Task> completedTasks = new HashSet<>();

    @Nonnull
    private final Map<Integer, Set<Task>> tasks = new HashMap<>();
    @Nonnull
    private final Map<Integer, Integer> lessTasks = new HashMap<>();
    @Nonnull
    private final Map<Integer, Set<Task>> acceptedTasks = new HashMap<>();

    @Nonnull
    private final Map<Integer, Map<Task, Map<ResourceLocation, Integer>>> stats = Maps.newHashMap();

    public TaskManager(IFactionPlayer<?> factionPlayer, @Nonnull IPlayableFaction<?> faction) {
        this.faction = faction;
        this.player = (ServerPlayerEntity) factionPlayer.getRepresentingPlayer();
        this.factionPlayer = factionPlayer;
    }

    @Override
    public boolean completeTask(int entityId, @Nonnull Task task) {
        int tmpId =task.isUnique()?-1:entityId;
        if (!isTaskUnlocked(task)) return false;
        if(task.getRequirement().isHasStatBasedReq()) {
            this.stats.get(tmpId).remove(task);
        }
        this.acceptedTasks.get(tmpId).remove(task);
        this.completedTasks.add(task);
        this.tasks.get(tmpId).remove(task);
        if (!task.isUnique()) {
            this.lessTasks.compute(entityId, (id, value) -> value == null ? 1 : value + 1);
        }
        this.removeRequirements(task);
        this.applyRewards(task);
        this.updateTaskMasterScreen(entityId);
        return true;
    }

    @Override
    public void acceptTask(int entityId, @Nonnull Task task) {
        this.acceptedTasks.compute(task.isUnique()?-1:entityId, (id, tasks) -> {
            if(tasks == null) {
                return Sets.newHashSet(task);
            }else {
                tasks.add(task);
                return tasks;
            }
        });
        this.updateTaskMasterScreen(entityId);
    }

    @Override
    public void abortTask(int entityId, @Nonnull Task task) {
        this.acceptedTasks.compute(task.isUnique()?-1:entityId, (id, tasks) -> {
            if(tasks != null) {
                tasks.remove(task);
                this.stats.computeIfPresent(entityId, (entityId1, tasks1) -> {
                    tasks1.remove(task);
                    return tasks1;
                });
            }
            return tasks;
        });
    }

    @Override
    public boolean hasAvailableTasks(int entityId) {
        return !(getTasks(entityId).isEmpty() && getUniqueTasks().isEmpty());
    }

    @Override
    public void openTaskMasterScreen(int entityId) {
        Set<Task> selectedTasks = new HashSet<>(getTasks(entityId));
        selectedTasks.addAll(getUniqueTasks());
        this.updateClient(entityId, getCompletedRequirements(entityId, selectedTasks), reduceToCompletableTasks(entityId, selectedTasks), reduceToNotAccepted(entityId,selectedTasks), selectedTasks);
    }

    private void updateTaskMasterScreen(int entityId) {
        Set<Task> selectedTasks = new HashSet<>(this.getTasks(entityId));
        selectedTasks.addAll(getUniqueTasks());
        this.updateClient(entityId, getCompletedRequirements(entityId, selectedTasks), reduceToCompletableTasks(entityId, selectedTasks), reduceToNotAccepted(entityId,selectedTasks), selectedTasks);
    }

    private Set<Task> getTasks(int entityId) {
        Set<Task> selectedTasks = this.tasks.getOrDefault(entityId, new HashSet<>());
        if(!selectedTasks.isEmpty()) {
            this.removeLockedTasks(entityId, selectedTasks);
        }
        int neededTaskAmount = VampirismConfig.BALANCE.taskMasterTaskAmount.get() - lessTasks.getOrDefault(entityId,0);
        if(selectedTasks.size() < neededTaskAmount) {
            List<Task> tasks = ModRegistries.TASKS.getValues().stream().filter(this::matchesFaction).filter(task -> !task.isUnique()).filter(this::isTaskUnlocked).collect(Collectors.toList());
            Collections.shuffle(tasks);
            selectedTasks.addAll(tasks.subList(0,neededTaskAmount-selectedTasks.size()));
        }
        this.tasks.put(entityId,selectedTasks);
        this.updateStats(entityId, selectedTasks);
        return selectedTasks;
    }

    private Set<Task> getUniqueTasks() {
        Set<Task> uniqueTasks = this.tasks.getOrDefault(-1, new HashSet<>());
        if(!uniqueTasks.isEmpty()) {
            this.removeLockedTasks(-1, uniqueTasks);
        }
        uniqueTasks.addAll(ModRegistries.TASKS.getValues().stream().filter(this::matchesFaction).filter(Task::isUnique).filter(task -> !this.completedTasks.contains(task)).filter(this::isTaskUnlocked).collect(Collectors.toSet()));
        this.tasks.put(-1,uniqueTasks);
        this.updateStats(-1, uniqueTasks);
        return uniqueTasks;
    }

    @Override
    public void updateClient(int entityId,  Map<Task,List<ResourceLocation>> requirements, Set<Task> completable,Set<Task> notAcceptedTasks, Set<Task> available) {
        if(player.openContainer instanceof TaskMasterContainer) {
            VampirismMod.dispatcher.sendTo(new TaskStatusPacket(completable, available, notAcceptedTasks, requirements, player.openContainer.windowId, entityId), player);
        }
    }

    /**
     * gets all completed task requirements of all tasks
     */
    @Nonnull
    public Map<Task, List<ResourceLocation>> getCompletedRequirements(int entityId, Set<Task> tasks) {
        Map<Task,List<ResourceLocation>> completedRequirements = Maps.newHashMap();
        tasks.forEach(task -> {
            List<ResourceLocation> completed = getCompletedRequirements(entityId, task);
            if(!completed.isEmpty()) {
                completedRequirements.put(task,completed);
            }
        });
        return completedRequirements;
    }

    private Set<Task> reduceToCompletableTasks(int entityId, Set<Task> tasks) {
        Set<Task> completable = tasks.stream().filter(task -> canCompleteTask(entityId, task)).collect(Collectors.toSet());
        tasks.removeAll(completable);
        return completable;
    }

    private Set<Task> reduceToNotAccepted(int entityId, Set<Task> tasks) {
        Set<Task> notAccepted = tasks.stream().filter(task -> !isTaskAccepted(entityId,task)).collect(Collectors.toSet());
        tasks.removeAll(notAccepted);
        return notAccepted;
    }

    /**
     * @param task the task that should be checked
     * @return whether the task is unlocked my the player or not
     */
    public boolean isTaskUnlocked(Task task) {
        if(!matchesFaction(task)) return false;
        for (TaskUnlocker taskUnlocker : task.getUnlocker()) {
            if (!taskUnlocker.isUnlocked(this.factionPlayer)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesFaction(Task task) {
        return task.getFaction() == this.faction || task.getFaction() == null;
    }

    /**
     * remove the task's requirements from the player
     */
    public void removeRequirements(@Nonnull Task task) {
        task.getRequirement().removeRequirement(this.factionPlayer);
    }

    /**
     * applies the reward of the given task
     */
    public void applyRewards(Task task) {
        task.getReward().applyReward(this.factionPlayer);
    }

    @Override
    public boolean canCompleteTask(int entityId, @Nonnull Task task) {
        if (!isTaskUnlocked(task))
            return false;
        for (TaskRequirement.Requirement<?> requirement : task.getRequirement().getAll()) {
            if (!checkStat(entityId, task, requirement)) {
                return false;
            }
        }
        return true;
    }

    /**
     * gets all task requirements of the specific task, that have been completed already
     *
     * @param task the task which requirements should be checked
     * @return all task requirements of the task that have been completed
     */
    private List<ResourceLocation> getCompletedRequirements(int entityId, Task task) {
        List<ResourceLocation> completed = Lists.newArrayList();
        for (TaskRequirement.Requirement<?> requirement : task.getRequirement().getAll()) {
            if (checkStat(entityId, task, requirement)) {
                completed.add(requirement.getId());
            }
        }
        return completed;
    }

    /**
     * checks if the requirement is completed
     *
     * @param task the task of the requirement
     * @param requirement the requirement to check
     * @return if the requirement is completed
     */
    private boolean checkStat(int entityId, Task task, TaskRequirement.Requirement<?> requirement) {
        if(task.isUnique()) {
            entityId = -1;
        }
        if(!isTaskAccepted(entityId,task))return false;
        try {
            switch (requirement.getType()) {
                case STATS:
                    int actualStat = this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) requirement.getStat(this.factionPlayer)));
                    int neededStat = this.stats.get(entityId).get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
                    if (actualStat < neededStat)
                        return false;
                    break;
                case ENTITY:
                    int actualStat1 = this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat(this.factionPlayer)));
                    int neededStat1 = this.stats.get(entityId).get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
                    if (actualStat1 < neededStat1)
                        return false;
                    break;
                case ENTITY_TAG:
                    int actualStats = 0;
                    //noinspection unchecked
                    for (EntityType<?> type : ((Tag<EntityType<?>>) requirement.getStat(this.factionPlayer)).getAllElements()) {
                        actualStats += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
                    }
                    int neededStats = this.stats.get(entityId).get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
                    if (actualStats < neededStats)
                        return false;
                    break;
                case ITEMS:
                    ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                    if (this.player.inventory.count(stack.getItem()) < stack.getCount()) return false;
                    break;
                case BOOLEAN:
                    if (!(Boolean) requirement.getStat(this.factionPlayer)) return false;
                    break;
                default:
                    return false;
            }
        } catch (NullPointerException exception) {
            LOGGER.warn("failed to find registered stat value. Recalculating.", exception);
            this.updateStats(entityId,task);
            return false;
        }
        return true;
    }

    @Override
    public void reset() {
        this.completedTasks.clear();
        this.stats.clear();
        this.tasks.clear();
        this.completedTasks.clear();
        this.acceptedTasks.clear();
        this.lessTasks.clear();
    }

    /**
     * if there are less that 3 available tasks
     * re enables a completed task 1 time per day as available task
     */
    public void tick() {
        if (this.player.getEntityWorld().getGameTime() % 24000 == 0) {
            this.updateTaskLists();
        }
    }

    @Override
    public void updateTaskLists() {
        for (Map.Entry<Integer, Set<Task>> entrySet : this.tasks.entrySet()) {
            if(entrySet.getKey() == -1)continue;
            Set<Task> accepted = this.acceptedTasks.get(entrySet.getKey());
            if(accepted == null || accepted.isEmpty()) {
                tasks.get(entrySet.getKey()).clear();
                continue;
            }
            entrySet.getValue().removeIf(task -> !accepted.contains(task));
        }
    }

    @Override
    public void resetTaskLists() {
        this.acceptedTasks.clear();
        this.lessTasks.clear();
        this.updateTaskLists();
    }

    @Override
    public boolean isUniqueTaskCompleted(@Nonnull Task task) {
        return this.completedTasks.contains(task);
    }

    private void removeLockedTasks(int entityId, Collection<Task> tasks) {
        tasks.removeIf(task -> {
            if(!this.isTaskUnlocked(task)) {
                this.stats.getOrDefault(entityId, Collections.emptyMap()).remove(task);
                return true;
            }
            return false;
        });
    }

    private boolean isTaskAccepted(int entityId, Task task) {
        return this.acceptedTasks.getOrDefault(task.isUnique()?-1:entityId, Collections.emptySet()).contains(task);
    }

    private void updateStats(int entityId, Collection<Task> tasks) {
        tasks.forEach(task -> updateStats(entityId, task));
    }

    private void updateStats(int entityId, Task task) {
        if(!isTaskAccepted(entityId,task))return;
        if(!task.getRequirement().isHasStatBasedReq()) return;
        Map<Task,Map<ResourceLocation,Integer>> taskStats = this.stats.getOrDefault(task.isUnique()?-1:entityId, new HashMap<>());
        Map<ResourceLocation,Integer> reqStats = taskStats.getOrDefault(task,new HashMap<>());
        for (TaskRequirement.Requirement<?> requirement : task.getRequirement().getAll()) {
            switch (requirement.getType()) {
                case STATS:
                    reqStats.putIfAbsent(requirement.getId(), this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) requirement.getStat(this.factionPlayer))));
                    break;
                case ENTITY:
                    reqStats.putIfAbsent(requirement.getId(), this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat(this.factionPlayer))));
                    break;
                case ENTITY_TAG:
                    int amount = 0;
                    //noinspection unchecked
                    for (EntityType<?> type : ((Tag<EntityType<?>>) requirement.getStat(this.factionPlayer)).getAllElements()) {
                        amount += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
                    }
                    reqStats.putIfAbsent(requirement.getId(), amount);
                    break;
                default:
            }
        }
        taskStats.put(task,reqStats);
        this.stats.put(task.isUnique()?-1:entityId,taskStats);
    }

    public void writeNBT(CompoundNBT compoundNBT) {
        //completed tasks
        if (!this.completedTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.completedTasks.forEach((task) -> tasksNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
            compoundNBT.put("completedTasks", tasksNBT);
        }
        //tasks
        if(!this.tasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.tasks.forEach((entityId, tasks) -> {
                CompoundNBT entityIdNBT = new CompoundNBT();
                tasks.forEach(task -> entityIdNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
                tasksNBT.put(entityId.toString(),entityIdNBT);
            });
            compoundNBT.put("tasks", tasksNBT);
        }
        //less tasks
        if(!this.lessTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.tasks.forEach((entityId, tasks) -> {
                CompoundNBT entityIdNBT = new CompoundNBT();
                tasks.forEach(task -> entityIdNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
                tasksNBT.put(entityId.toString(),entityIdNBT);
            });
            compoundNBT.put("lessTasks", tasksNBT);
        }
        //accepted tasks
        if(!this.acceptedTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.tasks.forEach((entityId, tasks) -> {
                CompoundNBT entityIdNBT = new CompoundNBT();
                tasks.forEach(task -> entityIdNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
                tasksNBT.put(entityId.toString(),entityIdNBT);
            });
            compoundNBT.put("acceptedTasks", tasksNBT);
        }
        //stats
        if (!this.stats.isEmpty()) {
            CompoundNBT stats = new CompoundNBT();
            this.stats.forEach((entity, tasks) -> {
                CompoundNBT tasksNBT = new CompoundNBT();
                tasks.forEach((task, requirements) -> {
                    CompoundNBT taskNBT = new CompoundNBT();
                    requirements.forEach((requirement, amount) -> {
                        taskNBT.putInt(requirement.toString(), amount);
                    });
                    taskNBT.put(Objects.requireNonNull(task.getRegistryName()).toString(),taskNBT);
                });
                stats.put(entity.toString(),tasksNBT);
            });
            compoundNBT.put("stats", stats);
        }
    }

    public void readNBT(CompoundNBT compoundNBT) {
        //completed tasks
        if (compoundNBT.contains("completedTasks")) {
            compoundNBT.getCompound("completedTasks").keySet().forEach(taskId -> {
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                if (task != null) {
                    this.completedTasks.add(task);
                }
            });
        }
        //tasks
        if (compoundNBT.contains("tasks")) {
            compoundNBT.getCompound("tasks").keySet().forEach(entityId -> {
                CompoundNBT entityIdNBT = compoundNBT.getCompound("tasks").getCompound(entityId);
                Set<Task> tasks = new HashSet<>();
                entityIdNBT.keySet().forEach((taskId-> {
                    Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                    if (task != null) {
                        tasks.add(task);
                    }
                }));
                this.tasks.put(Integer.valueOf(entityId), tasks);
            });
        }
        //less tasks
        if (compoundNBT.contains("lessTasks")) {
            compoundNBT.getCompound("lessTasks").keySet().forEach(entityId -> {
                CompoundNBT entityIdNBT = compoundNBT.getCompound("lessTasks").getCompound(entityId);
                Set<Task> tasks = new HashSet<>();
                entityIdNBT.keySet().forEach((taskId-> {
                    Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                    if (task != null) {
                        tasks.add(task);
                    }
                }));
                this.tasks.put(Integer.valueOf(entityId), tasks);
            });
        }
        //accepted tasks
        if (compoundNBT.contains("acceptedTasks")) {
            compoundNBT.getCompound("acceptedTasks").keySet().forEach(entityId -> {
                CompoundNBT entityIdNBT = compoundNBT.getCompound("acceptedTasks").getCompound(entityId);
                Set<Task> tasks = new HashSet<>();
                entityIdNBT.keySet().forEach((taskId-> {
                    Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                    if (task != null) {
                        tasks.add(task);
                    }
                }));
                this.tasks.put(Integer.valueOf(entityId), tasks);
            });
        }
        //stats
        if (compoundNBT.contains("stats")) {
            CompoundNBT stats = compoundNBT.getCompound("stats");
            stats.keySet().forEach(entityId -> {
                CompoundNBT entityIdNBT = stats.getCompound(entityId);
                Map<Task,Map<ResourceLocation,Integer>> tasks = new HashMap<>();
                entityIdNBT.keySet().forEach((task) -> {
                    CompoundNBT taskNBT = entityIdNBT.getCompound(task);
                    Map<ResourceLocation,Integer> requirements = new HashMap<>();
                    taskNBT.keySet().forEach((requirementId) -> {
                        requirements.put(new ResourceLocation(requirementId), taskNBT.getInt(requirementId));
                    });
                    tasks.put(ModRegistries.TASKS.getValue(new ResourceLocation(task)), requirements);
                });
                this.stats.put(Integer.valueOf(entityId), tasks);
            });
        }
    }

}
