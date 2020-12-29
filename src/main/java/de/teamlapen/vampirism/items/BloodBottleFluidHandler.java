package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
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
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Fluid handler capability for blood bottles.
 * Only allows storing fluid amounts that are a multiple of {@link VReference#FOOD_TO_FLUID_BLOOD}
 */
public class BloodBottleFluidHandler implements IFluidHandlerItem, ICapabilityProvider {

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
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);
    private final int capacity;
    @Nonnull
    protected ItemStack container;

    public BloodBottleFluidHandler(@Nonnull ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource == null || resource.getAmount() <= 0 || !ModFluids.blood.equals(resource.getFluid())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int currentAmt = getBlood(container);
        if (currentAmt == 0) return FluidStack.EMPTY;
        FluidStack stack = new FluidStack(ModFluids.blood, Math.min(currentAmt, getAdjustedAmount(maxDrain)));
        if (action.execute()) {
            setBlood(container, currentAmt - stack.getAmount());
            /*
             might cause crashes with other mods, although this is probably legit as forge does something similar in {@link net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.SwapEmpty}
             */
            if (getBlood(container) == 0 && VampirismConfig.SERVER.autoConvertGlassBottles.get()) {
                container = new ItemStack(Items.GLASS_BOTTLE);
            }
        }
        return stack;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource == null) return 0;
        if (!resource.getFluid().equals(ModFluids.blood)) {
            return 0;
        }
        if (action.simulate()) {
            return Math.min(capacity - getBlood(container), getAdjustedAmount(resource.getAmount()));
        }
        int itemamt = getBlood(container);
        int toFill = Math.min(capacity - itemamt, getAdjustedAmount(resource.getAmount()));
        setBlood(container, itemamt + toFill);
        return toFill;
    }

    public int getBlood(ItemStack stack) {
        return stack.getItem() == ModItems.blood_bottle ? stack.getDamage() * MULTIPLIER : 0;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.orEmpty(cap, holder);

    }

    @Nonnull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return new FluidStack(ModFluids.blood, getBlood(container));
    }

    @Override
    public int getTankCapacity(int tank) {
        return BloodBottleItem.AMOUNT * MULTIPLIER;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return ModFluids.blood.isEquivalentTo(stack.getFluid());
    }

    public void setBlood(ItemStack stack, int amt) {
        stack.setDamage(amt / MULTIPLIER);
    }
}
