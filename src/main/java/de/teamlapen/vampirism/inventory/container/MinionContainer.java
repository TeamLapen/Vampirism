package de.teamlapen.vampirism.inventory.container;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.IContainerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class MinionContainer extends InventoryContainer {
    private final static Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static MinionContainer create(int id, Inventory playerInventory, MinionEntity<?> minionEntity) {
        Optional<IMinionInventory> minionInv = minionEntity.getInventory();
        return minionInv.map(inv -> new MinionContainer(id, playerInventory, minionEntity, inv, inv.getAvailableSize(), createSelectors(minionEntity, inv.getAvailableSize()))).orElse(null);
    }

    private static SelectorInfo[] createSelectors(MinionEntity<?> minionEntity, int extraSlots) {
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

    public MinionContainer(int id, Inventory playerInventory, MinionEntity<?> minionEntity, @Nonnull Container inventory, int extraSlots, SelectorInfo... selectorInfos) {
        super(ModContainer.MINION.get(), id, playerInventory, ContainerLevelAccess.create(minionEntity.level, minionEntity.blockPosition()), inventory, selectorInfos);
        this.minionEntity = minionEntity;
        this.extraSlots = extraSlots;
        this.availableTasks = this.minionEntity.getAvailableTasks().toArray(new IMinionTask[0]);
        this.minionEntity.setInteractingPlayer(playerInventory.player);
        this.addPlayerSlots(playerInventory, 27, 103);
        this.previousTask = this.minionEntity.getCurrentTask().map(IMinionTask.IMinionTaskDesc::getTask).orElse(null);
        this.previousTaskLocked = this.taskLocked = this.minionEntity.isTaskLocked();

    }

    @Override
    public void removed(@Nonnull Player playerIn) {
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
    public boolean stillValid(@Nonnull Player playerIn) {
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
                    VampirismMod.dispatcher.sendToServer(new CSelectMinionTaskPacket(id, RegUtil.id(this.taskToActivate)))
            );
        }
        if (previousTaskLocked != taskLocked) {
            minionEntity.getMinionId().ifPresent(id -> VampirismMod.dispatcher.sendToServer(new CToggleMinionTaskLock(id)));
        }
    }

    public static class Factory implements IContainerFactory<MinionContainer> {

        @Nullable
        @Override
        public MinionContainer create(int windowId, Inventory inv, FriendlyByteBuf data) {
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
