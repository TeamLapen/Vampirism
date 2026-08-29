package de.teamlapen.vampirism.data.reloadlistener;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

public class ModReloadListeners {

    @SubscribeEvent
    public void onAddReloadListenerEvent(AddServerReloadListenersEvent event) {
        event.addListener(SingleJigsawReloadListener.SINGLE_JIGSAW_ID, new SingleJigsawReloadListener());
        event.addListener(SundamageReloadListener.SUNDAMAGE_ID, new SundamageReloadListener());
    }
}
