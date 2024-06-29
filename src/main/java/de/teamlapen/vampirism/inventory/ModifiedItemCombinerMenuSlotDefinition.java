package de.teamlapen.vampirism.inventory;

import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ModifiedItemCombinerMenuSlotDefinition extends ItemCombinerMenuSlotDefinition {

    public ModifiedItemCombinerMenuSlotDefinition(List<SlotDefinition> pSlots, SlotDefinition pResultSlot) {
        super(pSlots, pResultSlot);
    }

    public static ModifiedItemCombinerMenuSlotDefinition.Builder createWithoutResult() {
        return new ModifiedItemCombinerMenuSlotDefinition.Builder();
    }

    @Override
    public int getResultSlotIndex() {
        return this.getResultSlot().slotIndex();
    }

    public static class Builder {
        private final List<SlotDefinition> slots = new ArrayList<>();

        public ModifiedItemCombinerMenuSlotDefinition.Builder withSlot(int pSlotIndex, int pX, int pY, Predicate<ItemStack> pMayPlace) {
            this.slots.add(new SlotDefinition(pSlotIndex, pX, pY, pMayPlace));
            return this;
        }

        public ModifiedItemCombinerMenuSlotDefinition build() {
            return new ModifiedItemCombinerMenuSlotDefinition(this.slots, new SlotDefinition(this.slots.size() - 1, 0, 0, (p_266823_) -> false));
        }
    }
}
