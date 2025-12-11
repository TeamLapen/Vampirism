package de.teamlapen.factions.client.entity;

import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.skills.ClientSkillTreeData;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ClientEventHandler {

    @SubscribeEvent
    public void onJoined(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSkillTreeData.reset();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemToolTip(ItemTooltipEvent event) {
        FactionRestriction.addTooltipIfExist(event.getEntity(), event.getItemStack(), event.getToolTip()::add);
    }
}
