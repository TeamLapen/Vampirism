package de.teamlapen.vampirism.common.util;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

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

    public static void addDescriptionTooltip(ResourceLocation itemLocation, List<Component> tooltipComponents, Object... parameters) {
        addDescriptionTooltip(itemLocation.getPath(), itemLocation.getNamespace(), tooltipComponents, parameters);
    }

    public static void addDescriptionTooltip(ItemLike item, List<Component> tooltipComponents, Object... parameters) {
        addDescriptionTooltip(BuiltInRegistries.ITEM.getKey(item.asItem()), tooltipComponents, parameters);
    }

    private static String getTranslationKey(String modId, String key) {
        return "tooltip." + modId + "." + key;
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
