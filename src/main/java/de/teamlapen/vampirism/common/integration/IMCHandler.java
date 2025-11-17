package de.teamlapen.vampirism.common.integration;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class IMCHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean requestedToDisableBloodbar = false;

    @SubscribeEvent
    public void handleInterModMessage(@NotNull InterModProcessEvent event) {
        ISundamageRegistry sundamageRegistry = VampirismAPI.services().sundamageRegistry();
        event.getIMCStream("nosundamage-biome"::equals).forEach(msg -> {
            Object value = msg.messageSupplier().get();
            if (value instanceof ResourceLocation) {
                LOGGER.info("Received no sundamage biome {} from {}", value, msg.senderModId());
                sundamageRegistry.addNoSundamageBiomes(ResourceKey.create(Registries.BIOME, (ResourceLocation) value));
            } else {
                LOGGER.error("Received invalid nosundamage-biome thing {} from {}", value, msg.senderModId());
            }
        });
        event.getIMCStream("disable-blood-bar"::equals).findAny().ifPresent((a) -> {
            requestedToDisableBloodbar = true;
            LOGGER.warn("{} requested to not render the vampire blood bar", a.senderModId());
        });
    }

    public boolean isRequestedToDisableBloodbar() {
        return requestedToDisableBloodbar;
    }
}
