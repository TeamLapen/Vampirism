package de.teamlapen.lib.lib.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fluid related helper methods
 */
public class FluidLib {


    /**
     * Checks if either both stacks are null or if they are equal. DOES NOT CHECK AMOUNTS
     */
    public static boolean areFluidStacksEqual(@Nullable FluidStack stackA, @Nullable FluidStack stackB) {
        return stackA == null && stackB == null || ((stackA != null && stackB != null) && stackA.isFluidEqual(stackB));
    }

    /**
     * Checks if either both stacks are null or if they are identical. DOES  CHECK AMOUNTS
     */
    public static boolean areFluidStacksIdentical(@Nullable FluidStack stackA, @Nullable FluidStack stackB) {
        return stackA == null && stackB == null || ((stackA != null && stackB != null) && stackA.isFluidStackIdentical(stackB));
    }


    public static @NotNull LazyOptional<IFluidHandlerItem> getFluidItemCap(@NotNull ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null);
    }

    public static int getFluidAmount(@NotNull IFluidHandler handler, @Nullable Fluid f) {
        FluidStack s = f == null ? handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE) : handler.drain(new FluidStack(f, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
        return s.getAmount();
    }


}
