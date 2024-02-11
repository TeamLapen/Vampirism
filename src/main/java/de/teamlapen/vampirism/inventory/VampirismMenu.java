package de.teamlapen.vampirism.inventory;

import de.teamlapen.lib.lib.inventory.InventoryContainerMenu;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.player.TaskManager;
import de.teamlapen.vampirism.network.ServerboundTaskActionPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VampirismMenu extends InventoryContainerMenu implements TaskMenu {

    private static final Function<Player, SelectorInfo[]> SELECTOR_INFOS = player -> {
        IPlayableFaction<?> faction = FactionPlayerHandler.getCurrentFactionPlayer(player).orElseThrow(() -> new IllegalStateException("Opening vampirism container without faction")).getFaction();
        return new SelectorInfo[]{
                new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.AMULET && faction.equals(((IRefinementItem) stack.getItem()).getExclusiveFaction(stack)), 58, 8),
                new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.RING && faction.equals(((IRefinementItem) stack.getItem()).getExclusiveFaction(stack)), 58, 26),
                new SelectorInfo(stack -> stack.getItem() instanceof IRefinementItem && ((IRefinementItem) stack.getItem()).getSlotType() == IRefinementItem.AccessorySlotType.OBI_BELT && faction.equals(((IRefinementItem) stack.getItem()).getExclusiveFaction(stack)), 58, 44)};
    };
    private final IFactionPlayer<?> factionPlayer;
    private final TextColor factionColor;
    private final NonNullList<ItemStack> refinementStacks;
    public @NotNull Map<UUID, TaskManager.TaskWrapper> taskWrapper = new HashMap<>();
    public @NotNull Map<UUID, Set<UUID>> completableTasks = new HashMap<>();
    public @NotNull Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements = new HashMap<>();
    private @Nullable Runnable listener;
    private final boolean refinementsAvailable;
    private final Registry<Task> registry;

    public VampirismMenu(int id, @NotNull Inventory playerInventory) {
        super(ModContainer.VAMPIRISM.get(), id, playerInventory, ContainerLevelAccess.NULL, new SimpleContainer(3), RemovingSelectorSlot::new, SELECTOR_INFOS.apply(playerInventory.player));
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().orElseThrow(() -> new IllegalStateException("Opening vampirism container without faction"));
        this.factionColor = factionPlayer.getFaction().getChatColor();
        this.refinementsAvailable = factionPlayer.getFaction().hasRefinements();
        this.addPlayerSlots(playerInventory, 37, 124);
        this.refinementStacks = this.factionPlayer.getSkillHandler().getRefinementItems();
        this.registry = playerInventory.player.level().registryAccess().registryOrThrow(VampirismRegistries.TASK_ID);
    }

    @Override
    public Registry<Task> getRegistry() {
        return this.registry;
    }

    @Override
    public boolean areRequirementsCompleted(@NotNull ITaskInstance taskInfo, @NotNull TaskRequirement.Type type) {
        if (this.completedRequirements.containsKey(taskInfo.getTaskBoard()) && this.completedRequirements.get(taskInfo.getTaskBoard()).containsKey(taskInfo.getId())) {
            Map<ResourceLocation, Integer> data = this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId());
            for (TaskRequirement.Requirement<?> requirement : getTask(taskInfo.getTask()).getRequirement().requirements().get(type)) {
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
        return taskInfo.isUnique(this.registry) || this.factionPlayer.asEntity().level().getGameTime() < taskInfo.getTaskTimeStamp() ? TaskMenu.TaskAction.ABORT : TaskAction.REMOVE;
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
        assert this.completedRequirements != null;
        if (this.completedRequirements.containsKey(taskInfo.getTaskBoard())) {
            return this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId()).get(requirement.id());
        } else {
            return requirement.getAmount(this.factionPlayer);
        }
    }

    public @NotNull Collection<ITaskInstance> getTaskInfos() {
        return this.taskWrapper.values().stream().flatMap(t -> t.getTaskInstances().stream().filter(ITaskInstance::isAccepted)).collect(Collectors.toList());
    }

    public void init(@NotNull Map<UUID, TaskManager.TaskWrapper> taskWrapper, @NotNull Map<UUID, Set<UUID>> completableTasks, @NotNull Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements) {
        this.taskWrapper = taskWrapper;
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
        if (this.completedRequirements != null) {
            if (this.completedRequirements.containsKey(taskInfo.getTaskBoard()) && this.completedRequirements.get(taskInfo.getTaskBoard()).containsKey(taskInfo.getId())) {
                Map<ResourceLocation, Integer> data = this.completedRequirements.get(taskInfo.getTaskBoard()).get(taskInfo.getId());
                return data.containsKey(requirement.id()) && data.get(requirement.id()) >= requirement.getAmount(this.factionPlayer);
            }
        }
        return false;
    }

    @Override
    public boolean isTaskNotAccepted(@NotNull ITaskInstance taskInfo) {
        return false;
    }

    @Override
    public void pressButton(@NotNull ITaskInstance taskInfo) {
        VampirismMod.proxy.sendToServer(new ServerboundTaskActionPacket(taskInfo.getId(), taskInfo.getTaskBoard(), buttonAction(taskInfo)));
        this.taskWrapper.get(taskInfo.getTaskBoard()).removeTask(taskInfo, true);
        if (this.listener != null) {
            this.listener.run();
        }
    }

    public void setRefinement(int slot, @NotNull ItemStack stack) {
        this.refinementStacks.set(slot, stack);
    }

    @Override
    public void setReloadListener(@Nullable Runnable listener) {
        this.listener = listener;
    }

    public static class RemovingSelectorSlot extends SelectorSlot {

        private RemovingSelectorSlot(@NotNull Container inventoryIn, int index, @NotNull SelectorInfo info, Consumer<Container> refreshInvFunc, Function<Integer, Boolean> activeFunc) {
            super(inventoryIn, index, info, refreshInvFunc, activeFunc);
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            if (!stack.isEmpty()) {
                ((VampirismMenu) this.getContainer()).setRefinement(this.getSlotIndex(), stack);
            }
        }
    }

}
