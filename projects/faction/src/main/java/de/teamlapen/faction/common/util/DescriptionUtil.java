package de.teamlapen.faction.common.util;

import com.mojang.blaze3d.platform.InputConstants;
import de.teamlapen.faction.common.core.FactionKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

public class DescriptionUtil {

    public static void addDescriptionTooltip(Component component, Item.TooltipContext context, TooltipFlag tooltipFlag, List<Component> tooltipComponents) {
        if (tooltipComponents.size() > 2) {
            tooltipComponents.add(Component.empty());
        }

        tooltipComponents.add(Component.translatable("tooltip.factionapi.hold_shift_for_info", FactionKeys.ITEM_DESCRIPTION.getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));

        int keyCode = FactionKeys.ITEM_DESCRIPTION.getKey().getValue();
        boolean isHeld = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), keyCode);

        if (isHeld) {
            List<String> lines = normalizeTextWidth(component.getString());

            tooltipComponents.add(Component.empty());
            for (String line : lines) {
                tooltipComponents.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static List<String> normalizeTextWidth(String text) {
        return normalizeTextWidth(text, 40);
    }

    public static List<String> normalizeTextWidth(String text, int maxLength) {
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
