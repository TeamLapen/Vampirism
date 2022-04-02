package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import de.teamlapen.vampirism.world.biome.VampirismBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * For new dynamic registry related things see {@link VampirismBiomes} and {@link OverworldModifications}
 */
public class ModBiomes {
    public static final ResourceKey<Biome> VAMPIRE_FOREST = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));

    static void registerBiomes(IForgeRegistry<Biome> registry) {
        registry.register(VampirismBiomes.createVampireForest().setRegistryName(VAMPIRE_FOREST.location()));

        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST.location());

        BiomeDictionary.addTypes(VAMPIRE_FOREST, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY);
    }

}
