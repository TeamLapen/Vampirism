package de.teamlapen.vampirism.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IBlockWithDescription {

    void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext context, TooltipDisplay display, TooltipFlag tooltipFlag, Consumer<Component> tooltips);
}
