package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.client.core.ModBlocksRender;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.core.ModItemsRender;
import de.teamlapen.vampirism.client.core.ModKeys;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Clientside Proxy
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        ModBlocksRender.onInitStep(step, event);
        ModItemsRender.onInitStep(step, event);
        ModEntitiesRender.onInitStep(step, event);
        ModKeys.onInitStep(step, event);
    }
}
