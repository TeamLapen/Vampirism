package de.teamlapen.vampirism.client;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.config.ConfigFilter;
import de.teamlapen.vampirism.client.renderer.items.BatCageSpecialRenderer;
import de.teamlapen.vampirism.client.renderer.items.BloodContainerRenderer;
import de.teamlapen.vampirism.client.renderer.items.MotherTrophyRenderer;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.proxy.IProxy;
import net.minecraft.client.renderer.Sheets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod(value = REFERENCE.MODID, dist = Dist.CLIENT)
public class VampirismModClient {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ClientServices SERVICES;

    public VampirismModClient(IEventBus modEventBus, ModContainer modContainer) {
        SERVICES = new ClientServices(modContainer);
        SERVICES.register(modEventBus);

        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) -> new ConfigurationScreen(container, parent, new ConfigFilter()));

        modEventBus.register(this);

        if (ShaderHandler.isOptifineLoaded()) {
            LOGGER.warn("Using Optifine. Expect visual glitches and reduces blood vision functionality if using shaders.");
        }
    }

    public static ClientServices services() {
        return SERVICES;
    }

    @SubscribeEvent
    public void setupClient(@NotNull FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Sheets.addWoodType(ModBlocks.WoodTypes.DARK_SPRUCE);
            Sheets.addWoodType(ModBlocks.WoodTypes.CURSED_SPRUCE);
        });
    }

    public static IProxy getProxy() {
        return new ClientProxy();
    }

    @SubscribeEvent
    public void onRegisterSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
        event.register(MotherTrophyRenderer.ID, MotherTrophyRenderer.Unbaked.MAP_CODEC);
        event.register(BloodContainerRenderer.ID, BloodContainerRenderer.Unbaked.MAP_CODEC);
        event.register(BatCageSpecialRenderer.ID, BatCageSpecialRenderer.Unbaked.MAP_CODEC);
    }
}
