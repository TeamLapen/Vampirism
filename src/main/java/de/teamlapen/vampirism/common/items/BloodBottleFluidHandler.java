package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.api.components.IBottleBlood;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFluids;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.items.component.BottleBlood;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;

import static de.teamlapen.vampirism.api.components.IBottleBlood.MULTIPLIER;

/**
 * Fluid handler capability for blood bottles.
 * Only allows storing fluid amounts that are a multiple of {@link IBottleBlood#MULTIPLIER}
 */
public class BloodBottleFluidHandler implements IFluidHandlerItem {

    private final int capacity;
    protected ItemStack container;

    public BloodBottleFluidHandler(ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    /**
     * Returns an amount which is a multiple of capacity%10
     */
    public static int getAdjustedAmount(int amt) {
        return amt - amt % MULTIPLIER;
    }

    @Override
    public FluidStack drain(@Nullable FluidStack resource, FluidAction action) {
        if (container.getCount() != 1 || resource == null || resource.getAmount() <= 0 || ModFluids.BLOOD.get() != resource.getFluid()) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int currentAmt = getBlood(container);
        if (currentAmt == 0) return FluidStack.EMPTY;
        FluidStack stack = new FluidStack(ModFluids.BLOOD.get(), Math.min(currentAmt, getAdjustedAmount(maxDrain)));
        if (action.execute()) {
            setBlood(container, currentAmt - stack.getAmount());
            /*
             might cause crashes with other mods, although this is probably legit as forge does something similar in {@link net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.SwapEmpty}
             */
            if (getBlood(container) == 0 && ModConfig.COMMON.autoConvertGlassBottles.get()) {
                container = new ItemStack(Items.GLASS_BOTTLE);
            }
        }
        return stack;
    }

    @Override
    public int fill(@Nullable FluidStack resource, FluidAction action) {
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

    public int getBlood(ItemStack stack) {
        return stack.getItem() == ModItems.BLOOD_BOTTLE.get() ? stack.getOrDefault(ModDataComponents.BOTTLE_BLOOD, BottleBlood.EMPTY).blood() * MULTIPLIER : 0;
    }

    @Override
    public ItemStack getContainer() {
        return container;
    }

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
    public boolean isFluidValid(int tank, FluidStack stack) {
        return ModFluids.BLOOD.get().isSame(stack.getFluid());
    }

    public void setBlood(ItemStack stack, int amt) {
        stack.set(ModDataComponents.BOTTLE_BLOOD, new BottleBlood(amt / MULTIPLIER));
    }
}
