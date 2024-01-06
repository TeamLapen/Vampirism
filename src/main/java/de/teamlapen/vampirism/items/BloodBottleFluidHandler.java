package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModFluids;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fluid handler capability for blood bottles.
 * Only allows storing fluid amounts that are a multiple of {@link VReference#FOOD_TO_FLUID_BLOOD}
 */
public class BloodBottleFluidHandler implements IFluidHandlerItem {

    public static final int MULTIPLIER = VReference.FOOD_TO_FLUID_BLOOD;

    /**
     * Returns an amount which is a multiple of capacity%10
     */
    public static int getAdjustedAmount(int amt) {
        return amt - amt % MULTIPLIER;
    }

    private final int capacity;
    @NotNull
    protected ItemStack container;

    public BloodBottleFluidHandler(@NotNull ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @NotNull
    @Override
    public FluidStack drain(@Nullable FluidStack resource, @NotNull FluidAction action) {
        if (container.getCount() != 1 || resource == null || resource.getAmount() <= 0 || ModFluids.BLOOD.get() != resource.getFluid()) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        int currentAmt = getBlood(container);
        if (currentAmt == 0) return FluidStack.EMPTY;
        FluidStack stack = new FluidStack(ModFluids.BLOOD.get(), Math.min(currentAmt, getAdjustedAmount(maxDrain)));
        if (action.execute()) {
            setBlood(container, currentAmt - stack.getAmount());
            /*
             might cause crashes with other mods, although this is probably legit as forge does something similar in {@link net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.SwapEmpty}
             */
            if (getBlood(container) == 0 && VampirismConfig.COMMON.autoConvertGlassBottles.get()) {
                container = new ItemStack(Items.GLASS_BOTTLE);
            }
        }
        return stack;
    }

    @Override
    public int fill(@Nullable FluidStack resource, @NotNull FluidAction action) {
        if (resource == null) return 0;
        if (!resource.getFluid().equals(ModFluids.BLOOD.get())) {
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

    public int getBlood(@NotNull ItemStack stack) {
        return stack.getItem() == ModItems.BLOOD_BOTTLE.get() ? stack.getDamageValue() * MULTIPLIER : 0;
    }

    @NotNull
    @Override
    public ItemStack getContainer() {
        return container;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return new FluidStack(ModFluids.BLOOD.get(), getBlood(container));
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
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return ModFluids.BLOOD.get().isSame(stack.getFluid());
    }

    public void setBlood(@NotNull ItemStack stack, int amt) {
        stack.setDamageValue(amt / MULTIPLIER);
    }
}
