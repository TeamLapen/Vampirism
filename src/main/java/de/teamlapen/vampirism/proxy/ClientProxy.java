package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.client.core.ModBlocksRender;
import de.teamlapen.vampirism.client.core.ModEntitiesRender;
import de.teamlapen.vampirism.client.core.ModItemsRender;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Clientside Proxy
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ModBlocksRender.preInit(event);
        ModItemsRender.preInit(event);
        ModEntitiesRender.preInit(event);
        ModKeys.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModBlocksRender.init(event);
        ModItemsRender.init(event);
        ModEntitiesRender.init(event);
        ModKeys.init(event);

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
