package de.teamlapen.factions.common.inventory;

import de.teamlapen.factions.FactionsMod;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.items.IRefinementItem;
import de.teamlapen.factions.api.refinements.IRefinementPlayer;
import de.teamlapen.factions.api.tasks.ITaskInstance;
import de.teamlapen.factions.api.tasks.Task;
import de.teamlapen.factions.api.tasks.TaskRequirement;
import de.teamlapen.factions.common.core.FactionMenus;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.inventory.base.AbstractInventoryContainer;
import de.teamlapen.factions.common.network.packets.server.ServerboundTaskActionPacket;
import de.teamlapen.factions.common.tasks.TaskManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FactionMenu extends AbstractInventoryContainer implements ITaskMenu {

    private final IFactionPlayer<?> factionPlayer;
    private final TextColor factionColor;
    private final NonNullList<ItemStack> refinementStacks;
    public @NotNull Map<UUID, TaskManager.TaskWrapper> taskWrappers = new HashMap<>();
    public @NotNull Map<UUID, Set<UUID>> completableTasks = new HashMap<>();
    public @NotNull Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements = new HashMap<>();
    private @Nullable Runnable listener;
    private final boolean refinementsAvailable;
    private final Registry<Task> registry;

    public FactionMenu(int id, @NotNull Inventory inventory) {
        super(FactionMenus.FACTION_MENU.get(), id, inventory, ContainerLevelAccess.NULL, createSlotDefinitions(inventory.player));
        this.factionPlayer = FactionPlayerHandler.get(inventory.player).factionPlayer();
        this.factionColor = factionPlayer.getFaction().value().getChatColor();
        this.refinementsAvailable = factionPlayer.getFaction().value().hasRefinements();
        this.addPlayerInventorySlots(inventory, 37, 124);
        this.refinementStacks = this.factionPlayer instanceof IRefinementPlayer<?> refinementPlayer ? refinementPlayer.getRefinementHandler().getRefinementItems() : NonNullList.create();
        this.registry = inventory.player.level().registryAccess().lookupOrThrow(FactionRegistries.Keys.TASK);
    }

    private static List<SlotDefinition> createSlotDefinitions(Player player) {
        Holder<? extends IPlayableFaction<?>> faction = FactionPlayerHandler.get(player).getFaction();
        if (IFaction.isNeutral(faction)) throw new IllegalStateException("Cannot open menu without faction");
        if (!faction.value().hasRefinements()) return List.of();

        Predicate<ItemStack> refinement = stack -> stack.getItem() instanceof IRefinementItem item && IFaction.contains(item.getExclusiveFactions(stack), faction);

        return List.of(
                new SlotDefinition(refinement.and(stack -> ((IRefinementItem)stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.AMULET), 58, 8),
                new SlotDefinition(refinement.and(stack -> ((IRefinementItem)stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.RING), 58, 26),
                new SlotDefinition(refinement.and(stack -> ((IRefinementItem)stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.OBI_BELT), 58, 44)
        );
    }

    @Override
    public boolean showLocateTaskmaster() {
        return true;
    }

    @Override
    public BlockPos getLastKnownPosition(ITaskInstance instance) {
        var taskWrapper = this.taskWrappers.get(instance.getTaskBoard());
        if (taskWrapper != null) {
            return taskWrapper.getLastSeenPos().orElse(null);
        }
        return null;
    }

    @Override
    protected Slot createSlot(Container container, int index, SlotDefinition definition) {
        return new RemovingSelectorSlot(container, index, definition, this::isSlotEnabled);
    }

    @Override
    protected void onInputSlotChanged(Container container) {
        super.onInputSlotChanged(container);
        if (this.factionPlayer instanceof IRefinementPlayer<?> refinementPlayer) {
            refinementPlayer.getRefinementHandler().updateItems();
        }
    }

    @Override
    protected Container createInputContainer(int size) {
        return new RefinementContainer();
    }

    @Override
    public Registry<Task> getRegistry() {
        return this.registry;
    }

    @Override
    public boolean areRequirementsCompleted(@NotNull ITaskInstance taskInfo, @NotNull TaskRequirement.Type type) {
        if (this.completedRequirements.containsKey(taskInfo.getTaskBoard()) && this.completedRequirements.get(taskInfo.getTaskBoard()).containsKey(taskInfo.getId())) {
            Map<ResourceLocation, Integer> data = this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId());
            for (TaskRequirement.Requirement<?> requirement : getTask(taskInfo.getTask()).requirements().requirements().get(type)) {
                if (!data.containsKey(requirement.id()) || data.get(requirement.id()) < requirement.getAmount(this.factionPlayer)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public @NotNull TaskAction buttonAction(@NotNull ITaskInstance taskInfo) {
        return taskInfo.isUnique(this.registry) || this.factionPlayer.asEntity().level().getGameTime() < taskInfo.getTaskTimeStamp() ? TaskAction.ABORT : TaskAction.REMOVE;
    }

    @Override
    public boolean canCompleteTask(@NotNull ITaskInstance taskInfo) {
        return this.completableTasks.containsKey(taskInfo.getTaskBoard()) && this.completableTasks.get(taskInfo.getTaskBoard()).contains(taskInfo.getId()) && (taskInfo.isUnique(this.registry) || this.factionPlayer.asEntity().level().getGameTime() < taskInfo.getTaskTimeStamp());
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
    public int getRequirementStatus(@NotNull ITaskInstance taskInfo, @NotNull TaskRequirement.Requirement<?> requirement) {
        if (this.completedRequirements.containsKey(taskInfo.getTaskBoard())) {
            return this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId()).get(requirement.id());
        } else {
            return requirement.getAmount(this.factionPlayer);
        }
    }

    public @NotNull Collection<ITaskInstance> getTaskInfos() {
        return this.taskWrappers.values().stream().flatMap(t -> t.getTaskInstances().stream().filter(ITaskInstance::isAccepted)).collect(Collectors.toList());
    }

    public void init(@NotNull Map<UUID, TaskManager.TaskWrapper> taskWrapper, @NotNull Map<UUID, Set<UUID>> completableTasks, @NotNull Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements) {
        this.taskWrappers = taskWrapper;
        this.completedRequirements = completedRequirements;
        this.completableTasks = completableTasks;
        if (this.listener != null) {
            this.listener.run();
        }
    }

    @Override
    public boolean isCompleted(@NotNull ITaskInstance item) {
        return false;
    }

    @Override
    public boolean isRequirementCompleted(@NotNull ITaskInstance taskInfo, @NotNull TaskRequirement.Requirement<?> requirement) {
        if (this.completedRequirements.containsKey(taskInfo.getTaskBoard()) && this.completedRequirements.get(taskInfo.getTaskBoard()).containsKey(taskInfo.getId())) {
            Map<ResourceLocation, Integer> data = this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId());
            return data.containsKey(requirement.id()) && data.get(requirement.id()) >= requirement.getAmount(this.factionPlayer);
        }
        return false;
    }

    @Override
    public boolean isTaskNotAccepted(@NotNull ITaskInstance taskInfo) {
        return false;
    }

    @Override
    public void pressButton(@NotNull ITaskInstance taskInfo) {
        FactionsMod.proxy.sendToServer(new ServerboundTaskActionPacket(taskInfo.getId(), taskInfo.getTaskBoard(), buttonAction(taskInfo)));
        this.taskWrappers.get(taskInfo.getTaskBoard()).removeTask(taskInfo, true);
        if (this.listener != null) {
            this.listener.run();
        }
    }

    @Override
    public void setReloadListener(@Nullable Runnable listener) {
        this.listener = listener;
    }

    public static class RemovingSelectorSlot extends InputSlot {

        public RemovingSelectorSlot(Container container, int index, SlotDefinition definition, Predicate<Integer> activeCheck) {
            super(container, index, definition, activeCheck);
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            if (!stack.isEmpty()) {
                this.container.setItem(this.getSlotIndex(), stack);
            }
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            return false;
        }
    }

    private class RefinementContainer implements Container {

        @Override
        public int getContainerSize() {
            return refinementStacks.size();
        }

        @Override
        public boolean isEmpty() {
            return refinementStacks.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
                return refinementStacks.get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return refinementStacks.remove(slot);
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
                return refinementStacks.remove(slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            refinementStacks.set(slot, stack);
        }

        @Override
        public void setChanged() {
            FactionMenu.this.slotsChanged(this);
        }

        @Override
        public boolean stillValid(@NotNull Player player) {
            return true;
        }

        @Override
        public void clearContent() {
        }
    }

}
