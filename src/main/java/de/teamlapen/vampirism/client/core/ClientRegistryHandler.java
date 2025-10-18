package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.models.items.properties.BloodFilled;
import de.teamlapen.vampirism.client.models.items.properties.ClipFilled;
import de.teamlapen.vampirism.client.models.items.properties.HasName;
import de.teamlapen.vampirism.client.renderer.tooltips.QuarrelPouchClientTooltip;
import de.teamlapen.vampirism.common.items.tooltip.QuarrelPouchTooltip;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import org.jetbrains.annotations.ApiStatus;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientRegistryHandler {

    @ApiStatus.Internal
    public static void init(IEventBus modbus) {

        modbus.addListener(ModEntitiesRender::onRegisterRenderers);
        modbus.addListener(ModEntitiesRender::onRegisterLayers);
        modbus.addListener(ModEntitiesRender::onAddLayers);
        modbus.addListener(ModBlocksRender::registerBlockEntityRenderers);
        modbus.addListener(ModScreens::registerScreenOverlays);
        modbus.addListener(ModScreens::registerScreens);
        modbus.addListener(ModBlocksRender::registerBlockColors);
        modbus.addListener(ModItemsRender::registerColors);
        modbus.addListener(ModParticleFactories::registerFactories);
        modbus.addListener(ModKeys::registerKeyMapping);
        modbus.addListener(ClientEventHandler::onModelRegistry);
        modbus.addListener(ModItemsRender::registerItemDecorator);
        modbus.addListener(ModClientEffects::registerClientExtensions);
        modbus.addListener(ModBlocksRender::registerClientExtensions);
        modbus.addListener(ModClientFluids::registerClientExtensions);
        modbus.addListener(ModItemsRender::registerClientExtensions);
        modbus.addListener(ModBlocksRender::registerAdditionalModels);
    }

    @SubscribeEvent
    public static void registerRangeSelector(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(BloodFilled.ID, BloodFilled.CODEC);
        event.register(ClipFilled.ID, ClipFilled.CODEC);
    }

    @SubscribeEvent
    public static void registerTooltipRenderer(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(QuarrelPouchTooltip.class, QuarrelPouchClientTooltip::new);
    }

    @SubscribeEvent
    public static void registerConditional(RegisterConditionalItemModelPropertyEvent event) {
        event.register(HasName.ID, HasName.CODEC);
    }
}
