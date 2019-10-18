package de.teamlapen.vampirism.modcompat;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.ISundamageRegistry;
import de.teamlapen.vampirism.api.entity.IVampirismEntityRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IMCHandler {
    private final static Logger LOGGER = LogManager.getLogger();

    public static void handleInterModMessage(InterModProcessEvent event) {
        IVampirismEntityRegistry entityRegistry = VampirismAPI.entityRegistry();
        ISundamageRegistry sundamageRegistry = VampirismAPI.sundamageRegistry();
        event.getIMCStream("nosundamage-biome"::equals).forEach(msg -> {
            Object value = msg.getMessageSupplier().get();
            if (value instanceof ResourceLocation) {
                LOGGER.info("Received no sundamage biome {} from {}", value, msg.getSenderModId());
                sundamageRegistry.addNoSundamageBiome((ResourceLocation) value);
            } else {
                LOGGER.error("Received invalid nosundamage-biome thing {} from {}", value, msg.getSenderModId());
            }
        });
    }
}
