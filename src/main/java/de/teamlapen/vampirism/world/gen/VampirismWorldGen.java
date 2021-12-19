package de.teamlapen.vampirism.world.gen;

import com.google.common.collect.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.mixin.LevelStructureSettingsAccessor;
import de.teamlapen.vampirism.mixin.MultiNoiseBiomeSourcePresetAccessor;
import de.teamlapen.vampirism.util.ConfigurableStructureSeparationSettings;
import de.teamlapen.vampirism.util.MixinHooks;
import de.teamlapen.vampirism.world.biome.VampirismBiomeFeatures;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomBlockState;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VampirismWorldGen {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean debug = false;

    public static void createJigsawPool() {
        VampirismWorldGen.setupSingleJigsawPieceGeneration();

        Pools.register(new StructureTemplatePool(new ResourceLocation(REFERENCE.MODID, "village/entities/hunter_trainer"), new ResourceLocation("empty"), Lists.newArrayList(Pair.of(singleJigsawPieceFunction("village/entities/hunter_trainer"), 1)), StructureTemplatePool.Projection.RIGID));
    }

    public static void addVillageStructures(RegistryAccess dynamicRegistries) {
        VampirismWorldGen.addHunterTrainerHouse(dynamicRegistries, getDefaultPools());
        VampirismWorldGen.addTotem(dynamicRegistries, getDefaultPools());
        VampirismWorldGen.replaceTemples(dynamicRegistries, getTempleReplacements());
    }


    /**
     * @param a The "container"
     * @param b The point to check
     * @return Whether all parameters of point are completely contained inside outer
     */
    private static boolean intersects(Climate.ParameterPoint a, Climate.ParameterPoint b) {
        return intersects(a.temperature(), b.temperature()) && intersects(a.humidity(), b.humidity()) && intersects(a.continentalness(), b.continentalness()) && intersects(a.erosion(), b.erosion()) && intersects(a.depth(), b.depth()) && intersects(a.weirdness(), b.weirdness());
    }

    /**
     * @return Whether point is completely contained inside outer
     */
    private static boolean intersects(Climate.Parameter a, Climate.Parameter b) {
        return (a.max() > b.min() && a.min() < b.max()) || (a.max() == a.min() && b.max() == b.min() && a.max() == b.max());
    }


    /**
     * Call on main thread
     */
    public static void addBiomesToOverworldUnsafe() {
        if(!VampirismConfig.COMMON.addVampireForestToOverworld.get()){
            return;
        }
        /*
         * Hack the vampire forest into the Overworld biome list preset, replacing some taiga biome areas.
         *
         * Create a wrapper function for the parameterSource function, which calls the original one and then modifies the result
         */

        final Function<Registry<Biome>, Climate.ParameterList<Supplier<Biome>>> originalParameterSourceFunction = ((MultiNoiseBiomeSourcePresetAccessor) MultiNoiseBiomeSource.Preset.OVERWORLD).getPresetSupplier_vampirism();


        Function<Registry<Biome>, Climate.ParameterList<Supplier<Biome>>> wrapperParameterSourceFunction = (registry) -> {
            //Create copy of vanilla list
            Climate.ParameterList<Supplier<Biome>> vanillaList = originalParameterSourceFunction.apply(registry);
            List<Pair<Climate.ParameterPoint, Supplier<Biome>>> biomes = new ArrayList<>(vanillaList.values());

            //Setup parameter point (basically the volume in the n-d parameter space) at which the biome should be generated
            //Order of parameters: Temp , humidity, continentalness, erosion, depth, weirdness
            Climate.ParameterPoint[] forestPoints = new Climate.ParameterPoint[]{
                    Climate.parameters(Climate.Parameter.span(-0.40F, -0.19F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(-0.11F, 0.55F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.point(0), Climate.Parameter.span(-0.56666666F, -0.05F), 0),
                    Climate.parameters(Climate.Parameter.span(-0.40F, -0.19F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(-0.11F, 0.55F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.point(1), Climate.Parameter.span(-0.56666666F, -0.05F), 0),
                    Climate.parameters(Climate.Parameter.span(-0.40F, -0.19F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(-0.11F, 0.55F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.point(0), Climate.Parameter.span(0.05f, 0.4F), 0),
                    Climate.parameters(Climate.Parameter.span(-0.40F, -0.19F), Climate.Parameter.span(0.1F, 0.3F), Climate.Parameter.span(-0.11F, 0.55F), Climate.Parameter.span(-0.375F, -0.2225F), Climate.Parameter.point(1), Climate.Parameter.span(0.05f, 0.4F), 0)
            };


            //Remove vanilla biomes that are completely inside the given range
            int oldCount = biomes.size();
            int removed = 0;
            Iterator<Pair<Climate.ParameterPoint, Supplier<Biome>>> it = biomes.iterator();
            while (it.hasNext()) {
                Pair<Climate.ParameterPoint, Supplier<Biome>> pair = it.next();
                //It should be safe to get the biome here because {@link BiomeSource} does so as well right after this function call
                ResourceLocation biomeId = pair.getSecond().get().getRegistryName();
                if (biomeId != null && "minecraft".equals(biomeId.getNamespace()) && Arrays.stream(forestPoints).anyMatch(p -> intersects(p, pair.getFirst()))) {
                    it.remove();
                    removed++;
                    LOGGER.debug("Removing biome {} from parameter point {} in overworld preset", biomeId, pair.getFirst());
                }
            }
            LOGGER.debug("Removed a total of {} points from {}", removed, oldCount);


            LOGGER.info("Adding biome {} to ParameterPoints {} in Preset.OVERWORLD", ModBiomes.VAMPIRE_FOREST_KEY.location(), Arrays.toString(forestPoints));
            for (Climate.ParameterPoint forestPoint : forestPoints) {
                biomes.add(Pair.of(forestPoint, () -> registry.get(ModBiomes.VAMPIRE_FOREST_KEY)));
            }

            return new Climate.ParameterList<>(biomes);
        };


        ((MultiNoiseBiomeSourcePresetAccessor) MultiNoiseBiomeSource.Preset.OVERWORLD).setPresetSupplier_vampirism(wrapperParameterSourceFunction);
    }

    /**
     * Credit to @TelepathicGrunt  StructureTutorialMod licenced under Creative Commons Zero v1.0 Universal
     * https://github.com/TelepathicGrunt/StructureTutorialMod/blob/7f79b80dfee2861a0e8c02db3f63d5477be8b9ef/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java#L83
     */
    public static void addBiomeStructuresTemporary(ServerLevel serverLevel) {
        ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
        // Skip superflat to prevent issues with it. Plus, users don't want structures clogging up their superflat worlds.
        if (chunkGenerator instanceof FlatLevelSource && serverLevel.dimension().equals(Level.OVERWORLD)) {
            return;
        }

        // We will need this a lot lol
        StructureSettings worldStructureSettings = serverLevel.getChunkSource().getGenerator().getSettings();

        //////////// BIOME BASED STRUCTURE SPAWNING ////////////
        /*
         * NOTE: Forge does not have a hook for injecting structures into biomes yet.
         * Instead, we will use the below to add our structure to overworld biomes.
         * Remember, this is temporary until Forge finds a better solution for adding structures to biomes.
         */

        // Grab the map that holds what ConfigureStructures a structure has and what biomes it can spawn in.
        // We will inject our structures into that map/multimap
        Map<StructureFeature<?>, Multimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> tempStructureToMultiMap = new HashMap<>();
        ((LevelStructureSettingsAccessor) worldStructureSettings).getConfiguredStructures().forEach((key, value) -> tempStructureToMultiMap.put(key, HashMultimap.create(value)));
        Registry<Biome> biomeRegistry = serverLevel.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY);

        VampirismBiomeFeatures.addStructuresToBiomes(biomeRegistry.entrySet(), (configuredFeature, biome) -> {
            tempStructureToMultiMap.computeIfAbsent(configuredFeature.feature, (f) -> HashMultimap.create());
            tempStructureToMultiMap.get(configuredFeature.feature).put(configuredFeature, biome);
        });


        // Turn the entire map and the inner multimaps to immutable to match the source code's require type
        ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> immutableOuterMap = ImmutableMap.builder();
        tempStructureToMultiMap.forEach((key, value) -> {
            ImmutableMultimap.Builder<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> immutableInnerMultiMap = ImmutableMultimap.builder();
            immutableInnerMultiMap.putAll(value);
            immutableOuterMap.put(key, immutableInnerMultiMap.build());
        });

        // Set it in the field.
        ((LevelStructureSettingsAccessor) worldStructureSettings).setConfiguredStructures(immutableOuterMap.build());


        //////////// Structure separation ////////////

        /*
         * Skip Terraforged's chunk generator as they are a special case of a mod locking down their chunkgenerator.
         * They will handle your structure spacing for your if you add to BuiltinRegistries.NOISE_GENERATOR_SETTINGS in your structure's registration.
         * This here is done with reflection as this tutorial is not about setting up and using Mixins.
         * If you are using mixins, you can call the codec method with an invoker mixin instead of using reflection.
         */

        try {
            Method GETCODEC_METHOD = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, "codec");
            ResourceLocation cgRL = Registry.CHUNK_GENERATOR.getKey((Codec<? extends ChunkGenerator>) GETCODEC_METHOD.invoke(chunkGenerator));
            if (cgRL != null && cgRL.getNamespace().equals("terraforged")) return;
        } catch (Exception e) {
            LOGGER.error("Was unable to check if " + serverLevel.dimension().location() + " is using Terraforged's ChunkGenerator.");
        }


        Map<StructureFeature<?>, StructureFeatureConfiguration> structureSettingsMap = new HashMap<>(worldStructureSettings.structureConfig());

        //Add our separation for our features
        ModFeatures.addStructureSeparationSettings(serverLevel.dimension(), structureSettingsMap);

        //Modify vanilla village separation
        if (VampirismConfig.COMMON.villageModify.get()) {
            LOGGER.info("Replacing vanilla village structure separation settings for the overworld dimension preset");
            structureSettingsMap.put(StructureFeature.VILLAGE, new ConfigurableStructureSeparationSettings(VampirismConfig.COMMON.villageDistance, VampirismConfig.COMMON.villageSeparation, StructureSettings.DEFAULTS.get(StructureFeature.VILLAGE).salt()));
        } else {
            LOGGER.trace("Not modifying village");
        }
        ((LevelStructureSettingsAccessor) worldStructureSettings).setStructureSeparation_vampirism(structureSettingsMap);

    }

    /**
     * @return a map the maps {@link StructurePoolElement}s that should be modified to the type village type of added objects
     */
    private static Map<ResourceLocation, BiomeType> getDefaultPools() {
        return Map.ofEntries(
                Map.entry(new ResourceLocation("village/plains/houses"), BiomeType.PLAINS),
                Map.entry(new ResourceLocation("village/desert/houses"), BiomeType.DESERT),
                Map.entry(new ResourceLocation("village/savanna/houses"), BiomeType.SAVANNA),
                Map.entry(new ResourceLocation("village/taiga/houses"), BiomeType.TAIGA),
                Map.entry(new ResourceLocation("village/snowy/houses"), BiomeType.SNOWY),
                Map.entry(new ResourceLocation("repurposed_structures", "village/badlands/houses"), BiomeType.BADLANDS),
                Map.entry(new ResourceLocation("repurposed_structures", "village/birch/houses"), BiomeType.BIRCH),
                Map.entry(new ResourceLocation("repurposed_structures", "village/dark_forest/houses"), BiomeType.DARK_FOREST),
                Map.entry(new ResourceLocation("repurposed_structures", "village/jungle/houses"), BiomeType.JUNGLE),
                Map.entry(new ResourceLocation("repurposed_structures", "village/mountains/houses"), BiomeType.MOUNTAINS),
                Map.entry(new ResourceLocation("repurposed_structures", "village/giant_taiga/houses"), BiomeType.GIANT_TAIGA),
                Map.entry(new ResourceLocation("repurposed_structures", "village/oak/houses"), BiomeType.OAK),
                Map.entry(new ResourceLocation("repurposed_structures", "village/swamp/houses"), BiomeType.SWAMP),
                Map.entry(new ResourceLocation("repurposed_structures", "village/crimson/houses"), BiomeType.CRIMSON),
                Map.entry(new ResourceLocation("repurposed_structures", "village/warped/houses"), BiomeType.WARPED));
    }

    /**
     * @return a map that maps {@link StructureTemplatePool}s that should be modified to a map that maps temple {@link StructurePoolElement}s to modified temple {@link StructurePoolElement}s
     */
    private static Map<ResourceLocation, Map<String, StructurePoolElement>> getTempleReplacements() {
        return new HashMap<>() {{
            put(new ResourceLocation("village/plains/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), singleJigsawPiece("village/plains/houses/plains_temple_3", ProcessorLists.MOSSIFY_10_PERCENT),
                    singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), singleJigsawPiece("village/plains/houses/plains_temple_4", ProcessorLists.MOSSIFY_10_PERCENT)));
            put(new ResourceLocation("village/desert/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), singleJigsawPiece("village/desert/houses/desert_temple_1"),
                    singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), singleJigsawPiece("village/desert/houses/desert_temple_2")));
            put(new ResourceLocation("village/savanna/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), singleJigsawPiece("village/savanna/houses/savanna_temple_1"),
                    singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), singleJigsawPiece("village/savanna/houses/savanna_temple_2")));
            put(new ResourceLocation("village/taiga/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/taiga/houses/taiga_temple_1"), singleJigsawPiece("village/taiga/houses/taiga_temple_1", ProcessorLists.MOSSIFY_10_PERCENT)));
            put(new ResourceLocation("village/snowy/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), singleJigsawPiece("village/snowy/houses/snowy_temple_1")));
            put(new ResourceLocation("village/plains/zombie/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), singleJigsawPiece("village/plains/houses/plains_temple_3", ProcessorLists.ZOMBIE_PLAINS),
                    singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), singleJigsawPiece("village/plains/houses/plains_temple_4", ProcessorLists.ZOMBIE_PLAINS)));
            put(new ResourceLocation("village/desert/zombie/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), singleJigsawPiece("village/desert/houses/desert_temple_1", ProcessorLists.ZOMBIE_DESERT),
                    singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), singleJigsawPiece("village/desert/houses/desert_temple_2", ProcessorLists.ZOMBIE_DESERT)));
            put(new ResourceLocation("village/savanna/zombie/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), singleJigsawPiece("village/savanna/houses/savanna_temple_1", ProcessorLists.ZOMBIE_SAVANNA),
                    singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), singleJigsawPiece("village/savanna/houses/savanna_temple_2", ProcessorLists.ZOMBIE_SAVANNA)));
            put(new ResourceLocation("village/taiga/zombie/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/taiga/zombie/houses/taiga_temple_1"), singleJigsawPiece("village/taiga/houses/taiga_temple_1", ProcessorLists.ZOMBIE_TAIGA)));
            put(new ResourceLocation("village/snowy/zombie/houses"), ImmutableMap.of(
                    singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), singleJigsawPiece("village/snowy/houses/snowy_temple_1", ProcessorLists.ZOMBIE_SNOWY)));
        }};
    }

    /**
     * replaces half of the temples with temples with church altar
     */
    private static void replaceTemples(RegistryAccess dynamicRegistries, Map<ResourceLocation, Map<String, StructurePoolElement>> patternReplacements) {
        // return if temples should not be modified
        if (!VampirismConfig.COMMON.villageReplaceTemples.get()) return;
        // get jigsaw registry
        dynamicRegistries.registry(BuiltinRegistries.TEMPLATE_POOL.key()).ifPresent(jigsawRegistry -> {
            // for every desired pools
            patternReplacements.forEach((pool, replacements) ->
                    // get the pool if present
                    jigsawRegistry.getOptional(pool).ifPresent(pattern -> {
                        // for each replacement of the pool
                        replacements.forEach((original, modified) -> {
                            // remove & count the original pieces
                            List<StructurePoolElement> oldPieces = new ArrayList<>();
                            pattern.templates.removeIf(piece -> {
                                if (piece.toString().equals(original)) {
                                    oldPieces.add(piece);
                                    return true;
                                }
                                return false;
                            });
                            // add original and modified places back with less quantity
                            for (int i = 0; i < oldPieces.size() * 0.6; i++) {
                                pattern.templates.add(modified);
                                pattern.templates.add(oldPieces.get(i));
                            }

                            // Add modified temple pieces to the weighted list for better mod compat if other mods read this field instead of templates
                            // (ex: Repurposed Structures)
                            List<Pair<StructurePoolElement, Integer>> weightedElementList = new ArrayList<>(pattern.rawTemplates);
                            Optional<Pair<StructurePoolElement, Integer>> originalPiece = weightedElementList.stream().filter(entry -> entry.getFirst().toString().equals(original)).findAny();
                            originalPiece.ifPresent(originalEntry -> {
                                // remove original
                                weightedElementList.remove(originalPiece.get());

                                // Readd original at reduced weight and new piece as well.
                                weightedElementList.add(new Pair<>(originalEntry.getFirst(), (int) (originalEntry.getSecond() * 0.6)));
                                weightedElementList.add(new Pair<>(modified, (int) (originalEntry.getSecond() * 0.6)));
                            });
                            pattern.rawTemplates = weightedElementList;
                        });
                    }));
        });
    }

    /**
     * adds a hunter trainer house to each village
     */
    private static void addHunterTrainerHouse(RegistryAccess reg, Map<ResourceLocation, BiomeType> pools) {
        // get jigsaw registry
        reg.registry(BuiltinRegistries.TEMPLATE_POOL.key()).ifPresent(patternRegistry -> {
            // for every desired pools
            pools.forEach((pool, type) -> {
                // get the pool if present
                patternRegistry.getOptional(pool).ifPresent(pattern -> {
                    // create trainer house piece with desired village type
                    StructurePoolElement piece = singleJigsawPiece("village/" + type.path + "/houses/hunter_trainer", new StructureProcessorList(Collections.emptyList()));
                    // add hunter trainer house with weight
                    for (int i = 0; i < VampirismConfig.COMMON.villageHunterTrainerWeight.get(); i++) {
                        pattern.templates.add(piece);
                    }

                    // Add hunter trainer house to the weighted list for better mod compat if other mods read this field instead of templates
                    // (ex: Repurposed Structures)
                    List<Pair<StructurePoolElement, Integer>> weightedElementList = new ArrayList<>(pattern.rawTemplates);
                    weightedElementList.add(new Pair<>(piece, VampirismConfig.COMMON.villageHunterTrainerWeight.get()));
                    pattern.rawTemplates = weightedElementList;
                });
            });
        });
    }

    /**
     * adds totem to every village
     */
    private static void addTotem(RegistryAccess reg, Map<ResourceLocation, BiomeType> pools) {
        // create totem piece
        StructureProcessor factionProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockState(new RandomBlockMatchTest(ModBlocks.totem_top, VampirismConfig.COMMON.villageTotemFactionChance.get().floatValue()), AlwaysTrueTest.INSTANCE, ModBlocks.totem_top.defaultBlockState(), TotemTopBlock.getBlocks().stream().filter(totem -> totem != ModBlocks.totem_top && !totem.isCrafted()).map(Block::defaultBlockState).collect(Collectors.toList()))));
        StructureProcessor biomeTopBlockProcessor = new BiomeTopBlockProcessor(Blocks.DIRT.defaultBlockState());
        StructurePoolElement totem = singleJigsawPiece("village/totem", new StructureProcessorList(Lists.newArrayList(factionProcessor, biomeTopBlockProcessor)));

        // get jigsaw registry
        reg.registry(BuiltinRegistries.TEMPLATE_POOL.key()).ifPresent(patternRegistry -> {
            // for every desired pools
            pools.forEach((pool, type) -> {
                // get the pool if present
                patternRegistry.getOptional(pool).ifPresent(pattern -> {
                    // add totem with weight
                    for (int i = 0; i < VampirismConfig.COMMON.villageTotemWeight.get(); ++i) {
                        pattern.templates.add(totem);
                    }

                    // Add totem house to the weighted list for better mod compat if other mods read this field instead of templates
                    // (ex: Repurposed Structures)
                    List<Pair<StructurePoolElement, Integer>> weightedElementList = new ArrayList<>(pattern.rawTemplates);
                    weightedElementList.add(new Pair<>(totem, VampirismConfig.COMMON.villageTotemWeight.get()));
                    pattern.rawTemplates = weightedElementList;
                });
            });
        });
    }

    /**
     * ensure single generation of following structures
     */
    private static void setupSingleJigsawPieceGeneration() {
        List<ResourceLocation> list = Lists.newArrayList(new ResourceLocation(REFERENCE.MODID, "village/totem"));
        list.addAll(Arrays.stream(BiomeType.values()).map(type -> new ResourceLocation(REFERENCE.MODID, "village/" + type.path + "/houses/hunter_trainer")).toList());
        MixinHooks.addSingleInstanceStructure(list);
    }

    private static SinglePoolElement singleJigsawPiece(@Nonnull String path) {
        return singleJigsawPiece(path, new StructureProcessorList(Collections.emptyList()));
    }

    private static SinglePoolElement singleJigsawPiece(@Nonnull String path, @Nonnull StructureProcessorList processors) {
        return SinglePoolElement.single(REFERENCE.MODID + ":" + path, processors).apply(StructureTemplatePool.Projection.RIGID);
    }

    private static Function<StructureTemplatePool.Projection, SinglePoolElement> singleJigsawPieceFunction(@SuppressWarnings("SameParameterValue") @Nonnull String path) {
        return singleJigsawPieceFunction(path, new StructureProcessorList(Collections.emptyList()));
    }

    private static Function<StructureTemplatePool.Projection, SinglePoolElement> singleJigsawPieceFunction(@Nonnull String path, @Nonnull StructureProcessorList processors) {
        return SinglePoolElement.single(REFERENCE.MODID + ":" + path, processors);
    }

    private static String singleJigsawString(String resourceLocation) {
        return "Single[Left[" + resourceLocation + "]]";
    }

    private static String singleLegacyJigsawString(String resourceLocation) {
        return "LegacySingle[Left[" + resourceLocation + "]]";
    }

    private enum BiomeType {
        PLAINS("plains"),
        TAIGA("taiga"),
        DESERT("desert"),
        SNOWY("snowy"),
        SAVANNA("savanna"),
        BADLANDS("badlands"),
        BIRCH("birch"),
        DARK_FOREST("dark_forest"),
        CRIMSON("crimson"),
        GIANT_TAIGA("giant_taiga"),
        JUNGLE("jungle"),
        MOUNTAINS("mountains"),
        OAK("oak"),
        SWAMP("swamp"),
        WARPED("warped");

        public final String path;

        BiomeType(String path) {
            this.path = path;
        }
    }

}
