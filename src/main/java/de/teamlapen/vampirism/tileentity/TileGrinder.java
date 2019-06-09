package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.tile.InventoryTileEntity;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModTiles;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class TileGrinder extends InventoryTileEntity implements ITickable {


    private static boolean canProcess(ItemStack stack) {
        return BloodConversionRegistry.getImpureBloodValue(stack) > 0;
    }


    protected static List<EntityItem> getCaptureItems(World worldIn, BlockPos pos) {
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        return worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(posX, posY + 0.5D, posZ, posX + 1D, posY + 1.5D, posZ + 1D), EntitySelectors.IS_ALIVE);
    }

    private int cooldownPull = 0;
    private int cooldownProcess = 0;
    //Used to provide ItemHandler compatibility
    private IItemHandler itemHandler = new InvWrapper(this);

    public TileGrinder() {
        super(ModTiles.grinder, new InventorySlot[]{new InventorySlot(TileGrinder::canProcess, 80, 34)});
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if ((facing == null || facing != EnumFacing.DOWN) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) (itemHandler);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public String getName() {
        return "tile.vampirism.blood_grinder.name";
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return ((facing == null || facing != EnumFacing.DOWN) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @Override
    public void read(NBTTagCompound tagCompound) {
        super.read(tagCompound);
        cooldownPull = tagCompound.getInteger("cooldown_pull");
        cooldownProcess = tagCompound.getInteger("cooldown_process");
    }

    @Override
    public void update() {
        if (this.world != null && !this.world.isRemote) {
            --this.cooldownPull;
            if (cooldownPull <= 0) {
                cooldownPull = 10;
                this.updatePull();
            }

            --this.cooldownProcess;
            if (cooldownProcess <= 0) {
                cooldownProcess = 10;
                this.updateProcess();
            }

        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound compound) {
        compound.setInteger("cooldown_pull", cooldownPull);
        compound.setInteger("cooldown_process", cooldownProcess);
        return super.write(compound);
    }

    private boolean pullItems() {
        IItemHandler handler = de.teamlapen.lib.lib.inventory.InventoryHelper.tryGetItemHandler(this.world, this.pos.up(), EnumFacing.DOWN);
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack extracted = handler.extractItem(i, 1, true);
                if (!extracted.isEmpty()) {
                    for (int j = 0; j < itemHandler.getSlots(); j++) {
                        ItemStack simulated = itemHandler.insertItem(j, extracted, true);
                        if (simulated.isEmpty()) {
                            extracted = handler.extractItem(i, 1, false);
                            itemHandler.insertItem(j, extracted, false);
                            return true;
                        }
                    }
                }
            }

        }
        for (EntityItem entityItem : getCaptureItems(this.world, this.pos)) {
            ItemStack stack = entityItem.getItem();
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack2 = itemHandler.insertItem(i, stack, true);
                if (stack2.isEmpty()) {
                    stack2 = itemHandler.insertItem(i, stack, false);
                    if (stack2.getCount() < stack.getCount()) {
                        entityItem.remove();
                    } else {
                        entityItem.setItem(stack2);
                    }
                    return true;
                }
            }
        }
        return false;

    }

    private void updateProcess() {
        if (!isEmpty()) {
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.extractItem(i, 1, true);
                int blood = BloodConversionRegistry.getImpureBloodValue(stack);
                if (blood > 0) {
                    FluidStack fluid = new FluidStack(ModFluids.impure_blood, blood);
                    IFluidHandler handler = FluidUtil.getFluidHandler(this.getWorld(), this.pos.down(), EnumFacing.UP);
                    if (handler != null) {
                        int filled = handler.fill(fluid, false);
                        if (filled >= 0.9f * blood) {
                            ItemStack extractedStack = itemHandler.extractItem(i, 1, false);
                            handler.fill(fluid, true);
                            this.cooldownProcess = MathHelper.clamp(20 * filled / VReference.FOOD_TO_FLUID_BLOOD, 20, 100);
                        }
                    }
                }
            }
        }
    }

    private boolean updatePull() {
        if (!isFull()) {
            boolean flag = pullItems();
            if (flag) {
                this.cooldownPull = 20;
            }
            return flag;
        }
        return false;
    }
}
