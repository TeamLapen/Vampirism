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
    private final @Nonnull Map<Task, Map<ResourceLocation, Integer>> stats = Maps.newHashMap();
    private final @Nonnull Map<Task, Map<EntityType<?>, Integer>> entityStats = Maps.newHashMap();
    private boolean init;

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
        this.completedTasks.add(task);
        this.getAvailableTasks().remove(task);
        this.availableTasks.addAll(ModRegistries.TASKS.getValues().stream().filter(task1 -> task1.requireParent() && task == task1.getParentTask()).collect(Collectors.toList()));
        this.updateStats();
        return true;
    }

    @Override
    public void removeRequirements(@Nonnull Task task) {
        for (TaskRequirement<?> requirement : task.getRequirements()) {
            if (requirement.getType().equals(TaskRequirement.Type.ITEMS)) {
                this.player.inventory.clearMatchingItems(itemStack -> itemStack.getItem() == requirement.getStat(), requirement.getAmount());
            }
        }
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

    @Override
    public void applyRewards(Task task) {
        task.getRewards().forEach(reward -> reward.applyReward(this.player));
    }

    @Override
    public boolean hasAvailableTasks() {
        return !this.availableTasks.isEmpty();
    }

    /**
     * returns {@link TaskManager#availableTasks} and initiate the Stats on first call
     */
    @Nonnull
    @Override
    public Set<Task> getAvailableTasks() {
        return this.availableTasks;
    }

    @Nonnull
    public Set<Task> getCompletableTasks() {
        return this.getAvailableTasks().stream().filter(this::canCompleteTask).collect(Collectors.toSet());
    }

    @Override
    @Nonnull
    public IPlayableFaction<?> getFaction() {
        return faction;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public boolean canCompleteTask(Task task) {
        for (TaskRequirement<?> requirement : task.getRequirements()) {
            if (task.requireParent() && !this.completedTasks.contains(task.getParentTask())) return false;
            switch (requirement.getType()) {
                case STATS:
                    if (this.player.getEntityWorld().isRemote()) return false;
                    if (((ServerPlayerEntity) this.player).getStats().getValue(Stats.CUSTOM.get((ResourceLocation) requirement.getStat())) < this.getStats().get(task).get(requirement.getStat()) + requirement.getAmount())
                        return false;
                    continue;
                case ENTITY:
                    if (this.player.getEntityWorld().isRemote()) return false;
                    if (((ServerPlayerEntity) this.player).getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat())) < this.getEntityStats().get(task).get(requirement.getStat()) + requirement.getAmount())
                        return false;
                    continue;
                case ITEMS:
                    ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                    if (this.player.inventory.count(stack.getItem()) < stack.getCount()) return false;
                    continue;
                case BOOLEAN:
                    if (!(Boolean) requirement.getStat()) return false;
                    continue;
                default:
                    return false;
            }
        }
        return true;
    }

    @Override
    public void reset() {
        this.completedTasks.clear();
        this.availableTasks.addAll(ModRegistries.TASKS.getValues().stream().filter(task -> (faction.equals(task.getFaction()) || task.getFaction() == null) && !task.requireParent()).collect(Collectors.toList()));
        this.stats.clear();
        this.init = true;
    }

    /**
     * cannot be called in constructor, because player is not constructed yet
     */
    private void updateStats() {
        for (Task task : this.availableTasks) {
            for (TaskRequirement<?> requirement : task.getRequirements()) {
                if (this.getStats().containsKey(task) || this.getEntityStats().containsKey(task)) continue;
                switch (requirement.getType()) {
                    case STATS:
                        if (!player.getEntityWorld().isRemote()) {
                            this.getStats().computeIfAbsent(task, task1 -> Maps.newHashMap()).put((ResourceLocation) requirement.getStat(), ((ServerPlayerEntity) this.player).getStats().getValue(Stats.CUSTOM.get((ResourceLocation) requirement.getStat())));
                        }
                        break;
                    case ENTITY:
                        if (!player.getEntityWorld().isRemote()) {
                            this.getEntityStats().computeIfAbsent(task, task1 -> Maps.newHashMap()).put((EntityType<?>) requirement.getStat(), ((ServerPlayerEntity) this.player).getStats().getValue(Stats.ENTITY_KILLED.get((EntityType<?>) requirement.getStat())));
                        }
                        break;
                    default:
                }
            }
        }
        this.getStats().entrySet().removeIf(entry -> !this.availableTasks.contains(entry.getKey()));
        this.getEntityStats().entrySet().removeIf(entry -> !this.availableTasks.contains(entry.getKey()));
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
            CompoundNBT tasks = new CompoundNBT();
            this.completedTasks.forEach(task -> tasks.putBoolean(task.getRegistryName().toString(), true));
            compoundNBT.put("tasks", tasks);
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
