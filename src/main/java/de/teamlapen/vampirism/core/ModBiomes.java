package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import de.teamlapen.vampirism.world.biome.VampirismBiomes;
import de.teamlapen.vampirism.world.gen.modifier.ExtendedAddSpawnsBiomeModifier;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * For new dynamic registry related things see {@link VampirismBiomes} and {@link OverworldModifications} {@link de.teamlapen.vampirism.world.gen.VampirismFeatures}
 */
public class ModBiomes {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, REFERENCE.MODID);

    public static final RegistryObject<Codec<ExtendedAddSpawnsBiomeModifier>> ADD_SPAWNS_BIOME_MODIFIER_TYPE = BIOME_MODIFIER_SERIALIZERS.register("extended_add_spawns", () -> ExtendedAddSpawnsBiomeModifier.CODEC);

    public static final ResourceKey<Biome> VAMPIRE_FOREST = ResourceKey.create(Registries.BIOME, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));


    static void register(IEventBus bus) {
        BIOME_MODIFIER_SERIALIZERS.register(bus);
    }

    public static void createBiomes(BootstapContext<Biome> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> configuredCarvers = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(ModBiomes.VAMPIRE_FOREST, VampirismBiomes.createVampireForest(placedFeatures, configuredCarvers));
    }
}
