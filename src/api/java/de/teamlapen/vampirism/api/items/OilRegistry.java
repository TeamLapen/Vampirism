package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

class OilRegistry {

    private static IForgeRegistry<IOil> oil_registry;

    /**
     * This only exists to access the oil registry from the api.
     * <p>
     * This is not a public api and will no longer exist in newer minecraft versions
     */
    @Deprecated
    static IForgeRegistry<IOil> getOilRegistry() {
        if (oil_registry == null) {
            oil_registry = RegistryManager.ACTIVE.getRegistry(IOil.class);
        }
        return oil_registry;
    }
}
