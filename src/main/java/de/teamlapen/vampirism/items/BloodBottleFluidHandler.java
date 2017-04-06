package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModFluids;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;


/**
 * Fluid handler capability for blood bottles.
 * Only allows storing fluid amounts that are a multiple of {@link VReference#FOOD_TO_FLUID_BLOOD}
 */
public class BloodBottleFluidHandler implements IFluidHandler, ICapabilityProvider {

    public static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;

    /**
     * Returns a amount which is a multiple of capacity%10
     *
     * @param amt
     * @return
     */
    public static int getAdjustedAmount(int amt) {
        return amt - amt % MULTIPLIER;
    }
    protected final ItemStack container;
    private final ItemStack GLAS_BOTTLE = new ItemStack(Items.GLASS_BOTTLE);
    private final int capacity;

    public BloodBottleFluidHandler(ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (container.stackSize != 1 || resource == null || resource.amount <= 0 || !ModFluids.blood.equals(resource.getFluid())) {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        int currentAmt = getBlood(container);
        if (currentAmt == 0) return null;
        FluidStack stack = new FluidStack(ModFluids.blood, Math.min(currentAmt, getAdjustedAmount(maxDrain)));
        if (doDrain) {
            setBlood(container, currentAmt - stack.amount);
            /**
             might cause crashes with other mods, although this is probably legit as forge does something similar in {@link net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.SwapEmpty}
             */
            if (getBlood(container) == 0 && Configs.autoConvertGlasBottles) {
                VampirismMod.log.d("BloodBottle", "Replaced blood bottle by glas bottle, during IFluidContainerItem#drain.");
                container.deserializeNBT(GLAS_BOTTLE.serializeNBT());
            }
        }
        return stack;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null) return 0;
        if (!resource.getFluid().equals(ModFluids.blood)) {
            return 0;
        }
        if (!doFill) {
            return Math.min(capacity - getBlood(container), getAdjustedAmount(resource.amount));
        }

        int itemamt = getBlood(container);
        int toFill = Math.min(capacity - itemamt, getAdjustedAmount(resource.amount));
        setBlood(container, itemamt + toFill);
        return toFill;
    }

    public int getBlood(ItemStack stack) {
        return stack.getItem() instanceof ItemBloodBottle ? stack.getItemDamage() * MULTIPLIER : 0;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[]{new FluidTankProperties(new FluidStack(ModFluids.blood, getBlood(container)), capacity)};
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    public void setBlood(ItemStack stack, int amt) {
        stack.setItemDamage(amt / MULTIPLIER);
    }
}
