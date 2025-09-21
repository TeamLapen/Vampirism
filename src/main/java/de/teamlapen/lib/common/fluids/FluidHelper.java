package de.teamlapen.lib.common.fluids;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FluidHelper {


    public static int getFluidAmount(@NotNull IFluidHandler handler, @Nullable Fluid f) {
        FluidStack s = f == null ? handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE) : handler.drain(new FluidStack(f, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE);
        return s.getAmount();
    }


}
