package de.teamlapen.vampirism.player;

import com.google.common.collect.Maps;
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
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.network.TaskPacket;
import de.teamlapen.vampirism.network.TaskStatusPacket;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    private final Map<UUID, TaskWrapper> taskWrapperMap = new HashMap<>();

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
        this.completedTasks.add(task);
        TaskWrapper wrapper = this.taskWrapperMap.get(tmpId);
        wrapper.removeTask(task, true);
        if (!task.isUnique()) {
            ++wrapper.lessTasks;
        }
        this.removeRequirements(task);
        this.applyRewards(task);
        this.updateTaskMasterScreen(taskBoardId);
    }

    @Override
    public void acceptTask(UUID taskBoardId, @Nonnull Task task) {
        this.taskWrapperMap.computeIfAbsent(task.isUnique() ? UNIQUE_TASKS : taskBoardId, TaskWrapper::new).acceptTask(task, this.player.world.getGameTime() + VampirismConfig.BALANCE.taskDuration.get() * 1200);
        this.updateStats(taskBoardId, task);
        this.updateTaskMasterScreen(taskBoardId);
    }

    @Override
    public void abortTask(UUID taskBoardId, @Nonnull Task task, boolean remove) {
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(task.isUnique() ? UNIQUE_TASKS : taskBoardId, TaskWrapper::new);
        wrapper.removeTask(task, remove);
        this.updateTaskMasterScreen(taskBoardId);
    }

    @Override
    public void abortTask(UUID taskBoardId, @Nonnull Task task) {
        abortTask(taskBoardId, task, false);
    }

    @Override
    public boolean hasAvailableTasks(UUID taskBoardId) {
        return !(getTasks(taskBoardId).isEmpty() && getUniqueTasks().isEmpty());
    }

    @Override
    public void openTaskMasterScreen(UUID taskBoardId) {
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(taskBoardId, TaskWrapper::new);
        Set<Task> selectedTasks = new HashSet<>(getTasks(taskBoardId));
        selectedTasks.addAll(getUniqueTasks());
        this.updateClient(taskBoardId, getCompletedRequirements(taskBoardId, selectedTasks), reduceToCompletableTasks(taskBoardId, selectedTasks), reduceToNotAcceptedTasks(taskBoardId, selectedTasks), selectedTasks, this.taskWrapperMap.get(taskBoardId).taskTimeStamp);
        wrapper.lastSeenPos = this.player.getPosition();
    }

    @Override
    public void updateTaskMasterScreen(UUID taskBoardId) {
        Set<Task> selectedTasks = new HashSet<>(this.getTasks(taskBoardId));
        selectedTasks.addAll(getUniqueTasks());
        this.updateClient(taskBoardId, getCompletedRequirements(taskBoardId, selectedTasks), reduceToCompletableTasks(taskBoardId, selectedTasks), reduceToNotAcceptedTasks(taskBoardId, selectedTasks), selectedTasks, this.taskWrapperMap.get(taskBoardId).taskTimeStamp);
    }

    @Override
    public void openVampirismMenu() {
        if (player.openContainer instanceof TaskContainer) {
            VampirismMod.dispatcher.sendTo(new TaskPacket(player.openContainer.windowId, this.taskWrapperMap, this.taskWrapperMap.entrySet().stream().map(entry -> Pair.of(entry.getKey(), reduceToCompletableTasks(entry.getKey(), entry.getValue().getAcceptedTasks()))).collect(Collectors.toMap(Pair::getKey, Pair::getValue)), this.taskWrapperMap.values().stream().map(wrapper -> Pair.of(wrapper.id, getCompletedRequirements(wrapper.id, wrapper.tasks))).collect(Collectors.toMap(Pair::getKey, Pair::getValue))), player);
        }
    }

    /**
     * syncs all shown task for a specific task board to the client
     */
    private void updateClient(UUID taskBoardId, @Nonnull Map<Task, Map<ResourceLocation, Integer>> requirements, @Nonnull Set<Task> completable, @Nonnull Set<Task> notAcceptedTasks, @Nonnull Set<Task> available, Map<Task, Long> taskTimeStamp) {
        if (player.openContainer instanceof TaskBoardContainer) {
            VampirismMod.dispatcher.sendTo(new TaskStatusPacket(completable, available, notAcceptedTasks, requirements, player.openContainer.windowId, taskBoardId, taskTimeStamp), player);
        }
    }

    @Override
    public void resetUniqueTask(Task task) {
        if (!task.isUnique()) return;
        this.completedTasks.remove(task);
        TaskWrapper wrapper = this.taskWrapperMap.get(UNIQUE_TASKS);
        wrapper.removeTask(task, false);
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
        if (!isTaskUnlocked(task)) return false;
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(task.isUnique() ? UNIQUE_TASKS : taskBoardId, TaskWrapper::new);
        if (!wrapper.isTimeEnough(task, this.player.world.getGameTime())) return false;
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
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(task.isUnique() ? UNIQUE_TASKS : taskBoardId, TaskWrapper::new);
        return !wrapper.getAcceptedTasks().contains(task);
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
        this.taskWrapperMap.values().forEach(wrapper -> {
            wrapper.lessTasks = 0;
            wrapper.taskTimeStamp.clear();
            wrapper.tasks.clear();
            wrapper.stats.clear();
        });
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
        for (TaskWrapper value : this.taskWrapperMap.values()) {
            if (value.id == UNIQUE_TASKS) continue;
            if (value.getAcceptedTasks().isEmpty()) {
                value.tasks.clear();
                continue;
            }
            value.tasks.removeIf(task -> !value.getAcceptedTasks().contains(task));
        }
    }

    @Override
    public void resetTaskLists() {
        this.taskWrapperMap.values().forEach(wrapper -> {
            wrapper.getAcceptedTasks().clear();
            wrapper.lessTasks = 0;
            wrapper.taskAmount = -1;
        });
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
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(taskBoardId, TaskWrapper::new);
        if (!wrapper.tasks.isEmpty()) {
            this.removeLockedTasks(taskBoardId, wrapper.tasks);
        }
        wrapper.taskAmount = wrapper.taskAmount < 0 ? player.getRNG().nextInt(VampirismConfig.BALANCE.taskMasterMaxTaskAmount.get()) + 1 - wrapper.lessTasks : wrapper.taskAmount;
        if (wrapper.tasks.size() < wrapper.taskAmount) {
            List<Task> tasks = new ArrayList<>(ModRegistries.TASKS.getValues());
            Collections.shuffle(tasks);
            wrapper.tasks.addAll(tasks.stream().filter(this::matchesFaction).filter(task -> !task.isUnique()).filter(this::isTaskUnlocked).limit(wrapper.taskAmount - wrapper.tasks.size()).collect(Collectors.toList()));
        }
        this.updateStats(taskBoardId, wrapper.tasks);
        return wrapper.tasks;
    }

    /**
     * gets all visible unique tasks
     * <p>
     * locks task that are no longer unlocked
     *
     * @return all visible unique tasks
     */
    private Set<Task> getUniqueTasks() {
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(UNIQUE_TASKS, TaskWrapper::new);
        Set<Task> uniqueTasks = wrapper.tasks;
        if (!uniqueTasks.isEmpty()) {
            this.removeLockedTasks(UNIQUE_TASKS, uniqueTasks);
        }
        uniqueTasks.addAll(ModRegistries.TASKS.getValues().stream().filter(this::matchesFaction).filter(Task::isUnique).filter(task -> !this.completedTasks.contains(task)).filter(this::isTaskUnlocked).collect(Collectors.toSet()));
        wrapper.tasks.addAll(uniqueTasks);
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
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(taskBoardId, TaskWrapper::new);
        tasks.removeIf(task -> {
            if (!this.isTaskUnlocked(task)) {
                wrapper.stats.remove(task);
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
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(taskBoardId, TaskWrapper::new);
        if (isTaskNotAccepted(taskBoardId, task)) return 0;
        int neededStat = 0;
        int actualStat = 0;
        switch (requirement.getType()) {
            case STATS:
                actualStat = this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) requirement.getStat(this.factionPlayer)));
                neededStat = wrapper.stats.get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
                break;
            case ENTITY:
                actualStat = this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat(this.factionPlayer)));
                neededStat = wrapper.stats.get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
                break;
            case ENTITY_TAG:
                //noinspection unchecked
                for (EntityType<?> type : ((ITag.INamedTag<EntityType<?>>) requirement.getStat(this.factionPlayer)).getAllElements()) {
                    actualStat += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
                }
                neededStat = wrapper.stats.get(task).get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
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
        if (task.isUnique()) {
            taskBoardId = UNIQUE_TASKS;
        }
        if (isTaskNotAccepted(taskBoardId, task)) return;
        if (!task.getRequirement().isHasStatBasedReq()) return;
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(taskBoardId, TaskWrapper::new);
        Map<ResourceLocation, Integer> reqStats = wrapper.stats.getOrDefault(task, new HashMap<>());
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
                    for (EntityType<?> type : ((ITag.INamedTag<EntityType<?>>) requirement.getStat(this.factionPlayer)).getAllElements()) {
                        amount += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type));
                    }
                    reqStats.putIfAbsent(requirement.getId(), amount);
                    break;
                default:
            }
        }
        wrapper.stats.put(task, reqStats);
    }

    // save/load -------------------------------------------------------------------------------------------------------

    public void writeNBT(@Nonnull CompoundNBT compoundNBT) {
        //completed tasks
        if (!this.completedTasks.isEmpty()) {
            CompoundNBT tasksNBT = new CompoundNBT();
            this.completedTasks.forEach((task) -> tasksNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
            compoundNBT.put("completedTasks", tasksNBT);
        }


        if (!this.taskWrapperMap.isEmpty()) {
            ListNBT infos = new ListNBT();
            this.taskWrapperMap.forEach((a, b) -> infos.add(b.writeNBT(new CompoundNBT())));
            compoundNBT.put("taskWrapper", infos);
        }
    }

    public void readNBT(@Nonnull CompoundNBT compoundNBT) {
        if (compoundNBT.contains("taskWrapper")) {
            ListNBT infos = compoundNBT.getList("taskWrapper", 10);
            for (int i = 0; i < infos.size(); i++) {
                CompoundNBT nbt = infos.getCompound(i);
                TaskWrapper wrapper = TaskWrapper.readNBT(nbt);
                this.taskWrapperMap.put(wrapper.id, wrapper);
            }
        }
        //completed tasks
        if (compoundNBT.contains("completedTasks")) {
            compoundNBT.getCompound("completedTasks").keySet().forEach(taskId -> {
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                if (task != null) {
                    this.completedTasks.add(task);
                }
            });
        }

        //TODO 1.17 remove following lines

        //tasks
        if (compoundNBT.contains("tasks")) {
            compoundNBT.getCompound("tasks").keySet().forEach(taskBoardIdStr -> {
                TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(UUID.fromString(taskBoardIdStr), TaskWrapper::new);
                CompoundNBT entityIdNBT = compoundNBT.getCompound("tasks").getCompound(taskBoardIdStr);
                Set<Task> tasks = new HashSet<>();
                entityIdNBT.keySet().forEach((taskId -> {
                    Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                    if (task != null) {
                        tasks.add(task);
                    }
                }));
                wrapper.tasks.addAll(tasks);
            });
        }
        //less tasks
        if (compoundNBT.contains("lessTasks")) {
            CompoundNBT lessTasksNBT = compoundNBT.getCompound("lessTasks");
            lessTasksNBT.keySet().forEach(taskBoardId -> {
                TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(UUID.fromString(taskBoardId), TaskWrapper::new);
                wrapper.lessTasks = (lessTasksNBT.getInt(taskBoardId));
            });
        }
        //accepted tasks
        if (compoundNBT.contains("acceptedTasks")) {
            compoundNBT.getCompound("acceptedTasks").keySet().forEach(taskBoardId -> {
                TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(UUID.fromString(taskBoardId), TaskWrapper::new);
                CompoundNBT entityIdNBT = compoundNBT.getCompound("acceptedTasks").getCompound(taskBoardId);
                entityIdNBT.keySet().forEach((taskId -> {
                    Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                    if (task != null) {
                        wrapper.acceptTask(task, this.player.world.getGameTime() + VampirismConfig.BALANCE.taskDuration.get() * 1200 * 4);
                    }
                }));
            });
        }
        //stats
        if (compoundNBT.contains("stats")) {
            CompoundNBT stats = compoundNBT.getCompound("stats");
            for (String taskBoardId : stats.keySet()) {
                TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(UUID.fromString(taskBoardId), TaskWrapper::new);
                CompoundNBT taskBoardNBT = stats.getCompound(taskBoardId);
                for (String taskRegistryName : taskBoardNBT.keySet()) {
                    CompoundNBT taskNBT = taskBoardNBT.getCompound(taskRegistryName);
                    Map<ResourceLocation, Integer> requirements = new HashMap<>();
                    for (String requirementString : taskNBT.keySet()) {
                        requirements.put(new ResourceLocation(requirementString), taskNBT.getInt(requirementString));
                    }
                    wrapper.stats.put(ModRegistries.TASKS.getValue(new ResourceLocation(taskRegistryName)), requirements);
                }
            }
        }
    }

    public static class TaskWrapper {

        private final UUID id;
        private int lessTasks;
        private int taskAmount;
        @Nullable
        private BlockPos lastSeenPos;
        @Nonnull
        private final Set<Task> tasks;
        @Nonnull
        private final Map<Task, Long> taskTimeStamp;
        @Nonnull
        private final Map<Task, Map<ResourceLocation, Integer>> stats;

        public TaskWrapper(UUID id) {
            this.id = id;
            this.lessTasks = 0;
            this.taskAmount = -1;
            this.tasks = new HashSet<>();
            this.stats = new HashMap<>();
            this.lastSeenPos = null;
            this.taskTimeStamp = new HashMap<>();
        }

        public TaskWrapper(UUID id, int lessTasks, int taskAmount, @Nonnull Set<Task> tasks, @Nonnull Map<Task, Long> acceptedTasks, @Nonnull Map<Task, Map<ResourceLocation, Integer>> stats, @Nullable BlockPos lastSeenPos) {
            this.id = id;
            this.lessTasks = lessTasks;
            this.taskAmount = taskAmount;
            this.tasks = tasks;
            this.stats = stats;
            this.lastSeenPos = lastSeenPos;
            this.taskTimeStamp = acceptedTasks;
        }

        public UUID getId() {
            return id;
        }

        @Nonnull
        public Optional<BlockPos> getLastSeenPos() {
            return Optional.ofNullable(lastSeenPos);
        }

        /**
         * This returns a {@link Map#keySet()}, which means that adding elements is not supported.
         */
        @Nonnull
        public Set<Task> getAcceptedTasks() {
            return this.taskTimeStamp.keySet();
        }

        public void acceptTask(Task task, long timeStamp) {
            this.taskTimeStamp.put(task, timeStamp);
        }

        public void removeTask(Task task, boolean delete) {
            if (delete) {
                this.tasks.remove(task);
            }
            this.taskTimeStamp.remove(task);
            this.stats.remove(task);
        }

        @Nonnull
        public Set<Task> getTasks() {
            return tasks;
        }

        public boolean isTimeEnough(Task task, long gameTime) {
            if (this.id != UNIQUE_TASKS) {
                if (this.taskTimeStamp.containsKey(task)) {
                    return this.taskTimeStamp.get(task) + VampirismConfig.BALANCE.taskDuration.get() >= gameTime;
                }
            }
            return true;
        }

        public long getTaskTimeStamp(Task task) {
            return taskTimeStamp.getOrDefault(task, 0L);
        }

        public CompoundNBT writeNBT(@Nonnull CompoundNBT nbt) {
            nbt.putUniqueId("id", this.id);
            nbt.putInt("lessTasks", this.lessTasks);
            nbt.putInt("taskAmount", this.taskAmount);

            ListNBT tasks = new ListNBT();
            this.tasks.forEach(task -> tasks.add(StringNBT.valueOf(task.getRegistryName().toString())));
            nbt.put("tasks", tasks);

            ListNBT acceptedTasks = new ListNBT();
            this.taskTimeStamp.forEach((task, time) -> {
                CompoundNBT taskNBT = new CompoundNBT();
                taskNBT.putString("task", task.getRegistryName().toString());
                taskNBT.putLong("time", time);
            });
            nbt.put("acceptedTasks", acceptedTasks);

            ListNBT stats = new ListNBT();
            this.stats.forEach((task, statEntry) -> {
                CompoundNBT entry = new CompoundNBT();
                entry.putString("task", task.getRegistryName().toString());
                ListNBT values = new ListNBT();
                statEntry.forEach((stat, value) -> {
                    CompoundNBT statNbt = new CompoundNBT();
                    statNbt.putString("stat", stat.toString());
                    statNbt.putInt("value", value);
                    values.add(statNbt);
                });
                entry.put("statEntry", values);
                stats.add(entry);
            });
            nbt.put("stats", stats);

            BlockPos pos = lastSeenPos;
            nbt.put("pos", Helper.newDoubleNBTList(pos.getX(), pos.getY(), pos.getZ()));

            return nbt;
        }

        public static TaskWrapper readNBT(@Nonnull CompoundNBT nbt) {
            UUID id = nbt.getUniqueId("id");
            int lessTasks = nbt.getInt("lessTasks");
            int taskAmount = nbt.getInt("taskAmount");
            Set<Task> tasks = new HashSet<>();
            Map<Task, Long> acceptedTasks = new HashMap<>();
            Map<Task, Map<ResourceLocation, Integer>> stats = new HashMap<>();
            BlockPos taskBoardInfo = null;
            if (nbt.contains("pos")) {
                ListNBT pos = nbt.getList("pos", 6);
                taskBoardInfo = new BlockPos(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
            }

            ListNBT tasksNBT = nbt.getList("tasks", 8);
            for (int i = 0; i < tasksNBT.size(); i++) {
                tasks.add(ModRegistries.TASKS.getValue(new ResourceLocation(tasksNBT.getString(i))));
            }

            ListNBT acceptedTasksNBT = nbt.getList("acceptedTasks", 10);
            for (int i = 0; i < acceptedTasksNBT.size(); i++) {
                CompoundNBT tasknbt = acceptedTasksNBT.getCompound(i);
                acceptedTasks.put(ModRegistries.TASKS.getValue(new ResourceLocation(acceptedTasksNBT.getString(i))), tasknbt.getLong("time"));
            }

            ListNBT statsNBT = nbt.getList("stats", 10);
            for (int i = 0; i < statsNBT.size(); i++) {
                CompoundNBT entry = statsNBT.getCompound(i);
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(entry.getString("task")));
                ListNBT statEntry = entry.getList("statEntry", 10);
                Map<ResourceLocation, Integer> taskStats = new HashMap<>();
                for (int i1 = 0; i1 < statEntry.size(); i1++) {
                    CompoundNBT statNBT = statEntry.getCompound(i1);
                    ResourceLocation stat = new ResourceLocation(statNBT.getString("stat"));
                    int value = statNBT.getInt("value");
                    taskStats.put(stat, value);
                }
                stats.put(task, taskStats);
            }
            return new TaskWrapper(id, lessTasks, taskAmount, tasks, acceptedTasks, stats, taskBoardInfo);
        }

        public void encode(PacketBuffer buffer) {
            buffer.writeUniqueId(this.id);
            buffer.writeVarInt(this.lessTasks);
            buffer.writeVarInt(this.taskAmount);
            buffer.writeBoolean(this.lastSeenPos != null);
            if (this.lastSeenPos != null) {
                buffer.writeBlockPos(this.lastSeenPos);
            }
            buffer.writeVarInt(this.tasks.size());
            buffer.writeVarInt(this.taskTimeStamp.size());
            buffer.writeVarInt(this.stats.size());
            this.tasks.forEach(task -> buffer.writeResourceLocation(task.getRegistryName()));
            this.taskTimeStamp.forEach((task, time) -> buffer.writeResourceLocation(task.getRegistryName()).writeVarLong(time));
            this.stats.forEach((task, stats) -> {
                buffer.writeResourceLocation(task.getRegistryName());
                buffer.writeVarInt(stats.size());
                stats.forEach((stat, value) -> {
                    buffer.writeResourceLocation(stat);
                    buffer.writeVarInt(value);
                });
            });
        }

        public static TaskWrapper decode(PacketBuffer buffer) {
            UUID id = buffer.readUniqueId();
            int lessTasks = buffer.readVarInt();
            int taskAmount = buffer.readVarInt();
            BlockPos pos = null;
            if (buffer.readBoolean()) {
                pos = buffer.readBlockPos();
            }
            int tasksSize = buffer.readVarInt();
            int acceptedTasksSize = buffer.readVarInt();
            int statsSize = buffer.readVarInt();
            Set<Task> tasks = new HashSet<>();
            for (int i = 0; i < tasksSize; i++) {
                tasks.add(ModRegistries.TASKS.getValue(buffer.readResourceLocation()));
            }
            Map<Task, Long> acceptedTasks = new HashMap<>();
            for (int i = 0; i < acceptedTasksSize; i++) {
                acceptedTasks.put(ModRegistries.TASKS.getValue(buffer.readResourceLocation()), buffer.readVarLong());
            }
            Map<Task, Map<ResourceLocation, Integer>> stats = new HashMap<>();
            for (int i = 0; i < statsSize; i++) {
                Task task = ModRegistries.TASKS.getValue(buffer.readResourceLocation());
                Map<ResourceLocation, Integer> taskStats = new HashMap<>();
                int taskStatSize = buffer.readVarInt();
                for (int i1 = 0; i1 < taskStatSize; i1++) {
                    taskStats.put(buffer.readResourceLocation(), buffer.readVarInt());
                }
                stats.put(task, taskStats);
            }
            return new TaskWrapper(id, lessTasks, taskAmount, tasks, acceptedTasks, stats, pos);
        }
    }

}
