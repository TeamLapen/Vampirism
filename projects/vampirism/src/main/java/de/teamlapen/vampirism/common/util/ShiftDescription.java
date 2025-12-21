package de.teamlapen.vampirism.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public record ShiftDescription() {

    public static final ShiftDescription INSTANCE = new ShiftDescription();

    public static final Codec<ShiftDescription> CODEC = MapCodec.unitCodec(INSTANCE);

    public static final StreamCodec<RegistryFriendlyByteBuf, ShiftDescription> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public void addTooltips(ItemStack stack, @Nullable Player player, Item.TooltipContext context, TooltipDisplay tooltipDisplay, TooltipFlag tooltipFlag, Consumer<Component> tooltipAdder, Object[] params) {
        DescriptionUtil.addDescriptionTooltip(stack.getItem(), context, tooltipDisplay, tooltipFlag, tooltipAdder, params);
    }
}
