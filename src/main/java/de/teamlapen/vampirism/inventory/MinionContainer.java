package de.teamlapen.vampirism.inventory;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.inventory.InventoryContainerMenu;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.minion.IMinionInventory;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.network.ServerboundSelectMinionTaskPacket;
import de.teamlapen.vampirism.network.ServerboundToggleMinionTaskLock;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.UseAnim;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.network.NetworkInitialization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MinionContainer extends InventoryContainerMenu {
    private final static Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static MinionContainer create(int id, @NotNull Inventory playerInventory, @NotNull MinionEntity<?> minionEntity, @NotNull ILordPlayer lord) {
        Optional<IMinionInventory> minionInv = minionEntity.getInventory();
        return minionInv.map(inv -> new MinionContainer(id, playerInventory, lord, minionEntity, inv, inv.getAvailableSize(), createSelectors(minionEntity, inv.getAvailableSize()))).orElse(null);
    }

    private static SelectorInfo @NotNull [] createSelectors(@NotNull MinionEntity<?> minionEntity, int extraSlots) {
        SelectorInfo[] slots = new SelectorInfo[6 + extraSlots];
        slots[0] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlot.MAINHAND).and(stack -> stack.canEquip(EquipmentSlot.MAINHAND, minionEntity)), 7, 60, false, 1, null);
        slots[1] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlot.OFFHAND).and(stack -> stack.canEquip(EquipmentSlot.OFFHAND, minionEntity) || stack.getUseAnimation() == UseAnim.DRINK || stack.getUseAnimation() == UseAnim.EAT), 7, 78, false, 5, null);
        slots[2] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlot.FEET).and(stack -> stack.canEquip(EquipmentSlot.FEET, minionEntity)), 81, 22, false, 1, Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS));
        slots[3] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlot.LEGS).and(stack -> stack.canEquip(EquipmentSlot.LEGS, minionEntity)), 63, 22, false, 1, Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS));
        slots[4] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlot.CHEST).and(stack -> stack.canEquip(EquipmentSlot.CHEST, minionEntity)), 45, 22, false, 1, Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE));
        slots[5] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlot.HEAD).and(stack -> stack.canEquip(EquipmentSlot.HEAD, minionEntity)), 27, 22, false, 1, Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET));

        assert extraSlots == 9 || extraSlots == 12 || extraSlots == 15 : "Minion inventory has unexpected size";
        for (int i = 0; i < extraSlots; i++) {
            slots[6 + i] = new SelectorInfo(itemStack -> true, 27 + 18 * (i / 3), 42 + 18 * (i % 3));
        }

        return slots;
    }

    private final @NotNull MinionEntity<?> minionEntity;
    @NotNull
    private final IMinionTask<?, ?> @NotNull [] availableTasks;
    @Nullable
    private final IMinionTask<?, ?> previousTask;
    private final boolean previousTaskLocked;
    private final int extraSlots;
    @Nullable
    private IMinionTask<?, ?> taskToActivate;
    private boolean taskLocked;

    public MinionContainer(int id, @NotNull Inventory playerInventory, @NotNull ILordPlayer lord, @NotNull MinionEntity<?> minionEntity, @NotNull Container inventory, int extraSlots, SelectorInfo... selectorInfos) {
        super(ModContainer.MINION.get(), id, playerInventory, ContainerLevelAccess.create(minionEntity.level(), minionEntity.blockPosition()), inventory, selectorInfos);
        this.minionEntity = minionEntity;
        this.extraSlots = extraSlots;
        this.availableTasks = this.minionEntity.getAvailableTasks().stream().filter(task -> task.isAvailable(lord.getLordFaction(), lord)).toArray(IMinionTask[]::new);
        this.minionEntity.setInteractingPlayer(playerInventory.player);
        this.addPlayerSlots(playerInventory, 27, 103);
        this.previousTask = this.minionEntity.getCurrentTask().map(IMinionTask.IMinionTaskDesc::getTask).orElse(null);
        this.previousTaskLocked = this.taskLocked = this.minionEntity.isTaskLocked();

    }

    @Override
    public void removed(@NotNull Player playerIn) {
        super.removed(playerIn);
        if (this.minionEntity.level().isClientSide()) {
            sendChanges();
        }
        minionEntity.setInteractingPlayer(null);
    }

    @NotNull
    public IMinionTask<?, ?>[] getAvailableTasks() {
        return availableTasks;
    }

    public int getExtraSlots() {
        return extraSlots;
    }

    public @NotNull Optional<IMinionTask<?, ?>> getPreviousTask() {
        return Optional.ofNullable(previousTask);
    }

    @NotNull
    public IMinionTask<?, ?> getSelectedTask() {
        return this.taskToActivate != null ? this.taskToActivate : (this.previousTask != null ? this.previousTask : MinionTasks.STAY.get());
    }

    public boolean isTaskLocked() {
        return taskLocked;
    }

    public void setTaskLocked(boolean taskLocked) {
        this.taskLocked = taskLocked;
    }

    @Override
    public boolean stillValid(@NotNull Player playerIn) {
        return minionEntity.isAlive();
    }

    public void openConfigurationScreen() {
        this.minionEntity.openAppearanceScreen();
    }

    public void openStatsScreen() {
        this.minionEntity.openStatsScreen();
    }

    public void setTaskToActivate(int id) {
        assert id >= 0 && id < availableTasks.length;
        //noinspection ConstantConditions
        if (id >= 0 && id < availableTasks.length) {
            this.taskToActivate = availableTasks[id];
        }
    }

    private void sendChanges() {
        if (taskToActivate != null && taskToActivate != previousTask) {
            minionEntity.getMinionId().ifPresent(id -> VampirismMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(id, RegUtil.id(this.taskToActivate))));
        }
        if (previousTaskLocked != taskLocked) {
            minionEntity.getMinionId().ifPresent(id -> VampirismMod.proxy.sendToServer(new ServerboundToggleMinionTaskLock(id)));
        }
    }

    public static class Factory implements IContainerFactory<MinionContainer> {

        @Override
        public MinionContainer create(int p_create_1_, Inventory p_create_2_) {
            return IContainerFactory.super.create(p_create_1_, p_create_2_);
        }

        @Nullable
        @Override
        public MinionContainer create(int windowId, @NotNull Inventory inv, @Nullable FriendlyByteBuf data) {
            if (data == null) return null;
            int entityId = data.readVarInt(); //Anything read here has to be written to buffer in open method (in MinionEntity)
            Entity e = inv.player.level() == null ? null : inv.player.level().getEntity(entityId);
            if (!(e instanceof MinionEntity)) {
                LOGGER.error("Cannot find related minion entity {}", entityId);
                return null;
            }
            //noinspection ConstantConditions
            ILordPlayer player = FactionPlayerHandler.getOpt(inv.player).orElse(null);
            return MinionContainer.create(windowId, inv, (MinionEntity<?>) e, player);
        }
    }

}
