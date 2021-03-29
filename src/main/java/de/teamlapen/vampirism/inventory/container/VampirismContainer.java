package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.client.gui.TaskContainer;
import de.teamlapen.vampirism.client.gui.widget.TaskItem;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.TaskManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class VampirismContainer extends InventoryContainer implements TaskContainer {

    public Map<UUID, TaskManager.TaskBoardInfo> taskBoardInfos = new HashMap<>();
    public Map<UUID, Set<Task>> tasks = new HashMap<>();
    public Map<UUID, Map<Task, Map<ResourceLocation, Integer>>> completedRequirements = new HashMap<>();

    private final IFactionPlayer<?> factionPlayer;

    private Runnable listener;

    public VampirismContainer(int id, PlayerInventory playerInventory) {
        super(ModContainer.vampirism, id, playerInventory, IWorldPosCallable.DUMMY, new Inventory(3), RemovingSelectorSlot::new, new SelectorInfo(stack -> true, 58, 8), new SelectorInfo(stack -> true, 58, 26), new SelectorInfo(stack -> true, 58, 44));
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get();
        addPlayerSlots(playerInventory, 37, 124);
    }

    @OnlyIn(Dist.CLIENT)
    public void init(@Nonnull Map<UUID, TaskManager.TaskBoardInfo> taskBoardInfos, @Nonnull Map<UUID, Set<Task>> tasks, @Nonnull Map<UUID, Map<Task, Map<ResourceLocation, Integer>>> completedRequirements) {
        this.taskBoardInfos = taskBoardInfos;
        this.tasks = tasks;
        this.completedRequirements = completedRequirements;
        if (this.listener != null) {
            this.listener.run();
        }
    }

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    public IFactionPlayer<?> getFactionPlayer() {
        return this.factionPlayer;
    }


    @Override
    public boolean isTaskNotAccepted(TaskItem.TaskInfo taskInfo) {
        return false;//TODO
    }

    @Override
    public boolean canCompleteTask(TaskItem.TaskInfo taskInfo) {
        return false;//TODO
    }

    @Override
    public boolean pressButton(TaskItem.TaskInfo taskInfo) {
        return false;//TODO
    }

    @Override
    public TaskAction buttonAction(TaskItem.TaskInfo taskInfo) {
        return null;//TODO
    }

    @Override
    public boolean isCompleted(TaskItem.TaskInfo item) {
        return false;
    }

    public static class RemovingSelectorSlot extends SelectorSlot {
        public RemovingSelectorSlot(IInventory inventoryIn, int index, SelectorInfo info, Consumer<IInventory> refreshInvFunc, Function<Integer, Boolean> activeFunc) {
            super(inventoryIn, index, info, refreshInvFunc, activeFunc);
        }

        @Override
        public boolean canTakeStack(PlayerEntity playerIn) {
            return false;
        }

        @Override
        public void onSlotChange(ItemStack oldStackIn, ItemStack newStackIn) {
            super.onSlotChange(oldStackIn, newStackIn);
            oldStackIn.shrink(1);
        }

        @Override
        public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
            return super.onTake(thePlayer, stack);
        }


        @Override
        public void putStack(ItemStack stack) {
            this.inventory.getStackInSlot(this.slotNumber).shrink(1);
            super.putStack(stack);
        }
    }

}
