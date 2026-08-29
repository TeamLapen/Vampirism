package de.teamlapen.faction.client.entity;

import de.teamlapen.faction.common.components.FactionRestriction;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ClientEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemToolTip(ItemTooltipEvent event) {
        FactionRestriction.addTooltipIfExist(event.getEntity(), event.getItemStack(), event.getToolTip()::add);
    }
}
