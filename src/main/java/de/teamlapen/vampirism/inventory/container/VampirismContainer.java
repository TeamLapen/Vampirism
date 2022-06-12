package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.network.TaskActionPacket;
import de.teamlapen.vampirism.player.TaskManager;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VampirismContainer extends InventoryContainer implements TaskContainer {

    private static final Function<Player, SelectorInfo[]> SELECTOR_INFOS = player -> {
        IPlayableFaction<?> faction = FactionPlayerHandler.getCurrentFactionPlayer(player).orElseThrow(() -> new IllegalStateException("Opening vampirism container without faction")).getFaction();
        return new SelectorInfo[]{
                new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.AMULET && ((IRefinementItem) stack.getItem()).getExclusiveFaction(stack).equals(faction), 58, 8),
                new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.RING && ((IRefinementItem) stack.getItem()).getExclusiveFaction(stack).equals(faction), 58, 26),
                new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.OBI_BELT && ((IRefinementItem) stack.getItem()).getExclusiveFaction(stack).equals(faction), 58, 44)};
    };
    private final IFactionPlayer<?> factionPlayer;
    private final TextColor factionColor;
    private final NonNullList<ItemStack> refinementStacks = NonNullList.withSize(3, ItemStack.EMPTY);
    public Map<UUID, TaskManager.TaskWrapper> taskWrapper = new HashMap<>();
    public Map<UUID, Set<UUID>> completableTasks = new HashMap<>();
    public Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements = new HashMap<>();
    private Runnable listener;
    private final boolean refinementsAvailable;

    public VampirismContainer(int id, @Nonnull Inventory playerInventory) {
        super(ModContainer.VAMPIRISM.get(), id, playerInventory, ContainerLevelAccess.NULL, new SimpleContainer(3), RemovingSelectorSlot::new, SELECTOR_INFOS.apply(playerInventory.player));
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().orElseThrow(() -> new IllegalStateException("Opening vampirism container without faction"));
        this.factionColor = factionPlayer.getFaction().getChatColor();
        this.refinementsAvailable = factionPlayer.getFaction().hasRefinements();
        this.addPlayerSlots(playerInventory, 37, 124);
        ItemStack[] sets = this.factionPlayer.getSkillHandler().createRefinementItems();
        for (int i = 0; i < sets.length; i++) {
            if (sets[i] != null) {
                this.refinementStacks.set(i, sets[i]);
            }
        }
    }

    @Override
    public boolean areRequirementsCompleted(@Nonnull ITaskInstance taskInfo, @Nonnull TaskRequirement.Type type) {
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(taskInfo.getTaskBoard()) && this.completedRequirements.get(taskInfo.getTaskBoard()).containsKey(taskInfo.getId())) {
                Map<ResourceLocation, Integer> data = this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId());
                for (TaskRequirement.Requirement<?> requirement : taskInfo.getTask().getRequirement().requirements().get(type)) {
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
    public TaskAction buttonAction(@Nonnull ITaskInstance taskInfo) {
        return taskInfo.isUnique() || this.factionPlayer.getRepresentingPlayer().level.getGameTime() < taskInfo.getTaskTimeStamp() ? TaskContainer.TaskAction.ABORT : TaskAction.REMOVE;
    }

    @Override
    public boolean canCompleteTask(@Nonnull ITaskInstance taskInfo) {
        return this.completableTasks.containsKey(taskInfo.getTaskBoard()) && this.completableTasks.get(taskInfo.getTaskBoard()).contains(taskInfo.getId()) && (taskInfo.isUnique() || this.factionPlayer.getRepresentingPlayer().level.getGameTime() < taskInfo.getTaskTimeStamp());
    }

    @Override
    public TextColor getFactionColor() {
        return this.factionColor;
    }

    public NonNullList<ItemStack> getRefinementStacks() {
        return refinementStacks;
    }

    @Override
    protected boolean isSlotEnabled(int id) {
        return refinementsAvailable;
    }

    public boolean areRefinementsAvailable() {
        return refinementsAvailable;
    }

    @Override
    public int getRequirementStatus(@Nonnull ITaskInstance taskInfo, @Nonnull TaskRequirement.Requirement<?> requirement) {
        assert this.completedRequirements != null;
        if (this.completedRequirements.containsKey(taskInfo.getTaskBoard())) {
            return this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId()).get(requirement.getId());
        } else {
            return requirement.getAmount(this.factionPlayer);
        }
    }

    public Collection<ITaskInstance> getTaskInfos() {
        return this.taskWrapper.values().stream().flatMap(t -> t.getTaskInstances().stream().filter(ITaskInstance::isAccepted)).collect(Collectors.toList());
    }

    @OnlyIn(Dist.CLIENT)
    public void init(@Nonnull Map<UUID, TaskManager.TaskWrapper> taskWrapper, @Nonnull Map<UUID, Set<UUID>> completableTasks, @Nonnull Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements) {
        this.taskWrapper = taskWrapper;
        this.completedRequirements = completedRequirements;
        this.completableTasks = completableTasks;
        if (this.listener != null) {
            this.listener.run();
        }
    }

    @Override
    public boolean isCompleted(@Nonnull ITaskInstance item) {
        return false;
    }

    @Override
    public boolean isRequirementCompleted(@Nonnull ITaskInstance taskInfo, @Nonnull TaskRequirement.Requirement<?> requirement) {
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(taskInfo.getTaskBoard()) && this.completedRequirements.get(taskInfo.getTaskBoard()).containsKey(taskInfo.getId())) {
                Map<ResourceLocation, Integer> data = this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId());
                return data.containsKey(requirement.getId()) && data.get(requirement.getId()) >= requirement.getAmount(this.factionPlayer);
            }
        }
        return false;
    }

    @Override
    public boolean isTaskNotAccepted(@Nonnull ITaskInstance taskInfo) {
        return false;
    }

    @Override
    public void pressButton(@Nonnull ITaskInstance taskInfo) {
        VampirismMod.dispatcher.sendToServer(new TaskActionPacket(taskInfo.getId(), taskInfo.getTaskBoard(), buttonAction(taskInfo)));
        this.taskWrapper.get(taskInfo.getTaskBoard()).removeTask(taskInfo, true);
        if (this.listener != null) {
            this.listener.run();
        }
    }

    public void setRefinement(int slot, @Nonnull ItemStack stack) {
        this.factionPlayer.getSkillHandler().equipRefinementItem(stack);
        this.refinementStacks.set(slot, stack);
    }

    @Override
    public void setReloadListener(@Nullable Runnable listener) {
        this.listener = listener;
    }

    public static class RemovingSelectorSlot extends SelectorSlot {

        private RemovingSelectorSlot(Container inventoryIn, int index, SelectorInfo info, Consumer<Container> refreshInvFunc, Function<Integer, Boolean> activeFunc) {
            super(inventoryIn, index, info, refreshInvFunc, activeFunc);
        }

        @Override
        public void set(@Nonnull ItemStack stack) {
            if (!stack.isEmpty()) {
                ((VampirismContainer) this.getContainer()).setRefinement(this.getSlotIndex(), stack);
            }
        }
    }

}
