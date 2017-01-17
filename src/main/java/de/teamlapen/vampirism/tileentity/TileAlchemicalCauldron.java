package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.tile.InventoryTileEntity;
import de.teamlapen.lib.util.ISoundReference;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import de.teamlapen.vampirism.blocks.BlockAlchemicalCauldron;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.inventory.AlchemicalCauldronCraftingManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    private int totalBurnTime = 0, burnTime = 0, cookTime = 0, totalCookTime = 0;
    @SideOnly(Side.CLIENT)
    private boolean cooking;
    @SideOnly(Side.CLIENT)
    private boolean burning;
    @SideOnly(Side.CLIENT)
    private int liquidColor = 0;

    @SideOnly(Side.CLIENT)
    private ISoundReference boilingSound;

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
    public int getField(int id) {
        switch (id) {
            case 0:
                return this.totalBurnTime;
            case 1:
                return this.burnTime;
            case 2:
                return this.totalCookTime;
            case 3:
                return this.cookTime;
            default:
                return 0;
        }
    }

    @Override
    public int getFieldCount() {
        return 4;
    }

    @SideOnly(Side.CLIENT)
    public int getLiquidColor() {
        return liquidColor;
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

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        nbt.setBoolean("cooking", cookTime > 0 && isBurning());
        nbt.setBoolean("burning", burnTime > 0);
        ItemStack liquidItem = getStackInSlot(SLOT_LIQUID);
        if (liquidItem != null) {
            nbt.setTag("liquidItem", liquidItem.writeToNBT(new NBTTagCompound()));
        }
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound nbt) {
        super.handleUpdateTag(nbt);
        cooking = nbt.getBoolean("cooking");
        burning = nbt.getBoolean("burning");
        if (nbt.hasKey("liquidItem")) {
            this.setInventorySlotContents(SLOT_LIQUID, ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("liquidItem")));
        } else {
            this.setInventorySlotContents(SLOT_LIQUID, null);
        }
        this.updateLiquidColor();
        this.worldObj.markBlockRangeForRenderUpdate(pos, pos);
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    @SideOnly(Side.CLIENT)
    public boolean isBurningClient() {
        return burning;
    }

    @SideOnly(Side.CLIENT)
    public boolean isCookingClient() {
        return cooking;
    }

    /**
     * @return If there is a liquid inside
     */
    public boolean isFilled() {
        return liquidColor != -1 && getStackInSlot(SLOT_LIQUID) != null;//TODO 1.11 null
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.getNbtCompound();
        handleUpdateTag(nbt);

    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        this.burnTime = tagCompound.getInteger("burntime");
        this.cookTime = tagCompound.getInteger("cooktime");
        this.totalBurnTime = tagCompound.getInteger("cooktime_total");
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.totalBurnTime = value;
                break;
            case 1:
                this.burnTime = value;
                break;
            case 2:
                this.totalCookTime = value;
                break;
            case 3:
                this.cookTime = value;
                break;
        }
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        super.setInventorySlotContents(slot, stack);
        if (slot == SLOT_LIQUID && worldObj instanceof WorldServer) {
            ((WorldServer) worldObj).getPlayerChunkMap().markBlockForUpdate(pos);
        }

    }

    @Override
    public void update() {
        boolean wasBurning = isBurning();
        boolean wasCooking = cookTime > 0;
        boolean dirty = false;
        if (wasBurning) {
            burnTime--;
        }

        if (!worldObj.isRemote) {
            if (isBurning() || isStackInSlot(SLOT_LIQUID) && isStackInSlot(SLOT_INGREDIENT) && isStackInSlot(SLOT_FUEL)) {
                if (!isBurning() && canCook()) {
                    this.burnTime = TileEntityFurnace.getItemBurnTime(getStackInSlot(SLOT_FUEL));
                    if (isBurning()) {
                        decrStackSize(SLOT_FUEL, 1);
                        dirty = true;
                    }
                    totalBurnTime = burnTime;
                }
                if (isBurning() && this.canCook()) {
                    cookTime++;
                    if (cookTime >= totalCookTime) {
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
            } else if (wasCooking != this.cookTime > 0) {
                dirty = true;
            }

        } else {
            //TODO particles
            //Do not check ISoundReference#isSoundPlaying for performance reason here. Also should not make any difference
            if (isCookingClient() && boilingSound == null && this.worldObj.rand.nextInt(25) == 0) {
                boilingSound = VampLib.proxy.createSoundReference(ModSounds.boiling, SoundCategory.BLOCKS, getPos(), 0.015F, 7);

                boilingSound.startPlaying();
            } else if (!isCookingClient() && boilingSound != null) {
                boilingSound.stopPlaying();
                boilingSound = null;
            }

        }
        if (dirty) {
            this.markDirty();
            IBlockState state = worldObj.getBlockState(pos);
            this.worldObj.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("burntime", this.burnTime);
        compound.setInteger("cooktime", this.cookTime);
        compound.setInteger("cooktime_total", this.totalCookTime);
        return compound;
    }

    private boolean canCook() {
        if (!isStackInSlot(SLOT_LIQUID)) return false;
        EntityPlayer owner = getOwner();
        //if(owner==null)return false;
        IAlchemicalCauldronRecipe recipe = AlchemicalCauldronCraftingManager.getInstance().findRecipe(getStackInSlot(SLOT_LIQUID), getStackInSlot(SLOT_INGREDIENT));
        if (recipe == null) return false;
        totalCookTime = recipe.getCookingTime();
        if (!canPlayerCook(recipe)) return false;
        if (!isStackInSlot(SLOT_RESULT)) return true;
        if (!getStackInSlot(SLOT_RESULT).isItemEqual(recipe.getOutput())) return false;
        int size = getStackInSlot(SLOT_RESULT).stackSize + recipe.getOutput().stackSize;
        return size <= getInventoryStackLimit() && size <= getStackInSlot(SLOT_RESULT).getMaxStackSize();
    }

    private boolean canPlayerCook(IAlchemicalCauldronRecipe recipe) {
        return true;
    }

    private void finishCooking() {
        if (canCook()) {
            IAlchemicalCauldronRecipe recipe = AlchemicalCauldronCraftingManager.getInstance().findRecipe(getStackInSlot(SLOT_LIQUID), getStackInSlot(SLOT_INGREDIENT));
            if (!isStackInSlot(SLOT_RESULT)) {
                setInventorySlotContents(SLOT_RESULT, recipe.getOutput().copy());
            } else if (getStackInSlot(SLOT_RESULT).isItemEqual(recipe.getOutput())) {
                getStackInSlot(SLOT_RESULT).stackSize += recipe.getOutput().stackSize;
            }
            if (recipe.isValidLiquidItem(getStackInSlot(SLOT_LIQUID))) {
                decrStackSize(SLOT_LIQUID, 1);
            } else {
                ItemStack fluidContainer = getStackInSlot(SLOT_LIQUID);
                FluidStack s = recipe.isValidFluidItem(fluidContainer);
                if (s != null) {
                    IFluidHandler handler = fluidContainer.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                    handler.drain(s, true);
                } else {
                    VampirismMod.log.w("AlchemicalCauldron", "Cooked item without valid input liquid (Recipe %s, Input %s)", recipe, fluidContainer);
                }
            }
            decrStackSize(SLOT_INGREDIENT, 1);
            VampirismMod.log.t("Finished cooking");
        }
    }

    private int getCookTime() {
        return 200;
    }

    private
    @Nullable
    EntityPlayer getOwner() {
        return null;
    }

    private boolean isStackInSlot(int slot) {
        return getStackInSlot(slot) != null;//TODO 1.11 null
    }

    /**
     * Updates the liquid color used for the model based on the item in the liquid slot
     */
    @SideOnly(Side.CLIENT)
    private void updateLiquidColor() {
        ItemStack s = getStackInSlot(SLOT_LIQUID);
        if (s != null) {
            if (s.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
                IFluidHandler handler = s.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                FluidStack fluid = handler.drain(100, false);
                if (fluid != null) {
                    liquidColor = fluid.getFluid().getColor(fluid);
                    return;
                }
            }
        }
        liquidColor = AlchemicalCauldronCraftingManager.getInstance().getLiquidColor(s);
    }

}
