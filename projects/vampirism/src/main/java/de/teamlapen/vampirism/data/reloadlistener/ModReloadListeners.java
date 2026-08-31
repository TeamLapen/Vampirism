package de.teamlapen.vampirism.data.reloadlistener;

import de.teamlapen.vampirism.data.reloadlistener.heritage.HeritageDefinitionReloadListener;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

import java.util.Optional;

public class ModReloadListeners {
    private final HeritageDefinitionReloadListener heritageDefinitions = new HeritageDefinitionReloadListener();

    @SubscribeEvent
    public void onAddReloadListenerEvent(AddServerReloadListenersEvent event) {
        event.addListener(SingleJigsawReloadListener.SINGLE_JIGSAW_ID, new SingleJigsawReloadListener());
        event.addListener(SundamageReloadListener.SUNDAMAGE_ID, new SundamageReloadListener());
        event.addListener(HeritageDefinitionReloadListener.ID, this.heritageDefinitions);
    }

    public Optional<Identifier> getHeritageDefinitionId(String namedNpc) {
        return this.heritageDefinitions.getIdForNpc(namedNpc);
    }
}
