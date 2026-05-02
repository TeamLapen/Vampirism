package de.teamlapen.faction.client.integration;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@EventBusSubscriber
public class AppleSkinCompat {

    private static final String MOD_ID = "appleskin";

    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded(MOD_ID)) {
            NeoForge.EVENT_BUS.register(AppleSkinEventHandler.class);
        }
    }
}
