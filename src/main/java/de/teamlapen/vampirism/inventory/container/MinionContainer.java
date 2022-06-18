package de.teamlapen.vampirism.inventory.container;

import com.google.common.base.Predicates;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.minion.IMinionInventory;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import de.teamlapen.vampirism.network.CSelectMinionTaskPacket;
import de.teamlapen.vampirism.network.CToggleMinionTaskLock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.UseAction;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.IContainerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class MinionContainer extends InventoryContainer {
    private final static Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static MinionContainer create(int id, PlayerInventory playerInventory, MinionEntity<?> minionEntity) {
        Optional<IMinionInventory> minionInv = minionEntity.getInventory();
        return minionInv.map(inv -> new MinionContainer(id, playerInventory, minionEntity, inv, inv.getAvailableSize(), createSelectors(minionEntity, inv.getAvailableSize()))).orElse(null);
    }

    private static SelectorInfo[] createSelectors(MinionEntity<?> minionEntity, int extraSlots) {
        SelectorInfo[] slots = new SelectorInfo[6 + extraSlots];
        slots[0] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlotType.MAINHAND).and(stack -> stack.canEquip(EquipmentSlotType.MAINHAND, minionEntity)), 7, 60, false, 1, null);
        slots[1] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlotType.OFFHAND).and(stack -> stack.canEquip(EquipmentSlotType.OFFHAND, minionEntity) || stack.getUseAnimation() == UseAction.DRINK || stack.getUseAnimation() == UseAction.EAT), 7, 78, false, 5, null);
        slots[2] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlotType.FEET).and(stack -> stack.canEquip(EquipmentSlotType.FEET, minionEntity)), 81, 22, false, 1, Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS));
        slots[3] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlotType.LEGS).and(stack -> stack.canEquip(EquipmentSlotType.LEGS, minionEntity)), 63, 22, false, 1, Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS));
        slots[4] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlotType.CHEST).and(stack -> stack.canEquip(EquipmentSlotType.CHEST, minionEntity)), 45, 22, false, 1, Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE));
        slots[5] = new SelectorInfo(minionEntity.getEquipmentPredicate(EquipmentSlotType.HEAD).and(stack -> stack.canEquip(EquipmentSlotType.HEAD, minionEntity)), 27, 22, false, 1, Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET));

        assert extraSlots == 9 || extraSlots == 12 || extraSlots == 15 : "Minion inventory has unexpected size";
        for (int i = 0; i < extraSlots; i++) {
            slots[6 + i] = new SelectorInfo(Predicates.alwaysTrue(), 27 + 18 * (i / 3), 42 + 18 * (i % 3));
        }

        return slots;
    }

    private final MinionEntity<?> minionEntity;
    @Nonnull
    private final IMinionTask<?, ?>[] availableTasks;
    @Nullable
    private final IMinionTask<?, ?> previousTask;
    private final boolean previousTaskLocked;
    private final int extraSlots;
    @Nullable
    private IMinionTask<?, ?> taskToActivate;
    private boolean taskLocked;

    public MinionContainer(int id, PlayerInventory playerInventory, MinionEntity<?> minionEntity, @Nonnull IInventory inventory, int extraSlots, SelectorInfo... selectorInfos) {
        super(ModContainer.MINION.get(), id, playerInventory, IWorldPosCallable.create(minionEntity.level, minionEntity.blockPosition()), inventory, selectorInfos);
        this.minionEntity = minionEntity;
        this.extraSlots = extraSlots;
        this.availableTasks = this.minionEntity.getAvailableTasks().toArray(new IMinionTask[0]);
        this.minionEntity.setInteractingPlayer(playerInventory.player);
        this.addPlayerSlots(playerInventory, 27, 103);
        this.previousTask = this.minionEntity.getCurrentTask().map(IMinionTask.IMinionTaskDesc::getTask).orElse(null);
        this.previousTaskLocked = this.taskLocked = this.minionEntity.isTaskLocked();

    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
        if (this.minionEntity.level.isClientSide()) {
            sendChanges();
        }
        minionEntity.setInteractingPlayer(null);
    }

    @Nonnull
    public IMinionTask<?, ?>[] getAvailableTasks() {
        return availableTasks;
    }

    public int getExtraSlots() {
        return extraSlots;
    }

    public Optional<IMinionTask<?, ?>> getPreviousTask() {
        return Optional.ofNullable(previousTask);
    }

    @Nonnull
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
    public boolean stillValid(PlayerEntity playerIn) {
        return minionEntity.isAlive();
    }

    @OnlyIn(Dist.CLIENT)
    public void openConfigurationScreen() {
        this.minionEntity.openAppearanceScreen();
    }

    @OnlyIn(Dist.CLIENT)
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
            minionEntity.getMinionId().ifPresent(id ->
                    VampirismMod.dispatcher.sendToServer(new CSelectMinionTaskPacket(id, this.taskToActivate.getRegistryName()))
            );
        }
        if (previousTaskLocked != taskLocked) {
            minionEntity.getMinionId().ifPresent(id -> VampirismMod.dispatcher.sendToServer(new CToggleMinionTaskLock(id)));
        }
    }

    public static class Factory implements IContainerFactory<MinionContainer> {

        @Nullable
        @Override
        public MinionContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            if (data == null) return null;
            int entityId = data.readVarInt(); //Anything read here has to be written to buffer in open method (in MinionEntity)
            Entity e = inv.player.level == null ? null : inv.player.level.getEntity(entityId);
            if (!(e instanceof MinionEntity)) {
                LOGGER.error("Cannot find related minion entity {}", entityId);
                return null;
            }
            return MinionContainer.create(windowId, inv, (MinionEntity<?>) e);
        }
    }

}
