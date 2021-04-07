package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskActionPacket;
import de.teamlapen.vampirism.player.TaskManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class VampirismContainer extends InventoryContainer implements TaskContainer {

    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{new SelectorInfo(stack -> true, 58, 8), new SelectorInfo(stack -> true, 58, 26), new SelectorInfo(stack -> true, 58, 44)};

    public Map<UUID, Set<Task>> completableTasks = new HashMap<>();
    public Map<UUID, TaskManager.TaskBoardInfo> taskBoardInfos = new HashMap<>();
    public Map<UUID, Set<Task>> tasks = new HashMap<>();
    public Map<UUID, Map<Task, Map<ResourceLocation, Integer>>> completedRequirements = new HashMap<>();

    private final IFactionPlayer<?> factionPlayer;
    private final TextFormatting factionColor;

    private Runnable listener;

    private final NonNullList<ItemStack> refinementStacks = NonNullList.withSize(3, ItemStack.EMPTY);

    public VampirismContainer(int id, PlayerInventory playerInventory) {
        super(ModContainer.vampirism, id, playerInventory, IWorldPosCallable.DUMMY, new Inventory(3), RemovingSelectorSlot::new, SELECTOR_INFOS);
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().orElseThrow(() -> new IllegalStateException("Opening vampirism container without faction"));
        this.factionColor = factionPlayer.getFaction().getChatColor();
        this.addPlayerSlots(playerInventory, 37, 124);
    }

    @OnlyIn(Dist.CLIENT)
    public void init(@Nonnull Map<UUID, TaskManager.TaskBoardInfo> taskBoardInfos, @Nonnull Map<UUID, Set<Task>> tasks, @Nonnull Map<UUID, Set<Task>> completableTasks, @Nonnull Map<UUID, Map<Task, Map<ResourceLocation, Integer>>> completedRequirements) {
        this.taskBoardInfos = taskBoardInfos;
        this.tasks = tasks;
        this.completedRequirements = completedRequirements;
        this.completableTasks = completableTasks;
        if (this.listener != null) {
            this.listener.run();
        }
    }

    public void setRefinement(int slot, ItemStack stack) {
        this.refinementStacks.set(slot, stack);
    }

    public NonNullList<ItemStack> getRefinementStacks() {
        return refinementStacks;
    }

    @Override
    public void setReloadListener(@Nullable Runnable listener) {
        this.listener = listener;
    }


    @Override
    public boolean isTaskNotAccepted(TaskInfo taskInfo) {
        return false;
    }

    @Override
    public boolean canCompleteTask(TaskInfo taskInfo) {
        return this.completableTasks.containsKey(taskInfo.taskBoard) && this.completableTasks.get(taskInfo.taskBoard).contains(taskInfo.task);
    }

    @Override
    public boolean pressButton(TaskInfo taskInfo) {
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(taskInfo.task, taskInfo.taskBoard, TaskAction.ABORT));
        this.tasks.get(taskInfo.taskBoard).remove(taskInfo.task);
        if (this.listener != null) {
            this.listener.run();
        }
        return true;
    }

    @Override
    public TaskAction buttonAction(TaskInfo taskInfo) {
        return TaskContainer.TaskAction.ABORT;
    }

    @Override
    public boolean isCompleted(TaskInfo item) {
        return false;
    }

    @Override
    public TextFormatting getFactionColor() {
        return this.factionColor;
    }

    @Override
    public boolean areRequirementsCompleted(TaskInfo taskInfo, TaskRequirement.Type type) {
        Task task = taskInfo.task;
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(taskInfo.taskBoard) && this.completedRequirements.get(taskInfo.taskBoard).containsKey(task)) {
                Map<ResourceLocation, Integer> data = this.completedRequirements.get(taskInfo.taskBoard).get(task);
                for (TaskRequirement.Requirement<?> requirement : task.getRequirement().requirements().get(type)) {
                    if (!data.containsKey(requirement.getId()) || data.get(requirement.getId()) < requirement.getAmount(this.factionPlayer)) {
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
        if (this.completedRequirements.containsKey(taskInfo.taskBoard) && this.completedRequirements.get(taskInfo.taskBoard).containsKey(taskInfo.task)) {
            return this.completedRequirements.get(taskInfo.taskBoard).get(taskInfo.task).get(requirement.getId());
        } else {
            return requirement.getAmount(this.factionPlayer);
        }
    }

    @Override
    public boolean isRequirementCompleted(TaskInfo taskInfo, TaskRequirement.Requirement<?> requirement) {
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(taskInfo.taskBoard) && this.completedRequirements.get(taskInfo.taskBoard).containsKey(taskInfo.task)) {
                Map<ResourceLocation, Integer> data = this.completedRequirements.get(taskInfo.taskBoard).get(taskInfo.task);
                return data.containsKey(requirement.getId()) && data.get(requirement.getId()) >= requirement.getAmount(this.factionPlayer);
            }
        }
        return false;
    }

    private static class RemovingSelectorSlot extends SelectorSlot {

        public RemovingSelectorSlot(IInventory inventoryIn, int index, SelectorInfo info, Consumer<IInventory> refreshInvFunc, Function<Integer, Boolean> activeFunc) {
            super(inventoryIn, index, info, refreshInvFunc, activeFunc);
        }

        @Override
        public void putStack(@Nonnull ItemStack stack) {
            ((VampirismContainer) this.getS()).setRefinement(this.getSlotIndex(), stack);
        }
    }

}
