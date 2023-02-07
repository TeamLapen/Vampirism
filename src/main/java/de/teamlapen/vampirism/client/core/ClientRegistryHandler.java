package de.teamlapen.vampirism.client.core;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

public class ClientRegistryHandler {

    @ApiStatus.Internal
    public static void init(){
        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        modbus.addListener(ClientEventHandler::onModelBakeRequest);
        modbus.addListener(ClientEventHandler::onModelBakeEvent);
        modbus.addListener(ModEntitiesRender::onRegisterRenderers);
        modbus.addListener(ModEntitiesRender::onRegisterLayers);
        modbus.addListener(ModEntitiesRender::onAddLayers);
        modbus.addListener(ModBlocksRender::registerBlockEntityRenderers);
        modbus.addListener(ModScreens::registerScreenOverlays);
        modbus.addListener(ModBlocksRender::registerBlockColors);
        modbus.addListener(ModItemsRender::registerColors);
        modbus.addListener(ModParticleFactories::registerFactories);
        modbus.addListener(ModKeys::registerKeyMapping);
        modbus.addListener(ClientEventHandler::onModelRegistry);
        modbus.addListener(ModItemsRender::registerItemDecorator);
    }
}
