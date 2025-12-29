package de.teamlapen.factions.client.world;

import de.teamlapen.factions.client.FactionsClientMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public class ClientLevelEventHandler {

    @SubscribeEvent
    public void onWorldClosed(LevelEvent.Unload event) {
        FactionsClientMod.services().bossInfoOverlay().clear();
    }
}
