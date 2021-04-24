package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


public class TaskBoardContainer extends Container implements TaskContainer {

    /**
     * all tasks that can be completed by the player
     */
    @Nonnull
    private final Set<UUID> completableTasks = Sets.newHashSet();
    @Nonnull
    private final List<ITaskInstance> taskInstances = new ArrayList<>();
    @Nonnull
    private final TextFormatting factionColor;
    @Nonnull
    private final IFactionPlayer<?> factionPlayer;
    /**
     * all task requirements that are completed
     */
    @Nullable
    private Map<UUID, Map<ResourceLocation, Integer>> completedRequirements;
    private UUID taskBoardId;

    @Nullable
    private Runnable listener;

    public TaskBoardContainer(int id, PlayerInventory playerInventory) {
        super(ModContainer.task_master, id);
        //noinspection OptionalGetWithoutIsPresent
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get();
        this.factionColor = this.factionPlayer.getFaction().getChatColor();
    }

    /**
     * @param completedRequirements updated completed requirements
     */
    @OnlyIn(Dist.CLIENT)
    public void init(@Nonnull Set<ITaskInstance> available, Set<UUID> completableTasks, Map<UUID, Map<ResourceLocation, Integer>> completedRequirements, UUID taskBoardId) {
        this.taskInstances.clear();
        this.taskInstances.addAll(available);
        this.completableTasks.addAll(completableTasks);
        this.completedRequirements = completedRequirements;
        this.taskBoardId = taskBoardId;
        if (this.listener != null) {
            this.listener.run();
        }
    }

    @Override
    public void setReloadListener(@Nullable Runnable listener) {
        this.listener = listener;
    }

    public UUID getTaskBoardId() {
        return taskBoardId;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return FactionPlayerHandler.getOpt(playerIn).map(player -> player.getCurrentFaction() != null).orElse(false);
    }

    public int size() {
        return this.taskInstances.size();
    }

    @Nonnull
    public List<ITaskInstance> getVisibleTasks() {
        return this.taskInstances;
    }

    public ITaskInstance getTask(int i) {
        return this.taskInstances.get(i);
    }

    @Nonnull
    public TextFormatting getFactionColor() {
        return this.factionColor;
    }

    @Nonnull
    public IPlayableFaction<?> getFaction() {
        return this.factionPlayer.getFaction();
    }

    @Override
    public boolean canCompleteTask(@Nonnull ITaskInstance taskInfo) {
        return this.completableTasks.contains(taskInfo.getId()) && this.factionPlayer.getRepresentingPlayer().world.getGameTime() < taskInfo.getTaskTimeStamp();
    }

    @Override
    public void pressButton(@Nonnull ITaskInstance taskInfo) {
        TaskAction action = buttonAction(taskInfo);
        switch (action) {
            case COMPLETE:
                taskInfo.complete();
                this.completableTasks.remove(taskInfo.getId());
                this.taskInstances.remove(taskInfo);
                break;
            case ACCEPT:
                taskInfo.startTask(Minecraft.getInstance().world.getGameTime() + taskInfo.getTaskDuration());
                break;
            default:
                taskInfo.aboardTask();
                break;
        }
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(taskInfo.getId(), taskInfo.getTaskBoard(), action));
        if (this.listener != null) {
            this.listener.run();
        }
    }

    @Override
    public TaskAction buttonAction(@Nonnull ITaskInstance taskInfo) {
        if (canCompleteTask(taskInfo)) {
            return TaskAction.COMPLETE;
        } else if (isTaskNotAccepted(taskInfo)) {
            return TaskAction.ACCEPT;
        } else if (!taskInfo.isUnique() && this.factionPlayer.getRepresentingPlayer().world.getGameTime() > taskInfo.getTaskTimeStamp()) {
            return TaskAction.REMOVE;
        } else {
            return TaskAction.ABORT;
        }
    }

    @Override
    public boolean isCompleted(@Nonnull ITaskInstance item) {
        return item.isCompleted();
    }

    @Override
    public boolean areRequirementsCompleted(@Nonnull ITaskInstance task, @Nonnull TaskRequirement.Type type) {
        if (task.isCompleted()) return true;
        if (this.completableTasks.contains(task.getId())) return true;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(task.getId())) {
                for (TaskRequirement.Requirement<?> requirement : task.getTask().getRequirement().requirements().get(type)) {
                    if (!this.completedRequirements.get(task.getId()).containsKey(requirement.getId()) || this.completedRequirements.get(task.getId()).get(requirement.getId()) < requirement.getAmount(this.factionPlayer)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRequirementStatus(@Nonnull ITaskInstance taskInfo, @Nonnull TaskRequirement.Requirement<?> requirement) {
        assert this.completedRequirements != null;
        if (this.completedRequirements.containsKey(taskInfo.getId())) {
            return this.completedRequirements.get(taskInfo.getId()).get(requirement.getId());
        } else {
            return requirement.getAmount(this.factionPlayer);
        }
    }

    @Override
    public boolean isRequirementCompleted(@Nonnull ITaskInstance taskInfo, @Nonnull TaskRequirement.Requirement<?> requirement) {
        if (taskInfo.isCompleted()) return true;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(taskInfo.getId())) {
                return this.completedRequirements.get(taskInfo.getId()).containsKey(requirement.getId()) && this.completedRequirements.get(taskInfo.getId()).get(requirement.getId()) >= requirement.getAmount(this.factionPlayer);
            }
        }
        return false;
    }

}
