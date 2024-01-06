package de.teamlapen.vampirism.client.core;

import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

public class ClientRegistryHandler {

    @ApiStatus.Internal
    public static void init(IEventBus modbus){

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
        modbus.addListener(ClientEventHandler::registerPackRepository);
        modbus.addListener(ClientEventHandler::registerReloadListener);
        modbus.addListener(ClientEventHandler::registerStageEvent);
    }
}
