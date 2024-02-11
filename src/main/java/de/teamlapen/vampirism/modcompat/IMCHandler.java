package de.teamlapen.vampirism.modcompat;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class IMCHandler {
    private final static Logger LOGGER = LogManager.getLogger();
    public static boolean requestedToDisableBloodbar = false;

    public static void handleInterModMessage(@NotNull InterModProcessEvent event) {
        IVampirismEntityRegistry entityRegistry = VampirismAPI.entityRegistry();
        ISundamageRegistry sundamageRegistry = VampirismAPI.sundamageRegistry();
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
}
