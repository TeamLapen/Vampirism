package de.teamlapen.vampirism.inventory.container;

import com.google.common.collect.Sets;
import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskActionPacket;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


public class TaskBoardContainer extends AbstractContainerMenu implements TaskContainer {

    /**
     * all tasks that can be completed by the player
     */
    @Nonnull
    private final Set<UUID> completableTasks = Sets.newHashSet();
    @Nonnull
    private final List<ITaskInstance> taskInstances = new ArrayList<>();
    @Nonnull
    private final TextColor factionColor;
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

    public TaskBoardContainer(int id, Inventory playerInventory) {
        super(ModContainer.TASK_MASTER.get(), id);
        this.factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(playerInventory.player).orElseThrow(() -> new IllegalStateException("Can't open container without faction"));
        this.factionColor = this.factionPlayer.getFaction().getChatColor();
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
    public TaskAction buttonAction(@Nonnull ITaskInstance taskInfo) {
        if (canCompleteTask(taskInfo)) {
            return TaskAction.COMPLETE;
        } else if (isTaskNotAccepted(taskInfo)) {
            return TaskAction.ACCEPT;
        } else if (!taskInfo.isUnique() && this.factionPlayer.getRepresentingPlayer().level.getGameTime() > taskInfo.getTaskTimeStamp()) {
            return TaskAction.REMOVE;
        } else {
            return TaskAction.ABORT;
        }
    }

    @Override
    public boolean canCompleteTask(@Nonnull ITaskInstance taskInfo) {
        return this.completableTasks.contains(taskInfo.getId()) && (taskInfo.isUnique() || this.factionPlayer.getRepresentingPlayer().level.getGameTime() < taskInfo.getTaskTimeStamp());
    }

    @Override
    public void pressButton(@Nonnull ITaskInstance taskInfo) {
        TaskAction action = buttonAction(taskInfo);
        switch (action) {
            case COMPLETE -> {
                taskInfo.complete();
                this.completableTasks.remove(taskInfo.getId());
                this.taskInstances.remove(taskInfo);
                VampLib.proxy.createMasterSoundReference(ModSounds.TASK_COMPLETE.get(), 1, 1).startPlaying();
            }
            case ACCEPT -> taskInfo.startTask(Minecraft.getInstance().level.getGameTime() + taskInfo.getTaskDuration());
            default -> taskInfo.aboardTask();
        }
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(taskInfo.getId(), taskInfo.getTaskBoard(), action));
        if (this.listener != null) {
            this.listener.run();
        }
    }

    @Nonnull
    public IPlayableFaction<?> getFaction() {
        return this.factionPlayer.getFaction();
    }

    @Nonnull
    public TextColor getFactionColor() {
        return this.factionColor;
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

    public ITaskInstance getTask(int i) {
        return this.taskInstances.get(i);
    }

    public UUID getTaskBoardId() {
        return taskBoardId;
    }

    @Nonnull
    public List<ITaskInstance> getVisibleTasks() {
        return this.taskInstances;
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
    public boolean isCompleted(@Nonnull ITaskInstance item) {
        return item.isCompleted();
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

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int p_38942_) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return VampirismPlayerAttributes.get(playerIn).faction != null;
    }

    @Override
    public void setReloadListener(@Nullable Runnable listener) {
        this.listener = listener;
    }

    public int size() {
        return this.taskInstances.size();
    }

}
