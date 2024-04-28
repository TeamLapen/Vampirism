package de.teamlapen.lib.lib.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Fluid related helper methods
 */
public class FluidLib {



    public static @NotNull Optional<IFluidHandlerItem> getFluidItemCap(@NotNull ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(Capabilities.FluidHandler.ITEM, null));
    }

    public static int getFluidAmount(@NotNull IFluidHandler handler, @Nullable Fluid f) {
        FluidStack s = f == null ? handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE) : handler.drain(new FluidStack(f, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
        return s.getAmount();
    }


}
