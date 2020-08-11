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
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
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
    @Nonnull
    private static final Logger LOGGER = LogManager.getLogger();
    @Nonnull
    private static final UUID UNIQUE_TASKS = UUID.fromString("e2c6068a-8f0e-4d5b-822a-38ad6ecf98c9");
    @Nonnull
    private final IPlayableFaction<?> faction;
    @Nonnull
    private final ServerPlayerEntity player;

    @Nonnull
    private final IFactionPlayer<?> factionPlayer;

    @Nonnull
    private final Set<Task> completedTasks = new HashSet<>();
    @Nonnull
    private final Map<UUID, Set<Task>> tasks = new HashMap<>();
    @Nonnull
    private final Map<UUID, Integer> lessTasks = new HashMap<>();
    @Nonnull
    private final Map<UUID, Integer> taskAmount = new HashMap<>();

    @Nonnull
    private final Map<UUID, Set<Task>> acceptedTasks = new HashMap<>();

    @Nonnull
    private final Map<UUID, Map<Task, Map<ResourceLocation, Integer>>> stats = Maps.newHashMap();

    public TaskManager(@Nonnull IFactionPlayer<?> factionPlayer, @Nonnull IPlayableFaction<?> faction) {
        this.faction = faction;
        this.player = (ServerPlayerEntity) factionPlayer.getRepresentingPlayer();
        this.factionPlayer = factionPlayer;
    }

    // interface -------------------------------------------------------------------------------------------------------

    @Override
    public void completeTask(UUID taskBoardId, @Nonnull Task task) {
        UUID tmpId = task.isUnique() ? UNIQUE_TASKS : taskBoardId;
        if (!canCompleteTask(taskBoardId, task)) return;
        if (task.getRequirement().isHasStatBasedReq()) {
            this.stats.get(tmpId).remove(task);
        }
        this.acceptedTasks.get(tmpId).remove(task);
        this.completedTasks.add(task);
        this.tasks.get(tmpId).remove(task);
        if (!task.isUnique()) {
            this.lessTasks.compute(taskBoardId, (id, value) -> value == null ? 1 : value + 1);
        }
        this.removeRequirements(task);
        this.applyRewards(task);
        this.updateTaskMasterScreen(taskBoardId);
    }

    @Override
    public void acceptTask(UUID taskBoardId, @Nonnull Task task) {
        this.acceptedTasks.compute(task.isUnique() ? UNIQUE_TASKS : taskBoardId, (id, tasks) -> {
            if (tasks == null) {
                return Sets.newHashSet(task);
            } else {
                tasks.add(task);
                return tasks;
            }
        });
        this.updateTaskMasterScreen(taskBoardId);
    }

    @Override
    public void abortTask(UUID taskBoardId, @Nonnull Task task) {
        this.acceptedTasks.compute(task.isUnique() ? UNIQUE_TASKS : taskBoardId, (id, tasks) -> {
            if (tasks != null) {
                tasks.remove(task);
                this.stats.computeIfPresent(task.isUnique() ? UNIQUE_TASKS : taskBoardId, (entityId1, tasks1) -> {
                    tasks1.remove(task);
                    return tasks1;
                });
            }
            return tasks;
        });
        this.updateTaskMasterScreen(taskBoardId);
    }

    @Override
    public void openTaskMasterScreen(UUID taskBoardId) {
        Set<Task> selectedTasks = new HashSet<>(getTasks(taskBoardId));
        selectedTasks.addAll(getUniqueTasks());
        this.updateClient(taskBoardId, getCompletedRequirements(taskBoardId, selectedTasks), reduceToCompletableTasks(taskBoardId, selectedTasks), reduceToNotAcceptedTasks(taskBoardId, selectedTasks), selectedTasks);
    }

    @Override
    public void updateTaskMasterScreen(UUID taskBoardId) {
        Set<Task> selectedTasks = new HashSet<>(this.getTasks(taskBoardId));
        selectedTasks.addAll(getUniqueTasks());
        this.updateClient(taskBoardId, getCompletedRequirements(taskBoardId, selectedTasks), reduceToCompletableTasks(taskBoardId, selectedTasks), reduceToNotAcceptedTasks(taskBoardId, selectedTasks), selectedTasks);
    }

    @Override
    public boolean hasAvailableTasks(UUID taskBoardId) {
        return !(getTasks(taskBoardId).isEmpty() && getUniqueTasks().isEmpty());
    }

    /**
     * syncs all shown task for a specific task board to the client
     */
    private void updateClient(UUID taskBoardId, @Nonnull Map<Task, Map<ResourceLocation, Integer>> requirements, @Nonnull Set<Task> completable, @Nonnull Set<Task> notAcceptedTasks, @Nonnull Set<Task> available) {
        if (player.openContainer instanceof TaskBoardContainer) {
            VampirismMod.dispatcher.sendTo(new TaskStatusPacket(completable, available, notAcceptedTasks, requirements, player.openContainer.windowId, taskBoardId), player);
        }
    }

    // task filter -----------------------------------------------------------------------------------------------------

    /**
     * @param task the task that should be checked
     * @return whether the task's faction is applicant to the taskManager's {@link #faction}
     */
    private boolean matchesFaction(@Nonnull Task task) {
        return task.getFaction() == this.faction || task.getFaction() == null;
    }

    /**
     * @param task the task that should be checked
     * @return whether the task is unlocked my the player or not
     */
    public boolean isTaskUnlocked(@Nonnull Task task) {
        if (!matchesFaction(task)) return false;
        for (TaskUnlocker taskUnlocker : task.getUnlocker()) {
            if (!taskUnlocker.isUnlocked(this.factionPlayer)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param task the task that should be checked
     * @return whether the task can be completed or not
     */
    public boolean canCompleteTask(UUID taskBoardId, @Nonnull Task task) {
        if (!isTaskUnlocked(task))
            return false;
        for (TaskRequirement.Requirement<?> requirement : task.getRequirement().getAll()) {
            if (!checkStat(taskBoardId, task, requirement)) {
                return false;
            }
        }
        return true;
    }

    /**
     * checks if the task has been accepted by the player at the given task board or not
     *
     * @param taskBoardId the id of the task board
     * @param task        the task that should be checked
     * @return whether the task is accepted or not
     */
    private boolean isTaskNotAccepted(UUID taskBoardId, @Nonnull Task task) {
        return !this.acceptedTasks.getOrDefault(task.isUnique() ? UNIQUE_TASKS : taskBoardId, Collections.emptySet()).contains(task);
    }

    @Override
    public boolean isTaskCompleted(@Nonnull Task task) {
        return this.completedTasks.contains(task);
    }

    // task actions ----------------------------------------------------------------------------------------------------

    /**
     * remove the task's requirements from the player
     */
    public void removeRequirements(@Nonnull Task task) {
        task.getRequirement().removeRequirement(this.factionPlayer);
    }

    /**
     * applies the reward of the given task
     */
    public void applyRewards(@Nonnull Task task) {
        task.getReward().applyReward(this.factionPlayer);
    }

    // general ---------------------------------------------------------------------------------------------------------

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
     * updates the task list once per day ({@link #updateTaskLists()}
     */
    public void tick() {
        if (this.player.getEntityWorld().getGameTime() % 24000 == 0) {
            this.updateTaskLists();
        }
    }

    @Override
    public void updateTaskLists() {
        for (Map.Entry<UUID, Set<Task>> entrySet : this.tasks.entrySet()) {
            if (entrySet.getKey() == UNIQUE_TASKS) continue;
            Set<Task> accepted = this.acceptedTasks.get(entrySet.getKey());
            if (accepted == null || accepted.isEmpty()) {
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
        this.taskAmount.clear();
        this.updateTaskLists();
    }

    // task methods ----------------------------------------------------------------------------------------------------

    /**
     * gets all visible tasks for a task board
     * <p>
     * locks task that are no longer unlocked
     * if there are to less tasks already chosen, add new task
     *
     * @param taskBoardId the id of the task board
     * @return all visible tasks for the task board
     */
    private Set<Task> getTasks(UUID taskBoardId) {
        Set<Task> selectedTasks = this.tasks.getOrDefault(taskBoardId, new HashSet<>());
        if (!selectedTasks.isEmpty()) {
            this.removeLockedTasks(taskBoardId, selectedTasks);
        }
        int neededTaskAmount = this.taskAmount.getOrDefault(taskBoardId, player.getRNG().nextInt(VampirismConfig.BALANCE.taskMasterMaxTaskAmount.get()) + 1) - lessTasks.getOrDefault(taskBoardId, 0);
        if (selectedTasks.size() < neededTaskAmount) {
            List<Task> tasks = new ArrayList<>(ModRegistries.TASKS.getValues());
            Collections.shuffle(tasks);
            selectedTasks.addAll(tasks.stream().filter(this::matchesFaction).filter(task -> !task.isUnique()).filter(this::isTaskUnlocked).limit(neededTaskAmount - selectedTasks.size()).collect(Collectors.toList()));
        }
        this.tasks.put(taskBoardId, selectedTasks);
        this.updateStats(taskBoardId, selectedTasks);
        return selectedTasks;
    }

    /**
     * gets all visible unique tasks
     * <p>
     * locks task that are no longer unlocked
     *
     * @return all visible unique tasks
     */
    private Set<Task> getUniqueTasks() {
        Set<Task> uniqueTasks = this.tasks.getOrDefault(UNIQUE_TASKS, new HashSet<>());
        if (!uniqueTasks.isEmpty()) {
            this.removeLockedTasks(UNIQUE_TASKS, uniqueTasks);
        }
        uniqueTasks.addAll(ModRegistries.TASKS.getValues().stream().filter(this::matchesFaction).filter(Task::isUnique).filter(task -> !this.completedTasks.contains(task)).filter(this::isTaskUnlocked).collect(Collectors.toSet()));
        this.tasks.put(UNIQUE_TASKS, uniqueTasks);
        this.updateStats(UNIQUE_TASKS, uniqueTasks);
        return uniqueTasks;
    }

    /**
     * removes all completable tasks from the given task set and returns the removed tasks
     *
     * @param taskBoardId the id of the task board
     * @param tasks       the tasks to be filtered
     * @return all completable tasks from the given task set
     */
    private Set<Task> reduceToCompletableTasks(UUID taskBoardId, @Nonnull Set<Task> tasks) {
        Set<Task> completable = tasks.stream().filter(task -> canCompleteTask(taskBoardId, task)).collect(Collectors.toSet());
        tasks.removeAll(completable);
        return completable;
    }

    /**
     * removes all not accepted tasks from the given task set and returns the not accepted tasks
     *
     * @param taskBoardId the id of the task board
     * @param tasks       the tasks to be filtered
     * @return all not accepted tasks from the given task set
     */
    private Set<Task> reduceToNotAcceptedTasks(UUID taskBoardId, @Nonnull Set<Task> tasks) {
        Set<Task> notAccepted = tasks.stream().filter(task -> isTaskNotAccepted(taskBoardId, task)).collect(Collectors.toSet());
        tasks.removeAll(notAccepted);
        return notAccepted;
    }

    /**
     * returns all completed task requirements for the given tasks for the specific task board
     *
     * @param taskBoardId the id of the task board
     * @param tasks       the task for which the requirements are needed
     * @return map of completed requirement per task
     */
    @Nonnull
    public Map<Task, Map<ResourceLocation, Integer>> getCompletedRequirements(UUID taskBoardId, @Nonnull Set<Task> tasks) {
        Map<Task, Map<ResourceLocation, Integer>> completedRequirements = Maps.newHashMap();
        tasks.forEach(task -> {
            Map<ResourceLocation, Integer> completed = getCompletedRequirements(taskBoardId, task);
            if (!completed.isEmpty()) {
                completedRequirements.put(task, completed);
            }
        });
        return completedRequirements;
    }

    /**
     * returns all completed task requirements for the given task for the specific task board
     *
     * @param taskBoardId the id of the task board
     * @param task        the task to be checked
     * @return a list of all task requirements
     */
    private Map<ResourceLocation, Integer> getCompletedRequirements(UUID taskBoardId, @Nonnull Task task) {
        Map<ResourceLocation, Integer> completed = Maps.newHashMap();
        for (TaskRequirement.Requirement<?> requirement : task.getRequirement().getAll()) {
            completed.put(requirement.getId(), getStat(taskBoardId, task, requirement));
        }
        return completed;
    }

    /**
     * removes all no longer unlocked task from the specific task board
     *
     * @param taskBoardId the id of the task board
     * @param tasks       the task to be checked
     */
    private void removeLockedTasks(UUID taskBoardId, @Nonnull Collection<Task> tasks) {
        tasks.removeIf(task -> {
            if (!this.isTaskUnlocked(task)) {
                this.stats.getOrDefault(taskBoardId, Collections.emptyMap()).remove(task);
                return true;
            }
            return false;
        });
    }

    /**
     * checks if the requirement is completed
     *
     * @param taskBoardId the id of the task board
     * @param task        the task of the requirement
     * @param requirement the requirement to check
     * @return if the requirement is completed
     */
    private boolean checkStat(UUID taskBoardId, @Nonnull Task task, @Nonnull TaskRequirement.Requirement<?> requirement) {
        return getStat(taskBoardId, task, requirement) >= requirement.getAmount(this.factionPlayer);
    }

    private int getStat(UUID taskBoardId, @Nonnull Task task, @Nonnull TaskRequirement.Requirement<?> requirement) {
        if (task.isUnique()) {
            taskBoardId = UNIQUE_TASKS;
        }
        if (isTaskNotAccepted(taskBoardId, task)) return 0;
        int neededStat = 0;
        int actualStat = 0;
        switch (requirement.getType()) {
            case STATS:
                actualStat = this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) requirement.getStat(this.factionPlayer)));
                neededStat = this.stats.get(taskBoardId).get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
                break;
            case ENTITY:
                actualStat = this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat(this.factionPlayer)));
                neededStat = this.stats.get(taskBoardId).get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
                break;
            case ENTITY_TAG:
                //noinspection unchecked
                for (EntityType<?> type : ((Tag<EntityType<?>>) requirement.getStat(this.factionPlayer)).getAllElements()) {
                    actualStat += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
                }
                neededStat = this.stats.get(taskBoardId).get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
                break;
            case ITEMS:
                ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                neededStat = stack.getCount();
                actualStat = this.player.inventory.count(stack.getItem());
                break;
            case BOOLEAN:
                if (!(Boolean) requirement.getStat(this.factionPlayer)) return 0;
                return 1;
        }
        return Math.min(requirement.getAmount(this.factionPlayer) - (neededStat - actualStat), requirement.getAmount(this.factionPlayer));
    }

    /**
     * updated the saved stat target for the tasks of the task board
     *
     * @param taskBoardId the id of the task board
     * @param tasks       the tasks to be updated
     */
    private void updateStats(UUID taskBoardId, @Nonnull Collection<Task> tasks) {
        tasks.forEach(task -> updateStats(taskBoardId, task));
    }

    /**
     * updated the saved stat target for the task of the task board
     *
     * @param taskBoardId the id of the task board
     * @param task        the task to be updated
     */
    private void updateStats(UUID taskBoardId, @Nonnull Task task) {
        if (isTaskNotAccepted(taskBoardId, task)) return;
        if (!task.getRequirement().isHasStatBasedReq()) return;
        Map<Task, Map<ResourceLocation, Integer>> taskStats = this.stats.getOrDefault(task.isUnique() ? UNIQUE_TASKS : taskBoardId, new HashMap<>());
        Map<ResourceLocation, Integer> reqStats = taskStats.getOrDefault(task, new HashMap<>());
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
        taskStats.put(task, reqStats);
        this.stats.put(task.isUnique() ? UNIQUE_TASKS : taskBoardId, taskStats);
    }

    // save/load -------------------------------------------------------------------------------------------------------

    public void writeNBT(@Nonnull CompoundNBT compoundNBT) {
        //completed tasks
        if (!this.completedTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.completedTasks.forEach((task) -> tasksNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
            compoundNBT.put("completedTasks", tasksNBT);
        }
        //tasks
        if (!this.tasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.tasks.forEach((entityId, tasks) -> {
                CompoundNBT entityIdNBT = new CompoundNBT();
                tasks.forEach(task -> entityIdNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
                tasksNBT.put(entityId.toString(), entityIdNBT);
            });
            compoundNBT.put("tasks", tasksNBT);
        }
        //less tasks
        if (!this.lessTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.lessTasks.forEach((entityId, tasks) -> tasksNBT.putInt(entityId.toString(), tasks));
            compoundNBT.put("lessTasks", tasksNBT);
        }
        //accepted tasks
        if (!this.acceptedTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.acceptedTasks.forEach((entityId, tasks) -> {
                CompoundNBT entityIdNBT = new CompoundNBT();
                tasks.forEach(task -> entityIdNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
                tasksNBT.put(entityId.toString(), entityIdNBT);
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
                    requirements.forEach((requirement, amount) -> taskNBT.putInt(requirement.toString(), amount));
                    taskNBT.put(Objects.requireNonNull(task.getRegistryName()).toString(), taskNBT);
                });
                stats.put(entity.toString(), tasksNBT);
            });
            compoundNBT.put("stats", stats);
        }
    }

    public void readNBT(@Nonnull CompoundNBT compoundNBT) {
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
            compoundNBT.getCompound("tasks").keySet().forEach(taskBoardId -> {
                CompoundNBT entityIdNBT = compoundNBT.getCompound("tasks").getCompound(taskBoardId);
                Set<Task> tasks = new HashSet<>();
                entityIdNBT.keySet().forEach((taskId -> {
                    Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                    if (task != null) {
                        tasks.add(task);
                    }
                }));
                this.tasks.put(UUID.fromString(taskBoardId), tasks);
            });
        }
        //less tasks
        if (compoundNBT.contains("lessTasks")) {
            CompoundNBT lessTasksNBT = compoundNBT.getCompound("lessTasks");
            lessTasksNBT.keySet().forEach(taskBoardId -> this.lessTasks.put(UUID.fromString(taskBoardId), lessTasksNBT.getInt(taskBoardId)));
        }
        //accepted tasks
        if (compoundNBT.contains("acceptedTasks")) {
            compoundNBT.getCompound("acceptedTasks").keySet().forEach(taskBoardId -> {
                CompoundNBT entityIdNBT = compoundNBT.getCompound("acceptedTasks").getCompound(taskBoardId);
                Set<Task> tasks = new HashSet<>();
                entityIdNBT.keySet().forEach((taskId -> {
                    Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                    if (task != null) {
                        tasks.add(task);
                    }
                }));
                this.acceptedTasks.put(UUID.fromString(taskBoardId), tasks);
            });
        }
        //stats
        if (compoundNBT.contains("stats")) {
            CompoundNBT stats = compoundNBT.getCompound("stats");
            stats.keySet().forEach(taskBoardId -> {
                CompoundNBT entityIdNBT = stats.getCompound(taskBoardId);
                Map<Task, Map<ResourceLocation, Integer>> tasks = new HashMap<>();
                entityIdNBT.keySet().forEach((task) -> {
                    CompoundNBT taskNBT = entityIdNBT.getCompound(task);
                    Map<ResourceLocation, Integer> requirements = new HashMap<>();
                    taskNBT.keySet().forEach((requirementId) -> requirements.put(new ResourceLocation(requirementId), taskNBT.getInt(requirementId)));
                    tasks.put(ModRegistries.TASKS.getValue(new ResourceLocation(task)), requirements);
                });
                this.stats.put(UUID.fromString(taskBoardId), tasks);
            });
        }
    }

}
