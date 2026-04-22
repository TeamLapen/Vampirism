package de.teamlapen.faction.common.world.fluids;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

public class FluidContent implements DataComponentHolder {

    public final FluidStackTemplate fluidStack;

    private FluidContent(FluidStackTemplate fluidStack) {
        this.fluidStack = fluidStack;
    }

    public static FluidContent of(FluidStackTemplate fluidStack) {
        return new FluidContent(fluidStack);
    }

    public static FluidContent of(Fluid fluid, int amount) {
        return new FluidContent(new FluidStackTemplate(fluid, amount));
    }

    public static FluidContent of(FluidStack stack) {
        return new FluidContent(new FluidStackTemplate(stack.getFluid(), stack.getAmount()));
    }
}
