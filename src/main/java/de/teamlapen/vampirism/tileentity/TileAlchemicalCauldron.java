package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.tile.InventoryTileEntity;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.blocks.BlockAlchemicalCauldron;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronCraftingManager;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

/**
 * 1.10
 *
 * @author maxanier
 */
public class TileAlchemicalCauldron extends InventoryTileEntity implements ITickable, ISidedInventory {

    private static final int SLOT_RESULT = 0;
    private static final int SLOT_LIQUID = 1;
    private static final int SLOT_INGREDIENT = 2;
    private static final int SLOT_FUEL = 3;
    private static final int[] SLOTS_TOP = new int[]{SLOT_RESULT};
    private static final int[] SLOTS_WEST = new int[]{SLOT_LIQUID};
    private static final int[] SLOTS_EAST = new int[]{SLOT_INGREDIENT};
    private static final int[] SLOTS_BOTTOM = new int[]{SLOT_FUEL};
    private int burnTime, cookTime = 0, totalCookTime = 0;

    public TileAlchemicalCauldron() {
        super(new InventorySlot[]{new InventorySlot(new InventorySlot.IItemSelector() {
            @Override
            public boolean isItemAllowed(@Nonnull ItemStack item) {
                return false;
            }
        }, 116, 35), new InventorySlot(44, 17), new InventorySlot(68, 17), new InventorySlot(new InventorySlot.IItemSelector() {
            @Override
            public boolean isItemAllowed(@Nonnull ItemStack item) {
                return TileEntityFurnace.isItemFuel(item);
            }
        }, 56, 53)});
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public String getName() {
        return "vampirism.container." + BlockAlchemicalCauldron.regName;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        switch (side) {
            case WEST:
                return SLOTS_WEST;
            case EAST:
                return SLOTS_EAST;
            case DOWN:
                return SLOTS_BOTTOM;
            case UP:
                return SLOTS_TOP;
            default:
                return new int[0];
        }
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    /**
     * @return If there is a liquid inside
     */
    public boolean isFilled() {
        return getStackInSlot(SLOT_LIQUID) != null;//TODO 1.11 null
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void update() {
        boolean wasBurning = isBurning();
        boolean dirty = false;
        if (wasBurning) {
            burnTime--;
        }

        if (!worldObj.isRemote) {
            if (isBurning() || isStackInSlot(SLOT_LIQUID) && isStackInSlot(SLOT_INGREDIENT) && isStackInSlot(SLOT_FUEL)) {
                if (!isBurning() && canCook()) {
                    this.burnTime = TileEntityFurnace.getItemBurnTime(getStackInSlot(SLOT_FUEL));
                    VampirismMod.log.t("Start burning");
                    if (isBurning()) {
                        decrStackSize(SLOT_FUEL, 1);
                        dirty = true;
                        VampirismMod.log.t("Started burning");

                    }
                }
                if (isBurning() && this.canCook()) {
                    cookTime++;
                    VampirismMod.log.t("Cooking");
                    if (cookTime == totalCookTime) {
                        cookTime = 0;
                        this.totalCookTime = getCookTime();
                        this.finishCooking();
                        dirty = true;
                    }
                } else {
                    cookTime = 0;
                }

            } else if (!isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp_int(this.cookTime - 2, 0, this.totalCookTime);
            }
            if (wasBurning != this.isBurning()) {
                dirty = true;
            }
        }
        if (dirty) this.markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    private boolean canCook() {
        if (!isStackInSlot(SLOT_LIQUID) || !isStackInSlot(SLOT_INGREDIENT)) return false;
        ItemStack stack = AlchemicalCauldronCraftingManager.getInstance().getCookingResult(getStackInSlot(SLOT_LIQUID), getStackInSlot(SLOT_INGREDIENT));
        if (stack == null) return false;
        if (!isStackInSlot(SLOT_RESULT)) return true;
        if (!getStackInSlot(SLOT_RESULT).isItemEqual(stack)) return false;
        int size = getStackInSlot(SLOT_RESULT).stackSize + stack.stackSize;
        return size <= getInventoryStackLimit() && size <= getStackInSlot(SLOT_RESULT).getMaxStackSize();
    }

    private void finishCooking() {
        if (canCook()) {
            ItemStack stack = AlchemicalCauldronCraftingManager.getInstance().getCookingResult(getStackInSlot(SLOT_LIQUID), getStackInSlot(SLOT_INGREDIENT));
            if (!isStackInSlot(SLOT_RESULT)) {
                setInventorySlotContents(SLOT_RESULT, stack.copy());
            } else if (getStackInSlot(SLOT_RESULT).isItemEqual(stack)) {
                getStackInSlot(SLOT_RESULT).stackSize += stack.stackSize;
            }
            decrStackSize(SLOT_LIQUID, 1);
            decrStackSize(SLOT_INGREDIENT, 1);
            VampirismMod.log.t("Finished cooking");
        }
    }

    private int getCookTime() {
        return 200;
    }

    private boolean isStackInSlot(int slot) {
        return getStackInSlot(slot) != null;//TODO 1.11 null
    }

}
