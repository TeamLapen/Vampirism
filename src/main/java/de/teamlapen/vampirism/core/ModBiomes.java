package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.biome.BiomeGenVampireForest;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
            default://Do nothing
        }

    }

    private static void preInit() {

        vampireForest = new BiomeGenVampireForest();
        vampireForest.setRegistryName(REFERENCE.MODID, "vampireForest");
        VampirismAPI.sundamageRegistry().addNoSundamageBiome(vampireForest.getBiomeClass());
        GameRegistry.register(vampireForest);
        BiomeDictionary.addTypes(vampireForest, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
        if (!Configs.disable_vampireForest) {

            int weight = Balance.general.VAMPIRE_FOREST_WEIGHT;
            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampireForest, weight));
            VampirismMod.log.d("ModBiomes", "Registered vampire forest with weight %d", weight);
        }


    }


}
