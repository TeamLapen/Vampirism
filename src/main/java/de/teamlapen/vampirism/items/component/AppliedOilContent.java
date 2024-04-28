package de.teamlapen.vampirism.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.components.IAppliedOilContent;
import de.teamlapen.vampirism.api.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModDataComponents;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record AppliedOilContent(Holder<IApplicableOil> oil, int duration) implements IAppliedOilContent {

    public static final Codec<AppliedOilContent> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ModRegistries.OILS.holderByNameCodec().validate(s -> s.value() instanceof IApplicableOil ? DataResult.success(s) : DataResult.error(() -> "only applicable oils are allowed")).xmap(s -> (Holder<IApplicableOil>)(Object)s, s -> (Holder<IOil>) (Object)s).fieldOf("oil").forGetter(o -> o.oil),
                    Codec.INT.fieldOf("duration").forGetter(o -> o.duration)
            ).apply(inst, AppliedOilContent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AppliedOilContent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.OIL).map(s -> (Holder<IApplicableOil>) (Object) s, l -> (Holder<IOil>) (Object) l), AppliedOilContent::oil, ByteBufCodecs.VAR_INT, AppliedOilContent::duration, AppliedOilContent::new
    );

    public static ItemStack apply(ItemStack stack, Holder<IApplicableOil> oil) {
        return apply(stack, oil, oil.value().getMaxDuration(stack));
    }

    public static ItemStack apply(ItemStack stack, Holder<IApplicableOil> oil, int duration) {
        if (duration <= 0) {
            return remove(stack);
        }
        stack.set(ModDataComponents.APPLIED_OIL, new AppliedOilContent(oil, duration));
        return stack;
    }

    public static ItemStack remove(ItemStack stack) {
        stack.remove(ModDataComponents.APPLIED_OIL);
        return stack;
    }

    public static Optional<AppliedOilContent> getAppliedOil(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModDataComponents.APPLIED_OIL));
    }
}
