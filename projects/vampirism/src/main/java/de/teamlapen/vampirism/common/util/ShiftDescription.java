package de.teamlapen.vampirism.common.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.world.blocks.IDescriptionProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public record ShiftDescription(Either<Component, String> component) {

    public static final Codec<ShiftDescription> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            new EitherCodec<>(ComponentSerialization.CODEC, Codec.STRING).fieldOf("component").forGetter(ShiftDescription::component)
    ).apply(inst, ShiftDescription::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShiftDescription> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.either(ComponentSerialization.STREAM_CODEC, ByteBufCodecs.STRING_UTF8), ShiftDescription::component,
            ShiftDescription::new
    );

    public ShiftDescription(Component component) {
        this(Either.left(component));
    }

    public ShiftDescription(String string) {
        this(Either.right(string));
    }

    public void addTooltips(ItemStack stack, @Nullable Player player, Item.TooltipContext context, TooltipFlag tooltipFlag, Consumer<Component> tooltipAdder) {
        Component description = component.map(x -> x, x -> {
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IDescriptionProvider provider) {
                return Component.translatable(x, provider.getDescriptionParameters());
            } else if (stack.getItem() instanceof IDescriptionProvider provider) {
                return Component.translatable(x, provider.getDescriptionParameters());
            } else {
                throw new IllegalStateException();
            }
        });
        DescriptionUtil.addDescriptionTooltip(description, context, tooltipFlag, tooltipAdder);
    }
}
