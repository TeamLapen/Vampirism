package de.teamlapen.vampirism.modcompat.abyssalcraft;

import com.shinoow.abyssalcraft.api.biome.IDarklandsBiome;
import de.teamlapen.vampirism.api.VampirismAPI;

/**
 * Handle no sundamage registration for Abyssalcraft biomes
 */
class AbyssalCraftBiomes {

    static void registerNoSundamageBiomes(AbyssalCraftModCompat compat) {
        if (compat.disableSundamage_darklands) {
            VampirismAPI.sundamageRegistry().addNoSundamageBiome(IDarklandsBiome.class);

        }
    }
}
