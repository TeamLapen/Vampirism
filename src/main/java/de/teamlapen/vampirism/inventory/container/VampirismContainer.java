package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.config.VampirismConfig;
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
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VampirismContainer extends InventoryContainer implements TaskContainer {

    private static final SelectorInfo[] SELECTOR_INFOS = new SelectorInfo[]{
            new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.AMULET, 58, 8),
            new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.RING, 58, 26),
            new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.OBI_BELT, 58, 44)};

    public Map<UUID, TaskManager.TaskWrapper> taskWrapper = new HashMap<>();
    public Map<UUID, Set<Task>> completableTasks = new HashMap<>();
    public Map<UUID, Map<Task, Map<ResourceLocation, Integer>>> completedRequirements = new HashMap<>();

    private final IFactionPlayer<?> factionPlayer;
    private final TextFormatting factionColor;

    private Runnable listener;
    private final World world;

    private final NonNullList<ItemStack> refinementStacks = NonNullList.withSize(3, ItemStack.EMPTY);

    public VampirismContainer(int id, PlayerInventory playerInventory) {
        super(ModContainer.vampirism, id, playerInventory, IWorldPosCallable.DUMMY, new Inventory(3), RemovingSelectorSlot::new, SELECTOR_INFOS);
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().orElseThrow(() -> new IllegalStateException("Opening vampirism container without faction"));
        this.factionColor = factionPlayer.getFaction().getChatColor();
        this.addPlayerSlots(playerInventory, 37, 124);
        ItemStack[] sets = this.factionPlayer.getSkillHandler().createRefinementItems();
        for (int i = 0; i < sets.length; i++) {
            if (sets[i] != null) {
                this.refinementStacks.set(i, sets[i]);
            }
        }
        this.world = playerInventory.player.world;
    }

    @OnlyIn(Dist.CLIENT)
    public void init(@Nonnull Map<UUID, TaskManager.TaskWrapper> taskWrapper, @Nonnull Map<UUID, Set<Task>> completableTasks, @Nonnull Map<UUID, Map<Task, Map<ResourceLocation, Integer>>> completedRequirements) {
        this.taskWrapper = taskWrapper;
        this.completedRequirements = completedRequirements;
        this.completableTasks = completableTasks;
        if (this.listener != null) {
            this.listener.run();
        }
    }

    public void setRefinement(int slot, ItemStack stack) {
        this.factionPlayer.getSkillHandler().equipRefinementItem(stack);
        this.refinementStacks.set(slot, stack);
    }

    public NonNullList<ItemStack> getRefinementStacks() {
        return refinementStacks;
    }

    public Collection<TaskInfo> getTaskInfos() {
        long targetTime = VampirismConfig.BALANCE.taskDuration.get() * 60 * 20;
        return this.taskWrapper.values().stream().flatMap(wrapper -> wrapper.getAcceptedTasks().stream().map(task -> new TaskInfo(task, wrapper.getId(), () -> targetTime - (this.world.getGameTime() - wrapper.getTaskTimeStamp(task))))).collect(Collectors.toList());
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
        return this.completableTasks.containsKey(taskInfo.taskBoard) && this.completableTasks.get(taskInfo.taskBoard).contains(taskInfo.task) && taskInfo.remainingTime.get() > 0;
    }

    @Override
    public boolean pressButton(TaskInfo taskInfo) {
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(taskInfo.task, taskInfo.taskBoard, buttonAction(taskInfo)));
        this.taskWrapper.get(taskInfo.taskBoard).removeTask(taskInfo.task, false);
        if (this.listener != null) {
            this.listener.run();
        }
        return true;
    }

    @Override
    public TaskAction buttonAction(TaskInfo taskInfo) {
        return taskInfo.task.isUnique() || taskInfo.remainingTime.get() > 0 ? TaskContainer.TaskAction.ABORT : TaskAction.REMOVE;
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
            if (!stack.isEmpty()) {
                ((VampirismContainer) this.getContainer()).setRefinement(this.getSlotIndex(), stack);
            }
        }
    }

}
