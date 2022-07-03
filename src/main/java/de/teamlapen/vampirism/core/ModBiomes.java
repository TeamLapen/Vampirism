package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import de.teamlapen.vampirism.world.biome.VampirismBiomes;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * For new dynamic registry related things see {@link VampirismBiomes} and {@link OverworldModifications}
 */
public class ModBiomes {
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, REFERENCE.MODID);
    public static final RegistryObject<Biome> VAMPIRE_FOREST = BIOMES.register("vampire_forest", VampirismBiomes::createVampireForest);

    static void registerBiomes(IEventBus bus) {
        BIOMES.register(bus);
        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST.getKey().location());
    }

}
