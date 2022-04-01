package de.teamlapen.vampirism.player;

import com.google.common.collect.Maps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.network.TaskPacket;
import de.teamlapen.vampirism.network.TaskStatusPacket;
import de.teamlapen.vampirism.player.tasks.TaskInstance;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.player.tasks.reward.ItemRewardInstance;
import de.teamlapen.vampirism.player.tasks.reward.LordLevelReward;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskManager implements ITaskManager {
    private static final UUID UNIQUE_TASKS = UUID.fromString("e2c6068a-8f0e-4d5b-822a-38ad6ecf98c9");
    private static final Map<ResourceLocation, Pair<Function<CompoundTag, ITaskRewardInstance>, Function<FriendlyByteBuf, ITaskRewardInstance>>> TASK_REWARD_SUPPLIER = new HashMap<>() {{
        put(LordLevelReward.ID, Pair.of(LordLevelReward::readNbt, LordLevelReward::decode));
        put(ItemRewardInstance.ID, Pair.of(ItemRewardInstance::readNbt, ItemRewardInstance::decode));
    }};

    public static void registerTaskReward(ResourceLocation id, Pair<Function<CompoundTag, ITaskRewardInstance>, Function<FriendlyByteBuf, ITaskRewardInstance>> functions) {
        if (TASK_REWARD_SUPPLIER.containsKey(id))
            throw new IllegalStateException("This id is already registered: " + id);
        TASK_REWARD_SUPPLIER.put(id, functions);
    }

    public static ITaskRewardInstance createReward(ResourceLocation id, CompoundTag nbt) {
        return TASK_REWARD_SUPPLIER.get(id).getKey().apply(nbt);
    }

    public static ITaskRewardInstance createReward(ResourceLocation id, FriendlyByteBuf buffer) {
        return TASK_REWARD_SUPPLIER.get(id).getValue().apply(buffer);
    }

    @Nonnull
    private final IPlayableFaction<?> faction;
    @Nonnull
    private final ServerPlayer player;
    @Nonnull
    private final IFactionPlayer<?> factionPlayer;
    @Nonnull
    private final Set<Task> completedTasks = new HashSet<>();
    @Nonnull
    private final Map<UUID, TaskWrapper> taskWrapperMap = new HashMap<>();

    public TaskManager(@Nonnull ServerPlayer player, @Nonnull IFactionPlayer<?> factionPlayer, @Nonnull IPlayableFaction<?> faction) {
        this.faction = faction;
        this.player = player;
        this.factionPlayer = factionPlayer;
    }

    // interface -------------------------------------------------------------------------------------------------------

    @Override
    public void abortTask(UUID taskBoardId, @Nonnull UUID taskInstance, boolean remove) {
        this.taskWrapperMap.get(taskBoardId).removeTask(taskInstance, remove);
    }


    @Override
    public void acceptTask(UUID taskBoardId, @Nonnull UUID taskInstance) {
        ITaskInstance ins = this.taskWrapperMap.get(taskBoardId).acceptTask(taskInstance, this.player.level.getGameTime() + getTaskTimeConfig() * 1200L);
        this.updateStats(ins);
    }

    /**
     * applies the reward of the given taskInstance
     */
    public void applyRewards(@Nonnull ITaskInstance taskInstance) {
        taskInstance.getReward().applyReward(this.factionPlayer);
    }

    /**
     * @param taskInstance the taskInstance that should be checked
     * @return whether the taskInstance can be completed or not
     */
    public boolean canCompleteTask(@Nonnull ITaskInstance taskInstance) {
        if (!isTaskUnlocked(taskInstance.getTask())) return false;
        if (!isTimeEnough(taskInstance, this.player.level.getGameTime())) return false;
        for (TaskRequirement.Requirement<?> requirement : taskInstance.getTask().getRequirement().getAll()) {
            if (!checkStat(taskInstance, requirement)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void completeTask(UUID taskBoardId, @Nonnull UUID taskInstance) {
        TaskWrapper wrapper = this.taskWrapperMap.get(taskBoardId);
        ITaskInstance ins = wrapper.getTaskInstance(taskInstance);
        if (!canCompleteTask(ins)) return;
        this.completedTasks.add(ins.getTask());
        wrapper.removeTask(ins, true);
        if (!ins.isUnique()) {
            ++wrapper.lessTasks;
        }
        this.removeRequirements(ins);
        this.applyRewards(ins);
    }

    /**
     * returns all completed task requirements for the given taskInstances for the specific task board
     *
     * @param taskInstances the task for which the requirements are needed
     * @return map of completed requirement per task
     */
    @Nonnull
    public Map<UUID, Map<ResourceLocation, Integer>> getCompletedRequirements(@Nonnull Collection<ITaskInstance> taskInstances) {
        Map<UUID, Map<ResourceLocation, Integer>> completedRequirements = Maps.newHashMap();
        taskInstances.forEach(task -> {
            Map<ResourceLocation, Integer> completed = getCompletedRequirements(task);
            if (!completed.isEmpty()) {
                completedRequirements.put(task.getId(), completed);
            }
        });
        return completedRequirements;
    }

    public int getTaskTimeConfig() {
        if (ServerLifecycleHooks.getCurrentServer().isDedicatedServer()) {
            return VampirismConfig.BALANCE.taskDurationDedicatedServer.get();
        }
        return VampirismConfig.BALANCE.taskDurationSinglePlayer.get();
    }

    // task filter -----------------------------------------------------------------------------------------------------

    @Override
    public boolean hasAvailableTasks(UUID taskBoardId) {
        return !(getTasks(taskBoardId).isEmpty() && getUniqueTasks().isEmpty());
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

    @Override
    public void openTaskMasterScreen(UUID taskBoardId) {
        if (player.containerMenu instanceof TaskBoardContainer) {
            TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(taskBoardId, TaskWrapper::new);
            Set<ITaskInstance> selectedTasks = new HashSet<>(getTasks(taskBoardId));
            selectedTasks.addAll(getUniqueTasks());
            VampirismMod.dispatcher.sendTo(new TaskStatusPacket(selectedTasks, this.getCompletableTasks(selectedTasks), getCompletedRequirements(selectedTasks), player.containerMenu.containerId, taskBoardId), player);
            wrapper.lastSeenPos = this.player.blockPosition();
        }
    }

    @Override
    public void openVampirismMenu() {
        if (player.containerMenu instanceof TaskContainer) {
            VampirismMod.dispatcher.sendTo(new TaskPacket(player.containerMenu.containerId, this.taskWrapperMap, this.taskWrapperMap.entrySet().stream().map(entry -> Pair.of(entry.getKey(), getCompletableTasks(entry.getValue().getAcceptedTasks()))).collect(Collectors.toMap(Pair::getKey, Pair::getValue)), this.taskWrapperMap.values().stream().map(wrapper -> Pair.of(wrapper.id, getCompletedRequirements(wrapper.tasks.values()))).collect(Collectors.toMap(Pair::getKey, Pair::getValue))), player);
        }
    }

    public void readNBT(@Nonnull CompoundTag compoundNBT) {
        if (compoundNBT.contains("taskWrapper")) {
            ListTag infos = compoundNBT.getList("taskWrapper", 10);
            for (int i = 0; i < infos.size(); i++) {
                CompoundTag nbt = infos.getCompound(i);
                TaskWrapper wrapper = TaskWrapper.readNBT(nbt);
                this.taskWrapperMap.put(wrapper.id, wrapper);
            }
        }
        //completed tasks
        if (compoundNBT.contains("completedTasks")) {
            compoundNBT.getCompound("completedTasks").getAllKeys().forEach(taskId -> {
                Task task = ModRegistries.TASKS.getValue(new ResourceLocation(taskId));
                if (task != null) {
                    this.completedTasks.add(task);
                }
            });
        }
    }

    // task actions ----------------------------------------------------------------------------------------------------

    /**
     * remove the taskInstance's requirements from the player
     */
    public void removeRequirements(@Nonnull ITaskInstance taskInstance) {
        taskInstance.getTask().getRequirement().removeRequirement(this.factionPlayer);
    }

    @Override
    public void reset() {
        this.completedTasks.clear();
        this.taskWrapperMap.values().forEach(wrapper -> {
            wrapper.lessTasks = 0;
            wrapper.tasks.clear();
        });
    }

    // general ---------------------------------------------------------------------------------------------------------

    @Override
    public void resetTaskLists() {
        this.taskWrapperMap.values().forEach(TaskWrapper::reset);
        this.updateTaskLists();
    }

    @Override
    public void resetUniqueTask(Task task) {
        if (!task.isUnique()) return;
        this.completedTasks.remove(task);
        TaskWrapper wrapper = this.taskWrapperMap.get(UNIQUE_TASKS);
        if (wrapper != null) {
            wrapper.tasks.values().removeIf(ins -> ins.getTask() == task);
        }
    }

    /**
     * updates the task list once per day ({@link #updateTaskLists()}
     */
    public void tick() {
        if (this.player.getCommandSenderWorld().getGameTime() % 24000 == 0) {
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
            value.tasks.values().removeIf(task -> !value.getAcceptedTasks().contains(task));
        }
    }

    @Override
    public boolean wasTaskCompleted(@Nonnull Task task) {
        return this.completedTasks.contains(task);
    }

    // task methods ----------------------------------------------------------------------------------------------------

    public void writeNBT(@Nonnull CompoundTag compoundNBT) {
        //completed tasks
        if (!this.completedTasks.isEmpty()) {
            CompoundTag tasksNBT = new CompoundTag();
            this.completedTasks.forEach((task) -> tasksNBT.putBoolean(Objects.requireNonNull(task.getRegistryName()).toString(), true));
            compoundNBT.put("completedTasks", tasksNBT);
        }


        if (!this.taskWrapperMap.isEmpty()) {
            ListTag infos = new ListTag();
            this.taskWrapperMap.forEach((a, b) -> infos.add(b.writeNBT(new CompoundTag())));
            compoundNBT.put("taskWrapper", infos);
        }
    }

    /**
     * checks if the requirement is completed
     *
     * @param taskInstance the taskInstance of the requirement
     * @param requirement  the requirement to check
     * @return if the requirement is completed
     */
    private boolean checkStat(@Nonnull ITaskInstance taskInstance, @Nonnull TaskRequirement.Requirement<?> requirement) {
        return getStat(taskInstance, requirement) >= requirement.getAmount(this.factionPlayer);
    }

    /**
     * removes all completable taskInstances from the given task set and returns the removed taskInstances
     *
     * @param taskInstances the taskInstances to be filtered
     * @return all completable taskInstances from the given task set
     */
    private Set<UUID> getCompletableTasks(@Nonnull Set<ITaskInstance> taskInstances) {
        return taskInstances.stream().filter(this::canCompleteTask).map(ITaskInstance::getId).collect(Collectors.toSet());
    }

    /**
     * returns all completed taskInstance requirements for the given taskInstance for the specific taskInstance board
     *
     * @param taskInstance the taskInstance to be checked
     * @return a list of all taskInstance requirements
     */
    private Map<ResourceLocation, Integer> getCompletedRequirements(@Nonnull ITaskInstance taskInstance) {
        Map<ResourceLocation, Integer> completed = new HashMap<>();
        for (TaskRequirement.Requirement<?> requirement : taskInstance.getTask().getRequirement().getAll()) {
            completed.put(requirement.getId(), getStat(taskInstance, requirement));
        }
        return completed;
    }

    private int getStat(@Nonnull ITaskInstance taskInstance, @Nonnull TaskRequirement.Requirement<?> requirement) {
        Map<ResourceLocation, Integer> stats = taskInstance.getStats();
        if (!taskInstance.isAccepted()) return 0;
        int neededStat = 0;
        int actualStat = 0;
        switch (requirement.getType()) {
            case STATS -> {
                actualStat = this.player.getStats().getValue(Stats.CUSTOM.get((ResourceLocation) requirement.getStat(this.factionPlayer)));
                neededStat = stats.get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
            }
            case ENTITY -> {
                actualStat = this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat(this.factionPlayer)));
                neededStat = stats.get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
            }
            case ENTITY_TAG -> {
                //noinspection unchecked
                for (Holder<EntityType<?>> type : Registry.ENTITY_TYPE.getTagOrEmpty((TagKey<EntityType<?>>) requirement.getStat(this.factionPlayer))) {
                    actualStat += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type.value()));
                }
                neededStat = stats.get(requirement.getId()) + requirement.getAmount(this.factionPlayer);
            }
            case ITEMS -> {
                ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                neededStat = stack.getCount();
                actualStat = this.player.getInventory().countItem(stack.getItem());
            }
            case BOOLEAN -> {
                if (!(Boolean) requirement.getStat(this.factionPlayer)) return 0;
                return 1;
            }
        }
        return Math.min(requirement.getAmount(this.factionPlayer) - (neededStat - actualStat), requirement.getAmount(this.factionPlayer));
    }

    /**
     * gets all visible tasks for a task board
     * <p>
     * locks task that are no longer unlocked
     * if there are fewer tasks already chosen, add new task
     *
     * @param taskBoardId the id of the task board
     * @return all visible tasks for the task board
     */
    private Collection<ITaskInstance> getTasks(UUID taskBoardId) {
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(taskBoardId, TaskWrapper::new);
        if (!wrapper.tasks.isEmpty()) {
            this.removeLockedTasks(wrapper.getTaskInstances());
        }
        wrapper.taskAmount = wrapper.taskAmount < 0 ? player.getRandom().nextInt(VampirismConfig.BALANCE.taskMasterMaxTaskAmount.get()) + 1 - wrapper.lessTasks : wrapper.taskAmount;
        if (wrapper.tasks.size() < wrapper.taskAmount) {
            List<Task> tasks = new ArrayList<>(ModRegistries.TASKS.getValues());
            Collections.shuffle(tasks);
            wrapper.tasks.putAll(tasks.stream().filter(this::matchesFaction).filter(task -> !task.isUnique()).filter(this::isTaskUnlocked).limit(wrapper.taskAmount - wrapper.tasks.size()).map(task -> new TaskInstance(task, taskBoardId, this.factionPlayer, this.getTaskTimeConfig() * 1200L)).collect(Collectors.toMap(TaskInstance::getId, t -> t)));
        }
        this.updateStats(wrapper.getTaskInstances());
        return wrapper.getTaskInstances();
    }

    /**
     * gets all visible unique tasks
     * <p>
     * locks task that are no longer unlocked
     *
     * @return all visible unique tasks
     */
    private Collection<ITaskInstance> getUniqueTasks() {
        TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(UNIQUE_TASKS, TaskWrapper::new);
        Map<UUID, ITaskInstance> uniqueTasks = wrapper.tasks;
        if (!uniqueTasks.isEmpty()) {
            this.removeLockedTasks(uniqueTasks.values());
        }
        Collection<Task> tasks = uniqueTasks.values().stream().map(ITaskInstance::getTask).collect(Collectors.toSet());
        uniqueTasks.putAll(ModRegistries.TASKS.getValues().stream().filter(this::matchesFaction).filter(Task::isUnique).filter(task -> !tasks.contains(task)).filter(task -> !this.completedTasks.contains(task)).filter(this::isTaskUnlocked).map(task -> new TaskInstance(task, UNIQUE_TASKS, this.factionPlayer, 0)).collect(Collectors.toMap(TaskInstance::getId, a -> a)));
        wrapper.tasks.putAll(uniqueTasks);
        this.updateStats(uniqueTasks.values());
        return uniqueTasks.values();
    }

    private boolean isTimeEnough(ITaskInstance taskInstance, long gameTime) {
        if (!taskInstance.isUnique()) {
            return taskInstance.getTaskTimeStamp() >= gameTime;
        }
        return true;
    }

    /**
     * @param task the task that should be checked
     * @return whether the task's faction is applicant to the taskManager's {@link #faction}
     */
    private boolean matchesFaction(@Nonnull Task task) {
        return task.getFaction() == this.faction || task.getFaction() == null;
    }

    /**
     * removes all no longer unlocked task from the specific task board
     *
     * @param taskInstances the task to be checked
     */
    private void removeLockedTasks(@Nonnull Collection<ITaskInstance> taskInstances) {
        taskInstances.removeIf(task -> {
            if (!this.isTaskUnlocked(task.getTask())) {
                task.aboardTask();
                return true;
            }
            return false;
        });
    }

    // save/load -------------------------------------------------------------------------------------------------------

    /**
     * updated the saved stat target for the taskInstances of the task board
     *
     * @param taskInstances the taskInstances to be updated
     */
    private void updateStats(@Nonnull Collection<ITaskInstance> taskInstances) {
        taskInstances.forEach(this::updateStats);
    }

    /**
     * updated the saved stat target for the taskInstance of the taskInstance board
     *
     * @param taskInstance the taskInstance to be updated
     */
    private void updateStats(@Nonnull ITaskInstance taskInstance) {
        if (!taskInstance.isAccepted()) return;
        if (!taskInstance.getTask().getRequirement().isHasStatBasedReq()) return;
        Map<ResourceLocation, Integer> reqStats = taskInstance.getStats();
        for (TaskRequirement.Requirement<?> requirement : taskInstance.getTask().getRequirement().getAll()) {
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
                    for (Holder<EntityType<?>> type : Registry.ENTITY_TYPE.getTagOrEmpty((TagKey<EntityType<?>>) requirement.getStat(this.factionPlayer))) {
                        amount += this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type.value()));
                    }
                    reqStats.putIfAbsent(requirement.getId(), amount);
                    break;
                default:
            }
        }
    }

    public static class TaskWrapper {

        public static TaskWrapper readNBT(@Nonnull CompoundTag nbt) {
            UUID id = nbt.getUUID("id");
            int lessTasks = nbt.getInt("lessTasks");
            int taskAmount = nbt.getInt("taskAmount");
            Map<UUID, ITaskInstance> tasks = new HashMap<>();
            BlockPos taskBoardInfo = null;
            if (nbt.contains("pos")) {
                ListTag pos = nbt.getList("pos", 6);
                taskBoardInfo = new BlockPos(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2));
            }

            int taskSize = nbt.getInt("tasksSize");
            ListTag tasksNBT = nbt.getList("tasks", 10);
            for (int i = 0; i < taskSize; i++) {
                TaskInstance ins = TaskInstance.readNBT(tasksNBT.getCompound(i));
                if (ins != null) {
                    tasks.put(ins.getId(), ins);
                }
            }
            return new TaskWrapper(id, lessTasks, taskAmount, tasks, taskBoardInfo);
        }

        public static TaskWrapper decode(FriendlyByteBuf buffer) {
            UUID id = buffer.readUUID();
            int lessTasks = buffer.readVarInt();
            int taskAmount = buffer.readVarInt();
            BlockPos pos = null;
            if (buffer.readBoolean()) {
                pos = buffer.readBlockPos();
            }
            int tasksSize = buffer.readVarInt();
            Map<UUID, ITaskInstance> tasks = new HashMap<>();
            for (int i = 0; i < tasksSize; i++) {
                TaskInstance ins = TaskInstance.decode(buffer);
                tasks.put(ins.getId(), ins);
            }
            return new TaskWrapper(id, lessTasks, taskAmount, tasks, pos);
        }

        private final UUID id;
        @Nonnull
        private final Map<UUID, ITaskInstance> tasks;
        private int lessTasks;
        private int taskAmount;
        @Nullable
        private BlockPos lastSeenPos;

        public TaskWrapper(UUID id) {
            this.id = id;
            this.lessTasks = 0;
            this.taskAmount = -1;
            this.tasks = new HashMap<>();
            this.lastSeenPos = null;
        }

        private TaskWrapper(UUID id, int lessTasks, int taskAmount, @Nonnull Map<UUID, ITaskInstance> tasks, @Nullable BlockPos lastSeenPos) {
            this.id = id;
            this.lessTasks = lessTasks;
            this.taskAmount = taskAmount;
            this.tasks = tasks;
            this.lastSeenPos = lastSeenPos;
        }

        public ITaskInstance acceptTask(UUID taskInstance, long timeStamp) {
            ITaskInstance ins = this.tasks.get(taskInstance);
            ins.startTask(timeStamp);
            return ins;
        }

        public void encode(FriendlyByteBuf buffer) {
            buffer.writeUUID(this.id);
            buffer.writeVarInt(this.lessTasks);
            buffer.writeVarInt(this.taskAmount);
            buffer.writeBoolean(this.lastSeenPos != null);
            if (this.lastSeenPos != null) {
                buffer.writeBlockPos(this.lastSeenPos);
            }
            buffer.writeVarInt(this.tasks.size());
            this.tasks.values().forEach(taskInstance -> taskInstance.encode(buffer));
        }

        /**
         * This returns a {@link Map#keySet()}, which means that adding elements is not supported.
         */
        @Nonnull
        public Set<ITaskInstance> getAcceptedTasks() {
            return this.tasks.values().stream().filter(ITaskInstance::isAccepted).collect(Collectors.toSet());
        }

        public UUID getId() {
            return id;
        }

        @Nonnull
        public Optional<BlockPos> getLastSeenPos() {
            return Optional.ofNullable(lastSeenPos);
        }

        public ITaskInstance getTaskInstance(UUID taskInstance) {
            return this.tasks.get(taskInstance);
        }

        @Nonnull
        public Collection<ITaskInstance> getTaskInstances() {
            return tasks.values();
        }

        public void removeTask(ITaskInstance taskInstance, boolean delete) {
            if (delete) {
                this.tasks.remove(taskInstance.getId());
            }
            taskInstance.aboardTask();
        }

        public void removeTask(UUID taskInstance, boolean delete) {
            this.removeTask(this.tasks.get(taskInstance), delete);
        }

        public CompoundTag writeNBT(@Nonnull CompoundTag nbt) {
            nbt.putUUID("id", this.id);
            nbt.putInt("lessTasks", this.lessTasks);
            nbt.putInt("taskAmount", this.taskAmount);

            ListTag tasks = new ListTag();
            this.tasks.forEach((id, task) -> tasks.add(task.writeNBT(new CompoundTag())));
            nbt.put("tasks", tasks);
            nbt.putInt("tasksSize", this.tasks.size());

            BlockPos pos = lastSeenPos;
            if (pos != null) {
                nbt.put("pos", Helper.newDoubleNBTList(pos.getX(), pos.getY(), pos.getZ()));
            }

            return nbt;
        }

        private void reset() {
            this.tasks.clear();
            this.lessTasks = 0;
            this.taskAmount = -1;
        }
    }

}
