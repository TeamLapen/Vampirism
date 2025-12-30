package de.teamlapen.faction.common.world.inventory;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.world.entities.minion.IMinionInventory;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.common.core.FactionMenus;
import de.teamlapen.faction.common.core.FactionMinionTasks;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.minions.MinionEntity;
import de.teamlapen.faction.common.network.packets.server.ServerboundSelectMinionTaskPacket;
import de.teamlapen.faction.common.network.packets.server.ServerboundToggleMinionTaskLock;
import de.teamlapen.faction.common.util.RegUtil;
import de.teamlapen.faction.common.world.inventory.base.AbstractInventoryContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemUseAnimation;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MinionContainer extends AbstractInventoryContainer {

    public static Optional<MinionContainer> create(int id, @NotNull Inventory playerInventory, @NotNull MinionEntity<?> minionEntity, @NotNull ILordPlayer<?> lord) {
        Optional<IMinionInventory> minionInv = minionEntity.getInventory();
        return minionInv.map(inv -> new MinionContainer(id, playerInventory, lord, minionEntity, inv, inv.getAvailableSize(), createSelectors(minionEntity, inv.getAvailableSize())));
    }

    @NotNull
    private static List<SlotDefinition> createSelectors(@NotNull MinionEntity<?> minionEntity, int extraSlots) {
        SlotDefinition[] slots = new SlotDefinition[6 + extraSlots];
        slots[0] = new SlotDefinition(minionEntity.getEquipmentPredicate(EquipmentSlot.MAINHAND).and(stack -> stack.canEquip(EquipmentSlot.MAINHAND, minionEntity)), 7, 60, 1, null);
        slots[1] = new SlotDefinition(minionEntity.getEquipmentPredicate(EquipmentSlot.OFFHAND).and(stack -> stack.canEquip(EquipmentSlot.OFFHAND, minionEntity) || stack.getUseAnimation() == ItemUseAnimation.DRINK || stack.getUseAnimation() == ItemUseAnimation.EAT), 7, 78, 5, null);
        slots[2] = new SlotDefinition(minionEntity.getEquipmentPredicate(EquipmentSlot.FEET).and(stack -> stack.canEquip(EquipmentSlot.FEET, minionEntity)), 81, 22, 1, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS);
        slots[3] = new SlotDefinition(minionEntity.getEquipmentPredicate(EquipmentSlot.LEGS).and(stack -> stack.canEquip(EquipmentSlot.LEGS, minionEntity)), 63, 22, 1, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS);
        slots[4] = new SlotDefinition(minionEntity.getEquipmentPredicate(EquipmentSlot.CHEST).and(stack -> stack.canEquip(EquipmentSlot.CHEST, minionEntity)), 45, 22, 1, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE);
        slots[5] = new SlotDefinition(minionEntity.getEquipmentPredicate(EquipmentSlot.HEAD).and(stack -> stack.canEquip(EquipmentSlot.HEAD, minionEntity)), 27, 22, 1, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET);

        assert extraSlots == 9 || extraSlots == 12 || extraSlots == 15 : "Minion inventory has unexpected size";
        for (int i = 0; i < extraSlots; i++) {
            slots[6 + i] = new SlotDefinition(itemStack -> true, 27 + 18 * (i / 3), 42 + 18 * (i % 3));
        }

        return Arrays.asList(slots);
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

    public MinionContainer(int id, @NotNull Inventory playerInventory, @NotNull ILordPlayer<?> lord, @NotNull MinionEntity<?> minionEntity, @NotNull Container inventory, int extraSlots, List<SlotDefinition> selectorInfos) {
        super(FactionMenus.MINION.get(), id, playerInventory, ContainerLevelAccess.create(minionEntity.level(), minionEntity.blockPosition()),inventory, selectorInfos);
        this.minionEntity = minionEntity;
        this.extraSlots = extraSlots;
        this.availableTasks = this.minionEntity.getAvailableTasks().stream().filter(task -> task.isAvailable(lord)).toArray(IMinionTask[]::new);
        this.minionEntity.setInteractingPlayer(playerInventory.player);
        this.addPlayerInventorySlots(playerInventory, 27, 103);
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
        return this.taskToActivate != null ? this.taskToActivate : (this.previousTask != null ? this.previousTask : FactionMinionTasks.STAY.get());
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
        //noinspection ConstantValue
        if (id >= 0 && id < availableTasks.length) {
            this.taskToActivate = availableTasks[id];
        }
    }

    private void sendChanges() {
        if (taskToActivate != null && taskToActivate != previousTask) {
            minionEntity.getMinionId().ifPresent(id -> FactionsMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(id, RegUtil.id(this.taskToActivate))));
        }
        if (previousTaskLocked != taskLocked) {
            minionEntity.getMinionId().ifPresent(id -> FactionsMod.proxy.sendToServer(new ServerboundToggleMinionTaskLock(id)));
        }
    }

    public static class Factory implements IContainerFactory<MinionContainer> {

        @Nullable
        @Override
        public MinionContainer create(int windowId, @NotNull Inventory inv, @Nullable RegistryFriendlyByteBuf data) {
            if (data == null) return null;
            int entityId = data.readVarInt(); //Anything read here has to be written to buffer in open method (in MinionEntity)
            @SuppressWarnings("ConstantValue") Entity e = inv.player.level() == null ? null : inv.player.level().getEntity(entityId);
            if (!(e instanceof MinionEntity<?> minion)) {
                throw new IllegalStateException("Cannot find related minion entity " + entityId);
            }
            ILordPlayer<?> player = FactionPlayerHandler.get(inv.player).getLordPlayer().orElseThrow();
            return MinionContainer.create(windowId, inv, minion, player).orElseThrow(() -> new IllegalStateException("Could not create container for minion " + minion.getId() + ". Data is not available"));
        }
    }

}
