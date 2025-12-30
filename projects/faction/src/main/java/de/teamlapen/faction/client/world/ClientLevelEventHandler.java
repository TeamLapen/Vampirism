package de.teamlapen.faction.client.world;

import de.teamlapen.faction.client.FactionsClientMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public class ClientLevelEventHandler {

    @SubscribeEvent
    public void onWorldClosed(LevelEvent.Unload event) {
        FactionsClientMod.services().bossInfoOverlay().clear();
    }
}
