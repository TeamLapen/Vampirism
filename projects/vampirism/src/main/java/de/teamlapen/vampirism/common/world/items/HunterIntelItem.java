package de.teamlapen.vampirism.common.world.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Item used in the hunter leveling process. Is created in a hunter table.
 */
public class HunterIntelItem extends Item {

    private final int level;

    public HunterIntelItem(int level, Properties properties) {
        super(properties.vampirism$descriptionWithout("_\\d"));
        this.level = level;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltips, TooltipFlag flagIn) {
        tooltips.accept(Component.translatable("text.vampirism.for_up_to_level").append(Component.literal(": " + (level + 5))).withStyle(ChatFormatting.RED));
    }

    public Component getCustomName() {
        return Component.translatable(this.getDescriptionId()).append(Component.literal(" ")).append(Component.translatable("text.vampirism.for_up_to_level").append(Component.literal(" " + (level + 5))));
    }

    public int getLevel() {
        return level;
    }

    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
