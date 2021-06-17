package de.teamlapen.vampirism.api;

import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Internal handler for client class access in API
 */
class ClientHelper {

    /**
     * @return The client world if it matches the given dimension key
     */
    @Nullable
    static World getAndCheckWorld(RegistryKey<World> dimension) {
        World clientWorld = Minecraft.getInstance().world;
        if (clientWorld != null) {
            if (clientWorld.getDimensionKey().equals(dimension)) {
                return clientWorld;
            }
        }
        return null;
    }
}
