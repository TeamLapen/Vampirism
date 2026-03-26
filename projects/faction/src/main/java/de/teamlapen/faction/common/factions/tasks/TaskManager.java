package de.teamlapen.faction.common.factions.tasks;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFactionSpecificTags;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.tasks.*;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.FactionStats;
import de.teamlapen.faction.common.factions.tasks.requirements.ItemRequirement;
import de.teamlapen.faction.common.network.packets.client.ClientboundTaskPacket;
import de.teamlapen.faction.common.network.packets.client.ClientboundTaskStatusPacket;
import de.teamlapen.faction.common.network.packets.server.ServerboundTaskActionPacket;
import de.teamlapen.faction.common.tags.FactionTaskTags;
import de.teamlapen.faction.common.util.collections.CollectionUtil;
import de.teamlapen.faction.common.world.inventory.FactionMenu;
import de.teamlapen.faction.common.world.inventory.ITaskMenu;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import de.teamlapen.faction.common.world.inventory.TaskBoardMenu;
import de.teamlapen.sync.PropertySync;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager<T extends ITaskPlayer<T>> extends PropertySync implements ITaskManager {
    private static final UUID UNIQUE_TASKS = UUID.fromString("e2c6068a-8f0e-4d5b-822a-38ad6ecf98c9");
    private static final Codec<Map<UUID, TaskWrapper>> TASK_WRAPPER_CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, TaskWrapper.CODEC);

    private final Holder<? extends IPlayableFaction<?>> faction;
    private final ServerPlayer player;
    @NotNull
    private final T factionPlayer;
    private final Set<ResourceKey<Task>> completedTasks = new HashSet<>();
    private final Map<UUID, TaskWrapper> taskWrapperMap = new HashMap<>();

    private final Registry<Task> registry;

    public TaskManager(ServerPlayer player, @NotNull T factionPlayer, Holder<? extends IPlayableFaction<?>> faction) {
        this.faction = faction;
        this.player = player;
        this.factionPlayer = factionPlayer;
        this.registry = player.level().registryAccess().lookupOrThrow(FactionRegistries.Keys.TASK);
    }

    @Override
    public void sync() {
        this.factionPlayer.sync();
    }

    // interface -------------------------------------------------------------------------------------------------------

    @Override
    public void abortTask(UUID taskBoardId, UUID taskInstance, boolean remove) {
        this.taskWrapperMap.get(taskBoardId).removeTask(taskInstance, remove);
    }


    @Override
    public void acceptTask(UUID taskBoardId, UUID taskInstance) {
        this.player.awardStat(FactionStats.TASKS_ACCEPTED.get());
        ITaskInstance ins = this.taskWrapperMap.get(taskBoardId).acceptTask(taskInstance, this.player.level().getGameTime() + getTaskTimeConfig() * 1200L);
        this.updateStats(ins);
    }

    /**
     * Handle a task action message that was sent from client to server
     */
    public void handleTaskActionMessage(ServerboundTaskActionPacket msg) {
        switch (msg.action()) {
            case ITaskMenu.TaskAction.COMPLETE -> completeTask(msg.entityId(), msg.task());
            case ITaskMenu.TaskAction.ACCEPT -> acceptTask(msg.entityId(), msg.task());
            default -> abortTask(msg.entityId(), msg.task(), msg.action() == ITaskMenu.TaskAction.REMOVE);
        }
    }

    /**
     * applies the reward of the given taskInstance
     */
    public void applyRewards(ITaskInstance taskInstance) {
        taskInstance.getReward().applyReward(this.factionPlayer);
    }

    /**
     * @param taskInstance the taskInstance that should be checked
     * @return whether the taskInstance can be completed or not
     */
    public boolean canCompleteTask(ITaskInstance taskInstance) {
        if (!isTaskUnlocked(taskInstance.getTask())) return false;
        if (!isTimeEnough(taskInstance, this.player.level().getGameTime())) return false;
        for (TaskRequirement.Requirement<?> requirement : getTask(taskInstance.getTask()).requirements().getAll()) {
            if (!checkStat(taskInstance, requirement)) {
                return false;
            }
        }
        return true;
    }

    private Task getTask(ResourceKey<Task> key) {
        return this.registry.getValue(key.identifier());
    }

    @Override
    public void completeTask(UUID taskBoardId, UUID taskInstance) {
        TaskWrapper wrapper = this.taskWrapperMap.get(taskBoardId);
        ITaskInstance ins = wrapper.getTaskInstance(taskInstance);
        if (!canCompleteTask(ins)) return;
        this.completedTasks.add(ins.getTask());
        wrapper.removeTask(ins, true);
        if (!ins.isUnique(this.registry)) {
            ++wrapper.lessTasks;
        }
        this.removeRequirements(ins);
        this.applyRewards(ins);
        this.player.awardStat(FactionStats.TASKS_COMPLETED.get());
    }

    /**
     * returns all completed task requirements for the given taskInstances for the specific task board
     *
     * @param taskInstances the task for which the requirements are needed
     * @return map of completed requirement per task
     */
    public Map<UUID, Map<Identifier, Integer>> getCompletedRequirements(Collection<ITaskInstance> taskInstances) {
        Map<UUID, Map<Identifier, Integer>> completedRequirements = Maps.newHashMap();
        taskInstances.forEach(task -> {
            Map<Identifier, Integer> completed = getCompletedRequirements(task);
            if (!completed.isEmpty()) {
                completedRequirements.put(task.getId(), completed);
            }
        });
        return completedRequirements;
    }

    public int getTaskTimeConfig() {
        if (ServerLifecycleHooks.getCurrentServer().isDedicatedServer()) {
            return FactionConfig.server().taskDurationDedicatedServer.get();
        }
        return FactionConfig.server().taskDurationSinglePlayer.get();
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
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isTaskUnlocked(ResourceKey<Task> task) {
        if (!matchesFaction(task)) return false;
        for (TaskUnlocker taskUnlocker : getTask(task).unlocker()) {
            if (!taskUnlocker.isUnlocked(this.factionPlayer)) {
                return false;
            }
        }
        return true;
    }

    public boolean isTaskUnlocked(Holder<Task> task) {
        if (!matchesFaction(task)) return false;
        for (TaskUnlocker taskUnlocker : task.value().unlocker()) {
            if (!taskUnlocker.isUnlocked(this.factionPlayer)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void openTaskBoardScreen(UUID taskBoardId) {
        if (player.containerMenu instanceof TaskBoardMenu) {
            TaskWrapper wrapper = this.taskWrapperMap.computeIfAbsent(taskBoardId, TaskWrapper::new);
            player.connection.send(createTaskBoardPacket(taskBoardId));
            wrapper.lastSeenPos = this.player.blockPosition();
        }
    }

    private ClientboundTaskStatusPacket createTaskBoardPacket(UUID taskBoard) {
        Set<ITaskInstance> selectedTasks = new HashSet<>(getTasks(taskBoard));
        selectedTasks.addAll(getUniqueTasks());
        return new ClientboundTaskStatusPacket(selectedTasks, this.getCompletableTasks(selectedTasks), getCompletedRequirements(selectedTasks), player.containerMenu.containerId, taskBoard);
    }

    private ClientboundTaskPacket createFactionMenuPacket() {
        return new ClientboundTaskPacket(player.containerMenu.containerId, this.taskWrapperMap, this.taskWrapperMap.entrySet().stream().map(entry -> Pair.of(entry.getKey(), getCompletableTasks(entry.getValue().getAcceptedTasks()))).collect(Collectors.toMap(Pair::getKey, Pair::getValue)), this.taskWrapperMap.values().stream().map(wrapper -> Pair.of(wrapper.id, getCompletedRequirements(wrapper.tasks.values()))).collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
    }

    @Override
    @Nullable
    public CustomPacketPayload getUpdatePacket(UUID taskBoard) {
        if (player.containerMenu instanceof TaskBoardMenu) {
            return createTaskBoardPacket(taskBoard);
        } else if (player.containerMenu instanceof ITaskMenu) {
            return createFactionMenuPacket();
        }
        return null;
    }

    @Override
    public void openFactionMenu() {
        if (!player.isAlive()) return;
        player.openMenu(new SimpleMenuProvider((i, inventory, player) -> new FactionMenu(i, inventory), Component.empty()));
        if (player.containerMenu instanceof ITaskMenu) {
            player.connection.send(createFactionMenuPacket());
        }
    }

    @Override
    protected void registerProperties() {
        this.registerProperty(FIdentifier.mod("completed_tasks")).set(ResourceKey.codec(FactionRegistries.Keys.TASK)).provider(() -> this.completedTasks).serverLoader(x -> CollectionUtil.updateCollection(this.completedTasks, x)).register();
        this.registerProperty(FIdentifier.mod("task_wrapper")).map(TASK_WRAPPER_CODEC).provider(() -> this.taskWrapperMap).serverLoader(x -> {
            this.taskWrapperMap.clear();
            this.taskWrapperMap.putAll(x);
        }).register();
    }

    /**
     * remove the taskInstance's requirements from the player
     */
    public void removeRequirements(ITaskInstance taskInstance) {
        getTask(taskInstance.getTask()).requirements().removeRequirement(this.factionPlayer);
    }

    @Override
    public void reset() {
        this.completedTasks.clear();
        this.taskWrapperMap.values().forEach(wrapper -> {
            wrapper.lessTasks = 0;
            wrapper.tasks.clear();
        });
    }

    @Override
    public void resetTaskLists() {
        this.taskWrapperMap.values().forEach(TaskWrapper::reset);
        this.updateTaskLists();
    }

    @Override
    public void resetUniqueTask(ResourceKey<Task> id) {
        this.registry.get(id).filter(a -> a.is(FactionTaskTags.IS_UNIQUE)).ifPresent(task -> {
            this.completedTasks.remove(task.key());
            TaskWrapper wrapper = this.taskWrapperMap.get(UNIQUE_TASKS);
            if (wrapper != null) {
                wrapper.tasks.values().removeIf(ins -> task.is(ins.getTask()));
            }
        });

    }

    /**
     * updates the task list once per day ({@link #updateTaskLists()}
     */
    public void tick() {
        if (this.player.level().getGameTime() % 24000 == 0) {
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
    public boolean wasTaskCompleted(ResourceKey<Task> task) {
        return this.completedTasks.contains(task);
    }

    /**
     * checks if the requirement is completed
     *
     * @param taskInstance the taskInstance of the requirement
     * @param requirement  the requirement to check
     * @return if the requirement is completed
     */
    private boolean checkStat(ITaskInstance taskInstance, TaskRequirement.Requirement<?> requirement) {
        return getStat(taskInstance, requirement) >= requirement.getAmount(this.factionPlayer);
    }

    /**
     * removes all completable taskInstances from the given task set and returns the removed taskInstances
     *
     * @param taskInstances the taskInstances to be filtered
     * @return all completable taskInstances from the given task set
     */
    private Set<UUID> getCompletableTasks(Set<ITaskInstance> taskInstances) {
        return taskInstances.stream().filter(this::canCompleteTask).map(ITaskInstance::getId).collect(Collectors.toSet());
    }

    /**
     * returns all completed taskInstance requirements for the given taskInstance for the specific taskInstance board
     *
     * @param taskInstance the taskInstance to be checked
     * @return a map of all taskInstance requirements
     */
    private Map<Identifier, Integer> getCompletedRequirements(ITaskInstance taskInstance) {
        Map<Identifier, Integer> completed = new HashMap<>();
        for (TaskRequirement.Requirement<?> requirement : getTask(taskInstance.getTask()).requirements().getAll()) {
            completed.put(requirement.id(), getStat(taskInstance, requirement));
        }
        return completed;
    }

    private int getStat(ITaskInstance taskInstance, TaskRequirement.Requirement<?> requirement) {
        Map<Identifier, Integer> stats = taskInstance.getStats();
        if (!taskInstance.isAccepted()) return 0;
        int neededStat = 0;
        int actualStat = 0;
        switch (requirement.getType()) {
            case STATS -> {
                actualStat = this.player.getStats().getValue(Stats.CUSTOM.get((Identifier) requirement.getStat(this.factionPlayer)));
                neededStat = stats.get(requirement.id()) + requirement.getAmount(this.factionPlayer);
            }
            case ENTITY -> {
                actualStat = this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat(this.factionPlayer)));
                neededStat = stats.get(requirement.id()) + requirement.getAmount(this.factionPlayer);
            }
            case ENTITY_TAG -> {
                //noinspection unchecked
                actualStat += BuiltInRegistries.ENTITY_TYPE.get((TagKey<EntityType<?>>) requirement.getStat(this.factionPlayer)).stream().flatMap(HolderSet.ListBacked::stream).map(Holder::value).mapToInt(type -> this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type))).sum();
                neededStat = stats.get(requirement.id()) + requirement.getAmount(this.factionPlayer);
            }
            case ITEMS -> {
                ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                neededStat = stack.getCount();
                actualStat = InventoryHelper.countItemWithComponent(this.player.getInventory(), stack);
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
        wrapper.taskAmount = wrapper.taskAmount < 0 ? player.getRandom().nextInt(FactionConfig.server().taskMasterMaxTasks.get()) + 1 - wrapper.lessTasks : wrapper.taskAmount;
        if (wrapper.tasks.size() < wrapper.taskAmount) {
            List<Holder.Reference<Task>> tasks = this.registry.listElements().collect(Collectors.toList());
            Collections.shuffle(tasks);
            wrapper.tasks.putAll(tasks.stream().filter(this::matchesFaction).filter(task -> !task.is(FactionTaskTags.IS_UNIQUE)).filter(this::isTaskUnlocked).limit(wrapper.taskAmount - wrapper.tasks.size()).map(task -> new TaskInstance(task, taskBoardId, this.factionPlayer, this.getTaskTimeConfig() * 1200L)).collect(Collectors.toMap(TaskInstance::getId, t -> t)));
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
        Collection<ResourceKey<Task>> tasks = uniqueTasks.values().stream().map(ITaskInstance::getTask).collect(Collectors.toSet());
        uniqueTasks.putAll(this.registry.listElements().filter(this::matchesFaction).filter(t -> t.is(FactionTaskTags.IS_UNIQUE)).filter(task -> !tasks.contains(task.key())).filter(task -> !this.completedTasks.contains(task.key())).filter(this::isTaskUnlocked).map(task -> new TaskInstance(task, UNIQUE_TASKS, this.factionPlayer, 0)).collect(Collectors.toMap(TaskInstance::getId, a -> a)));
        wrapper.tasks.putAll(uniqueTasks);
        this.updateStats(uniqueTasks.values());
        return uniqueTasks.values();
    }

    private boolean isTimeEnough(ITaskInstance taskInstance, long gameTime) {
        if (!taskInstance.isUnique(this.registry)) {
            return taskInstance.getTaskTimeStamp() >= gameTime;
        }
        return true;
    }

    /**
     * @param task the task that should be checked
     * @return whether the task's faction is applicant to the taskManager's {@link #faction}
     */
    private boolean matchesFaction(ResourceKey<Task> task) {
        return this.registry.get(task).map(this::matchesFaction).orElse(false);
    }

    private boolean matchesFaction(Holder<Task> task) {
        return !task.is(FactionTaskTags.HAS_FACTION) || IFactionSpecificTags.get().get(this.faction,FactionRegistries.Keys.TASK).map(task::is).orElse(false);
    }

    /**
     * removes all no longer unlocked task from the specific task board
     *
     * @param taskInstances the task to be checked
     */
    private void removeLockedTasks(Collection<ITaskInstance> taskInstances) {
        taskInstances.removeIf(task -> {
            if (!this.isTaskUnlocked(task.getTask())) {
                task.abortTask();
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
    private void updateStats(Collection<ITaskInstance> taskInstances) {
        taskInstances.forEach(this::updateStats);
    }

    /**
     * updated the saved stat target for the taskInstance of the taskInstance board
     *
     * @param taskInstance the taskInstance to be updated
     */
    private void updateStats(ITaskInstance taskInstance) {
        if (!taskInstance.isAccepted()) return;
        Task task = getTask(taskInstance.getTask());
        if (!task.requirements().isHasStatBasedReq()) return;
        Map<Identifier, Integer> reqStats = taskInstance.getStats();
        for (TaskRequirement.Requirement<?> requirement : task.requirements().getAll()) {
            switch (requirement.getType()) {
                case STATS -> reqStats.putIfAbsent(requirement.id(), this.player.getStats().getValue(Stats.CUSTOM.get((Identifier) requirement.getStat(this.factionPlayer))));
                case ENTITY -> reqStats.putIfAbsent(requirement.id(), this.player.getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat(this.factionPlayer))));
                case ENTITY_TAG ->
                    //noinspection unchecked
                        reqStats.putIfAbsent(requirement.id(), BuiltInRegistries.ENTITY_TYPE.get((TagKey<EntityType<?>>) requirement.getStat(this.factionPlayer)).stream().flatMap(HolderSet.ListBacked::stream).map(Holder::value).mapToInt(type -> this.player.getStats().getValue(Stats.ENTITY_KILLED.get(type))).sum());
                default -> {
                }
            }
        }
    }

    public static class TaskWrapper {

        public static final Codec<TaskWrapper> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(i -> i.id),
                Codec.INT.fieldOf("lessTasks").forGetter(i -> i.lessTasks),
                Codec.INT.fieldOf("taskAmount").forGetter(i -> i.taskAmount),
                Codec.unboundedMap(UUIDUtil.STRING_CODEC, TaskInstance.CODEC).fieldOf("tasksSize").forGetter(i -> i.tasks),
                BlockPos.CODEC.optionalFieldOf("lastSeenPos").forGetter(i -> Optional.ofNullable(i.lastSeenPos))
        ).apply(instance, TaskWrapper::new));

        private final UUID id;
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

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private TaskWrapper(UUID id, int lessTasks, int taskAmount, Map<UUID, ITaskInstance> tasks, Optional<BlockPos> lastSeenPos) {
            this.id = id;
            this.lessTasks = lessTasks;
            this.taskAmount = taskAmount;
            this.tasks = new HashMap<>(tasks);
            this.lastSeenPos = lastSeenPos.orElse(null);
        }

        public ITaskInstance acceptTask(UUID taskInstance, long timeStamp) {
            ITaskInstance ins = this.tasks.get(taskInstance);
            ins.startTask(timeStamp);
            return ins;
        }

        /**
         * This returns a {@link Map#keySet()}, which means that adding elements is not supported.
         */
        public Set<ITaskInstance> getAcceptedTasks() {
            return this.tasks.values().stream().filter(ITaskInstance::isAccepted).collect(Collectors.toSet());
        }

        public UUID getId() {
            return id;
        }

        public Optional<BlockPos> getLastSeenPos() {
            return Optional.ofNullable(lastSeenPos);
        }

        public ITaskInstance getTaskInstance(UUID taskInstance) {
            return this.tasks.get(taskInstance);
        }

        public Collection<ITaskInstance> getTaskInstances() {
            return tasks.values();
        }

        public void removeTask(ITaskInstance taskInstance, boolean delete) {
            if (delete) {
                this.tasks.remove(taskInstance.getId());
            }
            taskInstance.abortTask();
        }

        public void removeTask(UUID taskInstance, boolean delete) {
            this.removeTask(this.tasks.get(taskInstance), delete);
        }

        private void reset() {
            this.tasks.clear();
            this.lessTasks = 0;
            this.taskAmount = -1;
        }
    }

}
