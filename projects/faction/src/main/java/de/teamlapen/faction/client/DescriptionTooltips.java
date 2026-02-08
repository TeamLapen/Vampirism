package de.teamlapen.faction.client;

import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.util.BlockDescription;
import de.teamlapen.faction.common.util.ShiftDescription;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class DescriptionTooltips {

    @SubscribeEvent
    public void onToolTip(ItemTooltipEvent event) {
        TooltipDisplay orDefault = event.getItemStack().getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);

        if (orDefault.shows(FactionDataComponents.BLOCK_DESCRIPTION.get()) && event.getItemStack().get(FactionDataComponents.BLOCK_DESCRIPTION) instanceof BlockDescription blockDescription) {
            blockDescription.addTooltips(event.getItemStack(), event.getContext(), orDefault, event.getFlags(), event.getToolTip()::add);
        }

        if (orDefault.shows(FactionDataComponents.SHIFT_DESCRIPTION.get()) && event.getItemStack().get(FactionDataComponents.SHIFT_DESCRIPTION) instanceof ShiftDescription shiftDescription) {
            shiftDescription.addTooltips(event.getItemStack(), event.getEntity(), event.getContext(), event.getFlags(), event.getToolTip()::add);
        }
    }
}
