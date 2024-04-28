package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.core.ModDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public record ContainedFluid(FluidStack fluid) {

    public static final ContainedFluid EMPTY = new ContainedFluid(FluidStack.EMPTY);
    public static final Codec<ContainedFluid> CODEC = FluidStack.CODEC.xmap(ContainedFluid::new, ContainedFluid::fluid);

    public static final StreamCodec<RegistryFriendlyByteBuf, ContainedFluid> STREAM_CODEC = FluidStack.STREAM_CODEC.map(ContainedFluid::new, ContainedFluid::fluid);

    @NotNull
    public static FluidStack get(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BLOOD_CONTAINER, EMPTY).fluid();
    }
}
