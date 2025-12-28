package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DescriptionUtil {

    public static void addDescriptionTooltip(Component component, Item.TooltipContext context, TooltipFlag tooltipFlag, Consumer<Component> tooltipComponents) {

        tooltipComponents.accept(Component.translatable("tooltip.vampirism.hold_shift_for_info").withStyle(ChatFormatting.DARK_GRAY));

        if (tooltipFlag.hasShiftDown()) {
            List<String> lines = normalizeTextWidth(component.getString(), 40);

            tooltipComponents.accept(Component.empty());
            for (String line : lines) {
                tooltipComponents.accept(Component.literal(line).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static List<String> normalizeTextWidth(String text, int maxLength) {
        List<String> lines = new ArrayList<>();

        for (String paragraph : text.split("\n")) {
            StringBuilder line = new StringBuilder();

            for (String word : paragraph.split(" ")) {
                if (line.length() + word.length() + 1 > maxLength) {
                    if (!line.isEmpty()) {
                        lines.add(line.toString());
                        line = new StringBuilder();
                    }
                }

                if (!line.isEmpty()) {
                    line.append(" ");
                }

                line.append(word);
            }

            if (!line.isEmpty()) {
                lines.add(line.toString());
            }

            if (paragraph.isEmpty()) {
                lines.add("");
            }
        }

        return lines;
    }
}
