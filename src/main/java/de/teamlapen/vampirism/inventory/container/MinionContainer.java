package de.teamlapen.vampirism.inventory.container;

import com.google.common.base.Predicates;
import de.teamlapen.lib.lib.inventory.InventoryContainer;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModContainer;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
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
        return minionInv.map(inv -> new MinionContainer(id, playerInventory, IWorldPosCallable.of(minionEntity.world, minionEntity.getPosition()), inv, createSelectors(minionEntity, inv.getAvailableSize()))).orElse(null);
    }

    private static SelectorInfo[] createSelectors(MinionEntity<?> minionEntity, int extraSlots) {
        Predicate<ItemStack> factionPredicate = itemStack -> !(itemStack.getItem() instanceof IFactionExclusiveItem) || ((IFactionExclusiveItem) itemStack.getItem()).getExclusiveFaction().equals(minionEntity.getFaction());
        SelectorInfo[] slots = new SelectorInfo[6 + extraSlots];
        slots[0] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.MAINHAND, minionEntity)), 1, 1, 1);
        slots[1] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.OFFHAND, minionEntity)), 1, 1, 1);
        slots[2] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.HEAD, minionEntity)), 1, 1, 1);
        slots[3] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.CHEST, minionEntity)), 1, 1, 1);
        slots[4] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.LEGS, minionEntity)), 1, 1, 1);
        slots[5] = new SelectorInfo(factionPredicate.and(stack -> stack.canEquip(EquipmentSlotType.FEET, minionEntity)), 1, 1, 1);
        assert extraSlots == 9 || extraSlots == 16 || extraSlots == 25 : "Minion inventory has unexpected size";
        for (int i = 0; i < extraSlots; i++) {
            slots[6 + i] = new SelectorInfo(Predicates.alwaysTrue(), 1, 1);
        }

        return slots;
    }

    public MinionContainer(int id, PlayerInventory playerInventory, IWorldPosCallable worldPos, @Nonnull IInventory inventory, SelectorInfo... selectorInfos) {
        super(ModContainer.minion, id, playerInventory, worldPos, inventory, selectorInfos);
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
