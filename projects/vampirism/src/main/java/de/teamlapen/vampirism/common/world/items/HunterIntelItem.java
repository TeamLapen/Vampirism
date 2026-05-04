package de.teamlapen.vampirism.common.world.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class HunterIntelItem extends Item {

    private final int level;

    public HunterIntelItem(int level, Properties properties) {
        super(properties.factions$descriptionWithout("_\\d"));
        this.level = level;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltips, TooltipFlag flagIn) {
        tooltips.accept(Component.translatable("tooltip.vampirism.for_level", level + 5).withStyle(ChatFormatting.RED));
    }

    public int getLevel() {
        return level;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
