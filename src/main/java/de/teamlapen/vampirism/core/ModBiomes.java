package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.List;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all biome registrations and reference.
 */
public class ModBiomes {
    @ObjectHolder("vampirism:vampire_forest")
    public static final Biome vampire_forest = getNull();
    public static final RegistryKey<Biome> VAMPIRE_FOREST_KEY = RegistryKey.func_240903_a_(Registry.BIOME_KEY, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));


    static void registerBiomes(IForgeRegistry<Biome> registry) {
        MobSpawnInfo.Builder forestSpawnBuilder = new MobSpawnInfo.Builder();
        forestSpawnBuilder.func_242572_a(0.25f);
        forestSpawnBuilder.func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, BalanceMobProps.mobProps.VAMPIRE_SPAWN_CHANCE / 2, 1, 3));
        forestSpawnBuilder.func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire_baron, BalanceMobProps.mobProps.VAMPIRE_BARON_SPAWN_CHANCE, 1, 1));
        forestSpawnBuilder.func_242575_a(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(ModEntities.blinding_bat, BalanceMobProps.mobProps.BLINDING_BAT_SPAWN_CHANCE, 2, 4));
        forestSpawnBuilder.func_242575_a(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.dummy_creature, BalanceMobProps.mobProps.DUMMY_CREATURE_SPAWN_CHANCE, 3, 6));

        BiomeAmbience.Builder ambienceBuilder = new BiomeAmbience.Builder().setWaterColor(0xEE2505).setWaterFogColor(0xEE2505).setFogColor(0xE0A0A0).func_242539_d(0xA08080).func_242540_e(0x1E1F1F).func_242541_f(0x1E1F1F).setMoodSound(MoodSoundAmbience.field_235027_b_);
        registry.register(getVampireForestBuilder(0.1F, 0.025F, forestSpawnBuilder, ambienceBuilder).func_242455_a().setRegistryName(VAMPIRE_FOREST_KEY.func_240901_a_()));


        VampirismAPI.sundamageRegistry().addNoSundamageBiomes(VAMPIRE_FOREST_KEY.func_240901_a_());
    }

    /**
     * Only call from main thread / non parallel event
     */
    static void addBiomesToGeneratorUnsafe() {
        List<RegistryKey<Biome>> modList = new ArrayList<>(OverworldBiomeProvider.field_226847_e_);
        modList.add(RegistryKey.func_240903_a_(Registry.BIOME_KEY, new ResourceLocation(REFERENCE.MODID, "vampire_forest")));
        OverworldBiomeProvider.field_226847_e_ = ImmutableList.copyOf(modList);
        BiomeManager.addBiome(net.minecraftforge.common.BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ModBiomes.VAMPIRE_FOREST_KEY, VampirismConfig.BALANCE.vampireForestWeight.get()));
    }

    /**
     * Use only for adding to biome lists
     * <p>
     * Registered in mod constructor
     */
    public static void onBiomeLoadingEventAdditions(BiomeLoadingEvent event) {
        List<MobSpawnInfo.Spawners> monsterList = event.getSpawns().getSpawner(EntityClassification.MONSTER);
        if (monsterList != null && monsterList.stream().anyMatch(spawners -> spawners.field_242588_c == EntityType.ZOMBIE)) {
            event.getSpawns().func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, BalanceMobProps.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 2));
            event.getSpawns().func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.advanced_vampire, BalanceMobProps.mobProps.ADVANCED_VAMPIRE_SPAWN_PROBE, 1, 1));
        }
        event.getGeneration().func_242513_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, ModFeatures.vampire_dungeon.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_242729_a(VampirismConfig.BALANCE.vampireDungeonWeight.get()));
        if (!VampirismConfig.SERVER.disableHunterTentGen.get() && VampirismAPI.worldGenRegistry().canStructureBeGeneratedInBiome(ModFeatures.hunter_camp.getRegistryName(), event.getName(), event.getCategory())) {
            event.getGeneration().func_242516_a(ModFeatures.hunter_camp.func_236391_a_/*withConfiguration*/(IFeatureConfig.NO_FEATURE_CONFIG));
        }


    }

    private static Biome.Builder getVampireForestBuilder(float depth, float scale, MobSpawnInfo.Builder spawnBuilder, BiomeAmbience.Builder ambienceBuilder) {
        BiomeGenerationSettings.Builder biomeGeneratorSettings = new BiomeGenerationSettings.Builder().func_242517_a(SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState())));
        DefaultBiomeFeatures.func_243738_d(biomeGeneratorSettings); //carver
        VampirismBiomeFeatures.addModdedWaterLake(biomeGeneratorSettings);

        VampirismBiomeFeatures.addVampireFlower(biomeGeneratorSettings);
        DefaultBiomeFeatures.func_243701_O(biomeGeneratorSettings);
        DefaultBiomeFeatures.func_243705_S(biomeGeneratorSettings);

        DefaultBiomeFeatures.func_243748_i(biomeGeneratorSettings); //stone variants
        DefaultBiomeFeatures.func_243750_j(biomeGeneratorSettings); //ore
        DefaultBiomeFeatures.func_243754_n(biomeGeneratorSettings); //disks

        VampirismBiomeFeatures.addVampireTrees(biomeGeneratorSettings);

        VampirismBiomeFeatures.addWaterSprings(biomeGeneratorSettings);
        return new Biome.Builder().precipitation(Biome.RainType.RAIN).category(Biome.Category.FOREST).depth(depth).scale(scale).temperature(0.3F).downfall(0F).func_235097_a_(ambienceBuilder.build()).func_242458_a(spawnBuilder.func_242577_b()).func_242457_a(biomeGeneratorSettings.func_242508_a());
    }
}
