package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Fluid handler capability for blood bottles.
 * Only allows storing fluid amounts that are a multiple of {@link VReference#FOOD_TO_FLUID_BLOOD}
 */
public class BloodBottleFluidHandler implements IFluidHandlerItem, ICapabilityProvider {

    public static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);


    /**
     * Returns a amount which is a multiple of capacity%10
     *
     * @param amt
     * @return
     */
    public static int getAdjustedAmount(int amt) {
        return amt - amt % MULTIPLIER;
    }

    private final int capacity;
    @Nonnull
    protected ItemStack container;

    public BloodBottleFluidHandler(@Nonnull ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (container.getCount() != 1 || resource == null || resource.amount <= 0 || !ModFluids.blood.equals(resource.getFluid())) {
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
            /*
             might cause crashes with other mods, although this is probably legit as forge does something similar in {@link net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.SwapEmpty}
             */
            if (getBlood(container) == 0 && Configs.autoConvertGlasBottles) {
                container = new ItemStack(Items.GLASS_BOTTLE);
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
        return stack.getItem() == ModItems.blood_bottle ? stack.getDamage() * MULTIPLIER : 0;
    }


    @Nonnull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[]{new FluidTankProperties(new FluidStack(ModFluids.blood, getBlood(container)), capacity)};
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, holder);

    }

    public void setBlood(ItemStack stack, int amt) {
        stack.setDamage(amt / MULTIPLIER);
    }
}
