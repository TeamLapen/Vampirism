package de.teamlapen.vampirism.data.reloadlistener;

import de.teamlapen.factions.data.listener.SkillTreeReloadListener;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

public class ModReloadListeners {

    @SubscribeEvent
    public void onAddReloadListenerEvent(AddServerReloadListenersEvent event) {
        event.addListener(SingleJigsawReloadListener.SINGLE_JIGSAW_ID, new SingleJigsawReloadListener());
        event.addListener(SundamageReloadListener.SUNDAMAGE_ID, new SundamageReloadListener(event.getRegistryAccess()));
        event.addListener(SkillTreeReloadListener.SKILL_TREE_ID, new SkillTreeReloadListener());
    }
}
