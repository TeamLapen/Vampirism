package de.teamlapen.vampirism.common.integration;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.world.entity.ISundamageRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class InterModHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean requestedToDisableBloodbar = false;

    private static final String NO_SUNDAMAGE_BIOME = "nosundamage-biome";
    private static final String NO_SUNDAMAGE_DIMENSION_TYPE = "nosundamage-dimensiontype";
    private static final String NO_SUNDAMAGE_DIMENSION = "nosundamage-dimension";

    @SubscribeEvent
    public void handleInterModMessage(@NotNull InterModProcessEvent event) {
        event.getIMCStream(NO_SUNDAMAGE_BIOME::equals).forEach(InterModHandler::onNoSundamageBiome);
        event.getIMCStream(NO_SUNDAMAGE_DIMENSION_TYPE::equals).forEach(InterModHandler::onNoSundamageDimensionType);
        event.getIMCStream(NO_SUNDAMAGE_DIMENSION::equals).forEach(InterModHandler::onNoSundamageDimension);
        event.getIMCStream("disable-blood-bar"::equals).findAny().ifPresent((a) -> {
            requestedToDisableBloodbar = true;
            LOGGER.warn("{} requested to not render the vampire blood bar", a.senderModId());
        });
    }

    public boolean isRequestedToDisableBloodbar() {
        return requestedToDisableBloodbar;
    }

    private static void onNoSundamageBiome(InterModComms.IMCMessage msg) {
        noSundamage(msg, Registries.BIOME, VampirismMod.services().sunDamageRegistry()::addNoSundamageBiomes);
    }
    private static void onNoSundamageDimensionType(InterModComms.IMCMessage msg) {
        noSundamage(msg, Registries.DIMENSION_TYPE, VampirismMod.services().sunDamageRegistry()::addNoSundamageDimensionTypes);
    }
    private static void onNoSundamageDimension(InterModComms.IMCMessage msg) {
        noSundamage(msg, Registries.DIMENSION, VampirismMod.services().sunDamageRegistry()::addNoSundamageDimension);
    }

    private static <T> void noSundamage(InterModComms.IMCMessage msg, ResourceKey<Registry<T>> registryResourceKey, Consumer<ResourceKey<T>> supplier) {
        Object value = msg.messageSupplier().get();
        if (value instanceof Identifier loc) {
            supplier.accept(ResourceKey.create(registryResourceKey, loc));
        } else {
            LOGGER.error("Received invalid nosundamage-dimension thing {} from {}", value, msg.senderModId());
        }
    }
}
