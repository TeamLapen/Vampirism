package de.teamlapen.vampirism.client.renderer.tooltips;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

/**
 * Tooltip data for a table that compares the default and female lord titles of every lord level.
 *
 * @param defaultTitles default title per lord level, index 0 is level 1
 * @param femaleTitles  female title per lord level, index 0 is level 1
 * @param currentLevel  the player's current lord level, 0 if not a lord
 * @param female        whether the player currently uses the female titles
 */
public record LordTitlesTooltip(List<Component> defaultTitles, List<Component> femaleTitles, int currentLevel, boolean female) implements TooltipComponent {
}
