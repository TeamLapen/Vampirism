package de.teamlapen.faction.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.world.blocks.IBlockWithDescription;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public record BlockDescription() {

    public static final BlockDescription INSTANCE = new BlockDescription();

    public static final Codec<BlockDescription> CODEC = MapCodec.unitCodec(INSTANCE);

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockDescription> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public void addTooltips(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, TooltipFlag tooltipFlag, Consumer<Component> tooltipAdder) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IBlockWithDescription block) {
            block.appendHoverText(stack, context, tooltipDisplay, tooltipFlag, tooltipAdder);
        }
    }
}
