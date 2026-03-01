package de.teamlapen.vampirism.common.world.inventory;

import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModMenus;
import de.teamlapen.vampirism.common.tags.ModItemTags;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireLeveling;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AltarInfusionMenu extends AbstractContainerMenu {

    public static final List<Predicate<ItemStack>> SLOT_PREDICATES = List.of(
            stack -> stack.is(ModItemTags.PURE_BLOOD),
            stack -> stack.is(ModItems.HUMAN_HEART),
            stack -> stack.is(ModItems.VAMPIRE_BOOK)
    );

    private final Container altarInventory;
    private final Player player;

    public AltarInfusionMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(3));
    }

    public AltarInfusionMenu(int containerId, Inventory playerInventory, Container altarInventory) {
        super(ModMenus.ALTAR_INFUSION.get(), containerId);
        checkContainerSize(altarInventory, 3);
        this.altarInventory = altarInventory;
        this.player = playerInventory.player;

        this.addSlot(new ValidatorSlot(altarInventory, 0, 44, 34, SLOT_PREDICATES.get(0)));
        this.addSlot(new ValidatorSlot(altarInventory, 1, 80, 34, SLOT_PREDICATES.get(1)));
        this.addSlot(new ValidatorSlot(altarInventory, 2, 116, 34, SLOT_PREDICATES.get(2)));

        this.addStandardInventorySlots(playerInventory, 8, 84);
    }

    public Optional<VampireLeveling.AltarInfusionRequirements> getRequirements() {
        return VampireLeveling.getInfusionRequirement(FactionPlayerHandler.get(this.player).getCurrentLevel(ModFactions.VAMPIRE) + 1);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.altarInventory.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack originalStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            originalStack = stack.copy();

            if (index < 3) {
                if (!this.moveItemStackTo(stack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stack, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == originalStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return originalStack;
    }

    public static class ValidatorSlot extends Slot {

        private final Predicate<ItemStack> validator;

        public ValidatorSlot(Container container, int slot, int x, int y, Predicate<ItemStack> validator) {
            super(container, slot, x, y);
            this.validator = validator;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return this.validator.test(stack) && super.mayPlace(stack);
        }
    }
}
