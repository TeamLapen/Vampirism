package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class RitualKnifeItem extends Item {
    public RitualKnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        if (itemStack.getOrDefault(ModDataComponents.CHARGED_RITUAL_KNIFE, false)) {
            builder.accept(Component.translatable("tooltip.vampirism.ritual_knife.infused").withStyle(ChatFormatting.DARK_RED));
        }
    }
}
