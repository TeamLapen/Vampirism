package de.teamlapen.vampirism.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskFinishedPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskManager implements ITaskManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private final @Nonnull PlayerEntity player;
    private final @Nonnull IPlayableFaction<?> faction;
    private final @Nonnull List<Task> completedTasks = Lists.newArrayList();
    private final @Nonnull List<Task> availableTasks = Lists.newArrayList();
    private final @Nonnull Map<Task, Map<Stat<?>, Integer>> stats = Maps.newHashMap();
    private boolean init = true;

    public TaskManager(@Nonnull PlayerEntity player, @Nonnull IPlayableFaction<?> faction) {
        this.faction = faction;
        this.player = player;
        this.reset();
    }

    @Nonnull
    public static List<Task> getTasks(PlayerEntity player, NonNullFunction<? super ITaskManager, List<Task>> mapper) {
        return FactionPlayerHandler.getOpt(player).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(IFactionPlayer::getTaskManager).map(mapper).orElse(ImmutableList.of());
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
        return true;
    }

    @Override
    public void removeRequirements(@Nonnull Task task) {
        for (TaskRequirement requirement : task.getRequirements()) {
            if (requirement.getType().equals(TaskRequirement.Type.ITEMS)) {
                ItemStack stack = ((ItemRequirement) requirement).getItemRequirement();
                this.player.inventory.clearMatchingItems(itemStack -> itemStack.isItemEqual(stack), stack.getCount());
            }
        }
    }

    @Nonnull
    @Override
    public List<Task> getCompletedTasks() {
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
    public List<Task> getAvailableTasks() {
        if (this.init) {
            this.init = false;
            this.updateKillStats();
        }
        return this.availableTasks;
    }

    @Nonnull
    public List<Task> getCompletableTasks() {
        return this.getAvailableTasks().stream().filter(this::canCompleteTask).collect(Collectors.toList());
    }

    @Override
    @Nonnull
    public IPlayableFaction<?> getFaction() {
        return faction;
    }

    @Override
    public boolean canCompleteTask(Task task) {
        for (TaskRequirement requirement : task.getRequirements()) {
            if (requirement.getType().equals(TaskRequirement.Type.STATS)) {
                if (this.player.getEntityWorld().isRemote()) return false;
                int amount = ((StatRequirement<?>) requirement).getAmount();
                if (((ServerPlayerEntity) this.player).getStats().getValue(((StatRequirement<?>) requirement).getStat()) < stats.get(task).get(((StatRequirement<?>) requirement).getStat()) + amount)
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
        this.stats.clear();
    }

    /**
     * cannot be called in constructor, because player is not constructed jet
     */
    private void updateKillStats() {
        if (player.getEntityWorld().isRemote()) return;
        for (Task task : this.availableTasks) {
            if (this.stats.containsKey(task)) continue;
            Map<Stat<?>, Integer> stats = null;
            for (TaskRequirement requirement : task.getRequirements()) {
                if (!requirement.getType().equals(TaskRequirement.Type.STATS)) continue;
                if (stats == null) {
                    stats = Maps.newHashMap();
                }
                stats.put(((StatRequirement<?>) requirement).getStat(), ((ServerPlayerEntity) this.player).getStats().getValue(((StatRequirement<?>) requirement).getStat()));
            }
            if (stats != null) {
                this.stats.put(task, stats);
            }
        }
    }

    public void writeNBT(CompoundNBT compoundNBT) {
        //tasks
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
                for (int i = 0; i < task.getRequirements().size(); i++) {
                    if (task.getRequirements().get(i).getType() == TaskRequirement.Type.ITEMS) return;
                    StatRequirement<?> requirement = ((StatRequirement<?>) task.getRequirements().get(i));
                    CompoundNBT statNBT = new CompoundNBT();
                    statNBT.putString("statType", requirement.getStatsType().getRegistryName().toString());
                    statNBT.putString("type", requirement.getStatType().getRegistryName().toString());
                    statNBT.putInt("amount", this.stats.get(task).get(requirement.getStat()));
                    statsNBT.put("requirement" + i, statNBT);
                }
                stats.put(task.getRegistryName().toString(), statsNBT);
            });
            compoundNBT.put("stats", stats);
        }
    }

    public <T> void readNBT(CompoundNBT compoundNBT) {
        //tasks
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
                    CompoundNBT statsNBT = tasks.getCompound(taskId);
                    statsNBT.keySet().forEach(requirement -> {
                        CompoundNBT statNBT = statsNBT.getCompound(requirement);
                        //noinspection unchecked
                        StatType<T> statType = (StatType<T>) ForgeRegistries.STAT_TYPES.getValue(new ResourceLocation(statNBT.getString("statType")));
                        if (statType == null) return;
                        Optional<T> type = statType.getRegistry().getValue(new ResourceLocation(statNBT.getString("type")));
                        if (type.isPresent()) {
                            Stat<T> stat = statType.get(type.get());
                            int amount = statNBT.getInt("amount");
                            this.stats.get(task).put(stat, amount);
                        }
                    });
                }
            });
        }
    }

}
