package de.teamlapen.vampirism.inventory.container;

import com.google.common.base.Predicates;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionInventory;
import de.teamlapen.vampirism.entity.minion.management.MinionTask;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.fml.network.IContainerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;


public class MinionContainer extends InventoryContainer {
    private final static Logger LOGGER = LogManager.getLogger();

    @Nullable
    public static MinionContainer create(int id, PlayerInventory playerInventory, MinionEntity<?> minionEntity) {
        Optional<MinionInventory> minionInv = minionEntity.getInventory();
        return minionInv.map(inv -> new MinionContainer(id, playerInventory, minionEntity, inv, inv.getAvailableSize(), createSelectors(minionEntity, inv.getAvailableSize()))).orElse(null);
    }

    private static SelectorInfo[] createSelectors(MinionEntity<?> minionEntity, int extraSlots) {
        Predicate<ItemStack> factionPredicate = itemStack -> !(itemStack.getItem() instanceof IFactionExclusiveItem) || ((IFactionExclusiveItem) itemStack.getItem()).getExclusiveFaction().equals(minionEntity.getFaction());
        SelectorInfo[] slots = new SelectorInfo[6 + extraSlots];
        slots[0] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.MAINHAND, minionEntity)), 7, 42, false, 1);
        slots[1] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.OFFHAND, minionEntity)), 7, 60, false, 1);
        slots[2] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.FEET, minionEntity)), 81, 22, false, 1, Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE,PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS));
        slots[3] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.LEGS, minionEntity)), 63, 22, false, 1, Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE,PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS));
        slots[4] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.CHEST, minionEntity)), 45, 22, false, 1, Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE,PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE));
        slots[5] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.HEAD, minionEntity)), 27, 22, false, 1, Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE,PlayerContainer.EMPTY_ARMOR_SLOT_HELMET));

        assert extraSlots == 9 || extraSlots == 12 || extraSlots == 15 : "Minion inventory has unexpected size";
        for (int i = 0; i < extraSlots; i++) {
            slots[6 + i] = new SelectorInfo(Predicates.alwaysTrue(), 27 + 18 * (i / 3), 42 + 18 * (i % 3));
        }

        return slots;
    }

    private final MinionEntity<?> minionEntity;
    @Nonnull
    private final MinionTask.Type[] availableTasks;
    private final IntReferenceHolder taskToActivate;
    private final int extraSlots;

    public MinionContainer(int id, PlayerInventory playerInventory, MinionEntity<?> minionEntity, @Nonnull IInventory inventory, int extraSlots, SelectorInfo... selectorInfos) {
        super(ModContainer.minion, id, playerInventory, IWorldPosCallable.of(minionEntity.world, minionEntity.getPosition()), inventory, selectorInfos);
        this.minionEntity = minionEntity;
        this.extraSlots = extraSlots;
        this.availableTasks = this.minionEntity.getAvailableTasks().toArray(new MinionTask.Type[0]);
        this.minionEntity.setInteractingPlayer(playerInventory.player);
        this.addPlayerSlots(playerInventory, 27, 103);
        this.taskToActivate = trackInt(IntReferenceHolder.single());
        this.taskToActivate.set(this.minionEntity.getCurrentTask().map(t -> {
            LOGGER.info("Active task {}", t.type);
            return this.minionEntity.getAvailableTasks().indexOf(t.type);
        }).orElse(-1));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @Nonnull
    public MinionTask.Type[] getAvailableTasks() {
        return availableTasks;
    }

    public int getExtraSlots() {
        return extraSlots;
    }

    public int getTaskToActivate() {
        return taskToActivate.get();
    }

    public void setTaskToActivate(int taskToActivate) {
        assert taskToActivate < availableTasks.length;
        if (taskToActivate >= availableTasks.length) return;
        this.taskToActivate.set(taskToActivate);
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        minionEntity.setInteractingPlayer(null);
    }

    public static class Factory implements IContainerFactory<MinionContainer> {

        @Nullable
        @Override
        public MinionContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
            if (data == null) return null;
            int entityId = data.readVarInt(); //Anything read here has to be written to buffer in open method (in MinionEntity)
            Entity e = inv.player.world == null ? null : inv.player.world.getEntityByID(entityId);
            if (!(e instanceof MinionEntity)) {
                LOGGER.error("Cannot find related minion entity {}", entityId);
                return null;
            }
            return MinionContainer.create(windowId, inv, (MinionEntity<?>) e);
        }
    }


}
