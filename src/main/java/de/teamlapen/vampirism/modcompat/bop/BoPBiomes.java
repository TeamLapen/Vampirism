package de.teamlapen.vampirism.modcompat.bop;

import biomesoplenty.api.biome.BOPBiomes;
import de.teamlapen.vampirism.api.VampirismAPI;

/**
 * Handle no sundamage biomes for BOP
 */
public class BoPBiomes {

    static void registerNoSundamageBiomes(BoPModCompat compat) {
        if (compat.disabled_sundamage_ominous_woods && BOPBiomes.ominous_woods.isPresent()) {
            VampirismAPI.sundamageRegistry().addNoSundamageBiome(BOPBiomes.ominous_woods.get().getBiomeClass());
        }
    }
}
