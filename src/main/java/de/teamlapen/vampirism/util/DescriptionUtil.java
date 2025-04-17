package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class DescriptionUtil {

    public static void addDescriptionTooltip(String key, String modId, List<Component> tooltipComponents, Object... parameters) {
        if (key.isEmpty()) return;

        tooltipComponents.add(Component.translatable("tooltip.vampirism.hold_shift_for_info").withStyle(ChatFormatting.DARK_GRAY));

        if (Screen.hasShiftDown()) {
            List<String> lines = normalizeTextWidth(Component.translatable(getTranslationKey(modId, key), parameters).getString(), 40);

            tooltipComponents.add(Component.empty());
            for (String line : lines) {
                tooltipComponents.add(Component.literal(line).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static void addDescriptionTooltip(String name, List<Component> tooltipComponents, Object... parameters) {
        addDescriptionTooltip(name, REFERENCE.MODID, tooltipComponents, parameters);
    }

    public static void addDescriptionTooltip(String name, List<Component> tooltipComponents) {
        addDescriptionTooltip(name, REFERENCE.MODID, tooltipComponents);
    }

    private static String getTranslationKey(String modId, String key) {
        return "tooltip." + modId + "." + key;
    }

    private static List<String> normalizeTextWidth(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        StringBuilder line = new StringBuilder();

        for (String word : text.split(" ")) {
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

        return lines;
    }
}
