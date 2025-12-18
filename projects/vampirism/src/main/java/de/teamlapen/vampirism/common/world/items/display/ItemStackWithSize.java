package de.teamlapen.vampirism.common.world.items.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public record ItemStackWithSize(Holder<Item> item, int amount) implements SlotDisplay {

    public static final MapCodec<ItemStackWithSize> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Item.CODEC.fieldOf("item").forGetter(ItemStackWithSize::item),
            Codec.INT.fieldOf("amount").forGetter(ItemStackWithSize::amount)
    ).apply(inst, ItemStackWithSize::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackWithSize> STREAM_CODEC = StreamCodec.composite(
            Item.STREAM_CODEC, ItemStackWithSize::item,
            ByteBufCodecs.INT, ItemStackWithSize::amount,
            ItemStackWithSize::new
    );

    @Override
    public <T> @NotNull Stream<T> resolve(@NotNull ContextMap context, @NotNull DisplayContentsFactory<T> output) {
        return output instanceof DisplayContentsFactory.ForStacks<T> forStacks ? Stream.of(forStacks.forStack(new ItemStack(item.value(), amount))) : Stream.empty();
    }

    @Override
    @NotNull
    public Type<? extends SlotDisplay> type() {
        return ModItems.ITEMSTACK_WITH_SIZE.get();
    }

    @Override
    public boolean isEnabled(@NotNull FeatureFlagSet enabledFeatures) {
        return this.item.value().isEnabled(enabledFeatures);
    }
}
