package de.teamlapen.vampirism.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.common.world.biomes.OverworldModifications;
import de.teamlapen.vampirism.common.world.biomes.VampirismBiomes;
import de.teamlapen.vampirism.common.world.features.VampirismFeatures;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * For new dynamic registry related things see {@link VampirismBiomes} and {@link OverworldModifications} {@link VampirismFeatures}
 */
public class ModBiomes {

    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, REFERENCE.MODID);

    public static final ResourceKey<Biome> VAMPIRE_FOREST = ResourceKey.create(Registries.BIOME, VResourceLocation.mod("vampire_forest"));


    static void register(IEventBus bus) {
        BIOME_MODIFIER_SERIALIZERS.register(bus);
    }

    static void createBiomes(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(ModBiomes.VAMPIRE_FOREST, VampirismBiomes.createVampireForest(placedFeatures, configuredCarvers));
    }
}
