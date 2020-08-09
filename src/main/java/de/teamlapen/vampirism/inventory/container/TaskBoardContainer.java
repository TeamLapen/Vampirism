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


public class TaskBoardContainer extends Container {

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
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return FactionPlayerHandler.getOpt(playerIn).map(player -> player.getCurrentFaction() != null).orElse(false);
    }

    public boolean canCompleteTask(Task task) {
        return this.completableTasks.contains(task);
    }

    public boolean isTaskNotAccepted(Task task) {
        return this.notAcceptedTasks.contains(task);
    }

    public boolean isRequirementCompleted(Task task, TaskRequirement.Requirement<?> requirement) {
        if (this.isCompleted(task)) return true;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(task)) {
                return this.completedRequirements.get(task).containsKey(requirement.getId()) && this.completedRequirements.get(task).get(requirement.getId()) >= requirement.getAmount(this.factionPlayer);
            }
        }
        return false;
    }

    public boolean areRequirementsCompleted(Task task, TaskRequirement.Type type) {
        if (this.isCompleted(task)) return true;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(task)) {
                for (TaskRequirement.Requirement<?> requirement : task.getRequirement().requirements().get(type)) {
                    if (!this.completedRequirements.get(task).containsKey(requirement.getId()) || this.completedRequirements.get(task).get(requirement.getId()) < requirement.getAmount(this.factionPlayer)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int getRequirementStatus(Task task, TaskRequirement.Requirement<?> requirement) {
        assert this.completedRequirements != null;
        if (this.completedRequirements.containsKey(task)) {
            return this.completedRequirements.get(task).get(requirement.getId());
        } else {
            return requirement.getAmount(this.factionPlayer);
        }
    }

    public void completeTask(Task task) {
        if (this.canCompleteTask(task)) {
            VampirismMod.dispatcher.sendToServer(new TaskActionPacket(task, taskBoardId, TaskAction.COMPLETE));
            this.completedTasks.add(task);
            this.completableTasks.remove(task);
        }
    }

    public void acceptTask(Task task) {
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(task, taskBoardId, TaskAction.ACCEPT));
        this.notAcceptedTasks.remove(task);
    }

    public void abortTask(Task task) {
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(task, taskBoardId, TaskAction.ABORT));
        this.notAcceptedTasks.add(task);
    }

    public boolean isCompleted(Task task) {
        return this.completedTasks.contains(task);
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

    public enum TaskAction {
        COMPLETE, ACCEPT, ABORT
    }
}
