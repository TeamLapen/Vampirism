package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.client.gui.TaskBoardScreen;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskActionPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class TaskBoardContainer extends Container implements TaskContainer {

    /**
     * all tasks that can be completed by the player
     */
    @Nonnull
    private final Set<Task> completableTasks = Sets.newHashSet();
    /**
     * all tasks that have been completed and should not be displayed
     */
    @Nonnull
    private final Set<Task> completedTasks = Sets.newHashSet();
    /**
     * all tasks that should be displayed in the {@link TaskBoardScreen}
     */
    @Nonnull
    private final List<Task> visibleTasks = Lists.newArrayList();
    @Nonnull
    private final Set<Task> notAcceptedTasks = Sets.newHashSet();
    @Nonnull
    private final TextFormatting factionColor;
    @Nonnull
    private final IFactionPlayer<?> factionPlayer;
    /**
     * all task requirements that are completed
     */
    @Nullable
    private Map<Task, Map<ResourceLocation, Integer>> completedRequirements;
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
     * @param completableTasks      updated possible tasks
     * @param visibleTasks          updated unlocked tasks
     * @param notAcceptedTasks      updated not accepted tasks
     * @param completedRequirements updated completed requirements
     */
    @OnlyIn(Dist.CLIENT)
    public void init(@Nonnull Set<Task> completableTasks, @Nonnull List<Task> visibleTasks, @Nonnull Set<Task> notAcceptedTasks, @Nonnull Map<Task, Map<ResourceLocation, Integer>> completedRequirements, UUID taskBoardId) {
        this.completableTasks.addAll(completableTasks);
        this.visibleTasks.addAll(visibleTasks.stream().filter(task -> !this.visibleTasks.contains(task)).sorted((task1, task2) -> (this.completableTasks.contains(task1) && !this.completableTasks.contains(task2)) || (!completableTasks.contains(task1) && !this.completedTasks.contains(task1) && this.completedTasks.contains(task2)) ? -1 : 0).collect(Collectors.toList()));
        this.completedRequirements = completedRequirements;
        this.taskBoardId = taskBoardId;
        this.notAcceptedTasks.addAll(notAcceptedTasks);
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

    public void completeTask(TaskInfo taskInfo) {
        if (this.completableTasks.contains(taskInfo.task)) {
            VampirismMod.dispatcher.sendToServer(new TaskActionPacket(taskInfo.task, taskInfo.taskBoard, TaskContainer.TaskAction.COMPLETE));
            this.completedTasks.add(taskInfo.task);
            this.completableTasks.remove(taskInfo.task);
        }
    }

    public void acceptTask(TaskInfo taskInfo) {
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(taskInfo.task, taskInfo.taskBoard, TaskContainer.TaskAction.ACCEPT));
        this.notAcceptedTasks.remove(taskInfo.task);
    }

    public void abortTask(TaskInfo taskInfo) {
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(taskInfo.task, taskInfo.taskBoard, TaskContainer.TaskAction.ABORT));
        this.notAcceptedTasks.add(taskInfo.task);
    }

    public int size() {
        return this.visibleTasks.size();
    }

    @Nonnull
    public List<Task> getVisibleTasks() {
        return this.visibleTasks;
    }

    public Task getTask(int i) {
        return this.visibleTasks.get(i);
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
    public boolean isTaskNotAccepted(TaskInfo taskInfo) {
        return this.notAcceptedTasks.contains(taskInfo.task);
    }

    @Override
    public boolean canCompleteTask(TaskInfo taskInfo) {
        return this.completableTasks.contains(taskInfo.task);
    }

    @Override
    public boolean pressButton(TaskInfo taskInfo) {
        switch (buttonAction(taskInfo)) {
            case COMPLETE:
                completeTask(taskInfo);
                break;
            case ABORT:
                abortTask(taskInfo);
                break;
            case ACCEPT:
                acceptTask(taskInfo);
                break;
        }
        return true;
    }

    @Override
    public TaskAction buttonAction(TaskInfo taskInfo) {
        if (canCompleteTask(taskInfo)) {
            return TaskContainer.TaskAction.COMPLETE;
        } else if (isTaskNotAccepted(taskInfo)) {
            return TaskContainer.TaskAction.ACCEPT;
        } else {
            return TaskContainer.TaskAction.ABORT;
        }
    }

    @Override
    public boolean isCompleted(TaskInfo item) {
        return this.completedTasks.contains(item.task);
    }

    @Override
    public boolean areRequirementsCompleted(TaskInfo task, TaskRequirement.Type type) {
        if (this.completedTasks.contains(task.task)) return true;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(task.task)) {
                for (TaskRequirement.Requirement<?> requirement : task.task.getRequirement().requirements().get(type)) {
                    if (!this.completedRequirements.get(task.task).containsKey(requirement.getId()) || this.completedRequirements.get(task.task).get(requirement.getId()) < requirement.getAmount(this.factionPlayer)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRequirementStatus(TaskInfo taskInfo, TaskRequirement.Requirement<?> requirement) {
        assert this.completedRequirements != null;
        if (this.completedRequirements.containsKey(taskInfo.task)) {
            return this.completedRequirements.get(taskInfo.task).get(requirement.getId());
        } else {
            return requirement.getAmount(this.factionPlayer);
        }
    }

    @Override
    public boolean isRequirementCompleted(TaskInfo taskInfo, TaskRequirement.Requirement<?> requirement) {
        if (this.completedTasks.contains(taskInfo.task)) return true;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(taskInfo.task)) {
                return this.completedRequirements.get(taskInfo.task).containsKey(requirement.getId()) && this.completedRequirements.get(taskInfo.task).get(requirement.getId()) >= requirement.getAmount(this.factionPlayer);
            }
        }
        return false;
    }
}
