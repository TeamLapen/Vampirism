package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.components.IContainedFluid;
import de.teamlapen.vampirism.core.ModDataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public record ContainedFluid(FluidStack fluid) implements IContainedFluid {

    public static final ContainedFluid EMPTY = new ContainedFluid(FluidStack.EMPTY);
    public static final Codec<ContainedFluid> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidStack.CODEC.fieldOf("fluid").forGetter(ContainedFluid::fluid)
    ).apply(instance, ContainedFluid::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ContainedFluid> STREAM_CODEC = StreamCodec.composite(FluidStack.STREAM_CODEC, ContainedFluid::fluid, ContainedFluid::new);

    public static FluidStack get(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BLOOD_CONTAINER, EMPTY).fluid();
    }

    @Override
    public FluidStack fluid() {
        return fluid.copy();
    }
}
