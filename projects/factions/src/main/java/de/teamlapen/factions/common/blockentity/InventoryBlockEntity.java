package de.teamlapen.factions.common.blockentity;

import de.teamlapen.factions.common.inventory.InventoryContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Basic abstract class for BlockEntities which need a small inventory (with a gui)
 */
public abstract class InventoryBlockEntity extends NetworkedContainerBlockEntity implements MenuProvider {

    /**
     * Maximal squared distance from which the player can access the inventory
     */
    protected final int MAX_DIST_SQRT = 64;
    protected NonNullList<ItemStack> inventorySlots;
    protected InventoryContainerMenu.SelectorInfo[] selectors;

    public InventoryBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, ItemCombinerMenuSlotDefinition slotDefinition) {
        this(tileEntityTypeIn, pos, state, slotDefinition.getNumOfInputSlots(), slotDefinition.getSlots().stream().map(d -> new InventoryContainerMenu.SelectorInfo(d.mayPlace(), d.x(), d.y())).toArray(InventoryContainerMenu.SelectorInfo[]::new));
    }

    public InventoryBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int size, InventoryContainerMenu.SelectorInfo ... selectorInfos) {
        super(tileEntityTypeIn, pos, state);
        this.inventorySlots = NonNullList.withSize(size, ItemStack.EMPTY);
        if (selectorInfos.length != size) {
            throw new IllegalArgumentException("Selector count must match inventory size");
        }
        selectors = selectorInfos;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= selectors.length) return false;
        return selectors[slot].validate(stack);
    }

    @Override
    public void clearContent() {
        inventorySlots.clear();
    }

    @Override
    public int getContainerSize() {
        return inventorySlots.size();
    }

    @Override
    public ItemStack getItem(int index) {
        return inventorySlots.get(index);
    }

    @Override
    public boolean isEmpty() {
        return inventorySlots.isEmpty();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> newInventory) {
        this.inventorySlots = newInventory;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventorySlots;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inventorySlots.clear();
        ContainerHelper.loadAllItems(input, this.inventorySlots);
    }

    @Override
    public ItemStack removeItem(int slot, int amt) {
        return ContainerHelper.removeItem(inventorySlots, slot, amt);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        return ContainerHelper.takeItem(inventorySlots, index);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, inventorySlots);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventorySlots.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        this.setChanged();//Not sure

    }

    @Override
    public boolean stillValid(Player player) {
        if (!hasLevel()) return false;
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5D, (double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) <= MAX_DIST_SQRT;
        }
    }

    protected boolean isFull() {
        for (ItemStack s : inventorySlots) {
            if (s.isEmpty() || s.getCount() < s.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

}