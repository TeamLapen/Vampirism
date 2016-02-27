package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.biome.BiomeGenVampireForest;
import de.teamlapen.vampirism.config.BalanceGeneral;
import de.teamlapen.vampirism.config.Configs;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Handles all biome registrations and reference.
 */
public class ModBiomes {

    public static BiomeGenVampireForest vampireForest;
    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                preInit();
                break;
        }

    }

    private static void preInit() {
        int biomeId = Configs.vampireForestId;
        if (biomeId == -1) {
            biomeId = 10;
            while (BiomeDictionary.isBiomeRegistered(biomeId)) {
                biomeId++;
            }
            Configs.updateVampireBiomeId(biomeId);

        }
        vampireForest = new BiomeGenVampireForest(biomeId);

        BiomeDictionary.registerBiomeType(vampireForest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        if (!Configs.disable_vampireForest) {
            int weight = BalanceGeneral.VAMPIRE_FOREST_WEIGHT;
            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampireForest, weight));
            VampirismMod.log.d("ModBiomes", "Registered vampire forest with id %d and weight %d", biomeId, weight);
        }



    }


}
