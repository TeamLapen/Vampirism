package de.teamlapen.vampirism.inventory;

import com.google.common.collect.Sets;
import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.network.ServerboundTaskActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class TaskBoardMenu extends AbstractContainerMenu implements TaskMenu {

    /**
     * all tasks that can be completed by the player
     */
    @NotNull
    private final Set<UUID> completableTasks = Sets.newHashSet();
    @NotNull
    private final List<ITaskInstance> taskInstances = new ArrayList<>();
    @NotNull
    private final TextColor factionColor;
    @NotNull
    private final IFactionPlayer<?> factionPlayer;
    /**
     * all task requirements that are completed
     */
    @Nullable
    private Map<UUID, Map<ResourceLocation, Integer>> completedRequirements;
    private UUID taskBoardId;

    @Nullable
    private Runnable listener;
    private final Registry<Task> registry;

    public TaskBoardMenu(int id, @NotNull Inventory playerInventory) {
        super(ModContainer.TASK_MASTER.get(), id);
        this.factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(playerInventory.player).orElseThrow(() -> new IllegalStateException("Can't open container without faction"));
        this.factionColor = this.factionPlayer.getFaction().getChatColor();
        this.registry = playerInventory.player.level.registryAccess().registryOrThrow(VampirismRegistries.TASK_ID);
    }

    @Override
    public Registry<Task> getRegistry() {
        return this.registry;
    }

    @Override
    public boolean areRequirementsCompleted(@NotNull ITaskInstance task, @NotNull TaskRequirement.Type type) {
        if (task.isCompleted()) return true;
        if (this.completableTasks.contains(task.getId())) return true;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(task.getId())) {
                for (TaskRequirement.Requirement<?> requirement : getTask(task.getTask()).getRequirement().requirements().get(type)) {
                    if (!this.completedRequirements.get(task.getId()).containsKey(requirement.id()) || this.completedRequirements.get(task.getId()).get(requirement.id()) < requirement.getAmount(this.factionPlayer)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull TaskAction buttonAction(@NotNull ITaskInstance taskInfo) {
        if (canCompleteTask(taskInfo)) {
            return TaskAction.COMPLETE;
        } else if (isTaskNotAccepted(taskInfo)) {
            return TaskAction.ACCEPT;
        } else if (!taskInfo.isUnique(this.registry) && this.factionPlayer.getRepresentingPlayer().level.getGameTime() > taskInfo.getTaskTimeStamp()) {
            return TaskAction.REMOVE;
        } else {
            return TaskAction.ABORT;
        }
    }

    @Override
    public boolean canCompleteTask(@NotNull ITaskInstance taskInfo) {
        return this.completableTasks.contains(taskInfo.getId()) && (taskInfo.isUnique(this.registry) || this.factionPlayer.getRepresentingPlayer().level.getGameTime() < taskInfo.getTaskTimeStamp());
    }

    @Override
    public void pressButton(@NotNull ITaskInstance taskInfo) {
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
        VampirismMod.dispatcher.sendToServer(new ServerboundTaskActionPacket(taskInfo.getId(), taskInfo.getTaskBoard(), action));
        if (this.listener != null) {
            this.listener.run();
        }
    }

    @NotNull
    public IPlayableFaction<?> getFaction() {
        return this.factionPlayer.getFaction();
    }

    @NotNull
    public TextColor getFactionColor() {
        return this.factionColor;
    }

    @Override
    public int getRequirementStatus(@NotNull ITaskInstance taskInfo, @NotNull TaskRequirement.Requirement<?> requirement) {
        assert this.completedRequirements != null;
        if (this.completedRequirements.containsKey(taskInfo.getId())) {
            return this.completedRequirements.get(taskInfo.getId()).get(requirement.id());
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

    @NotNull
    public List<ITaskInstance> getVisibleTasks() {
        return this.taskInstances;
    }

    /**
     * @param completedRequirements updated completed requirements
     */
    @OnlyIn(Dist.CLIENT)
    public void init(@NotNull Set<ITaskInstance> available, @NotNull Set<UUID> completableTasks, Map<UUID, Map<ResourceLocation, Integer>> completedRequirements, UUID taskBoardId) {
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
    public boolean isCompleted(@NotNull ITaskInstance item) {
        return item.isCompleted();
    }

    @Override
    public boolean isRequirementCompleted(@NotNull ITaskInstance taskInfo, @NotNull TaskRequirement.Requirement<?> requirement) {
        if (taskInfo.isCompleted()) return true;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(taskInfo.getId())) {
                return this.completedRequirements.get(taskInfo.getId()).containsKey(requirement.id()) && this.completedRequirements.get(taskInfo.getId()).get(requirement.id()) >= requirement.getAmount(this.factionPlayer);
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
    public boolean stillValid(@NotNull Player playerIn) {
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
