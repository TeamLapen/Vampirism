package de.teamlapen.vampirism.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.mixin.MultiNoiseBiomeSourcePresetAccessor;
import de.teamlapen.vampirism.modcompat.terrablender.TerraBlenderCompat;
import de.teamlapen.vampirism.util.MixinHooks;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomBlockState;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VampirismWorldGen {
    private static final Logger LOGGER = LogManager.getLogger();

    private static Holder<StructureProcessorList> TOTEM_FACTION_PROCESSOR;

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
     * Call on main thread.
     *
     * Add our biomes to the overworld biome source preset
     */
    public static void addBiomesToOverworldUnsafe() {
        if(TerraBlenderCompat.areBiomesAddedViaTerraBlender()){ //If we are already adding the biome to the overworld using TerraBlender, we shouldn't hack it into the overworld preset
            LOGGER.info("Vampirism Biomes are added via TerraBlender. Not adding them to overworld preset.");
            return;
        }
        if(!VampirismConfig.COMMON.addVampireForestToOverworld.get()){
            return;
        }
        /*
         * Hack the vampire forest into the Overworld biome list preset, replacing some taiga biome areas.
         *
         * Create a wrapper function for the parameterSource function, which calls the original one and then modifies the result
         */

        final Function<Registry<Biome>, Climate.ParameterList<Holder<Biome>>> originalParameterSourceFunction = ((MultiNoiseBiomeSourcePresetAccessor) MultiNoiseBiomeSource.Preset.OVERWORLD).getPresetSupplier_vampirism();


        Function<Registry<Biome>, Climate.ParameterList<Holder<Biome>>> wrapperParameterSourceFunction = (registry) -> {
            //Create copy of vanilla list
            Climate.ParameterList<Holder<Biome>> vanillaList = originalParameterSourceFunction.apply(registry);
            List<Pair<Climate.ParameterPoint, Holder<Biome>>> biomes = new ArrayList<>(vanillaList.values());

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
            Iterator<Pair<Climate.ParameterPoint, Holder<Biome>>> it = biomes.iterator();
            while (it.hasNext()) {
                Pair<Climate.ParameterPoint, Holder<Biome>> pair = it.next();
                //It should be safe to get the biome here because {@link BiomeSource} does so as well right after this function call
                removed += pair.getSecond().unwrapKey().map(biomeId -> {
                    if ("minecraft".equals(biomeId.location().getNamespace()) && Arrays.stream(forestPoints).anyMatch(p -> intersects(p, pair.getFirst()))) {
                        it.remove();
                        LOGGER.debug("Removing biome {} from parameter point {} in overworld preset", biomeId, pair.getFirst());
                        return 1;
                    }
                    return 0;
                }).orElse(0);

            }
            LOGGER.debug("Removed a total of {} points from {}", removed, oldCount);


            LOGGER.info("Adding biome {} to ParameterPoints {} in Preset.OVERWORLD", ModBiomes.VAMPIRE_FOREST.location(), Arrays.toString(forestPoints));
            for (Climate.ParameterPoint forestPoint : forestPoints) {
                biomes.add(Pair.of(forestPoint, registry.getHolderOrThrow(ModBiomes.VAMPIRE_FOREST)));
            }

            return new Climate.ParameterList<>(biomes);
        };


        ((MultiNoiseBiomeSourcePresetAccessor) MultiNoiseBiomeSource.Preset.OVERWORLD).setPresetSupplier_vampirism(wrapperParameterSourceFunction);
    }

    public static void createJigsawPool() {
        setupSingleJigsawPieceGeneration();
        Pools.register(new StructureTemplatePool(new ResourceLocation("vampirism", "village/entities/hunter_trainer"), new ResourceLocation("empty"), Lists.newArrayList(Pair.of(singleJigsawPieceFunction("village/entities/hunter_trainer"), 1)), StructureTemplatePool.Projection.RIGID));
    }

    public static void addVillageStructures(RegistryAccess dynamicRegistries) {
        addHunterTrainerHouse(dynamicRegistries, getDefaultPools());
        addTotem(dynamicRegistries, getDefaultPools());
        replaceTemples(dynamicRegistries, getTempleReplacements());
    }

    /**
     * @return a map the maps {@link StructurePoolElement}s that should be modified to the type village type of added objects
     */
    private static Map<ResourceLocation, BiomeType> getDefaultPools() {
        return Map.ofEntries(Map.entry(new ResourceLocation("village/plains/houses"), VampirismWorldGen.BiomeType.PLAINS), Map.entry(new ResourceLocation("village/desert/houses"), VampirismWorldGen.BiomeType.DESERT), Map.entry(new ResourceLocation("village/savanna/houses"), VampirismWorldGen.BiomeType.SAVANNA), Map.entry(new ResourceLocation("village/taiga/houses"), VampirismWorldGen.BiomeType.TAIGA), Map.entry(new ResourceLocation("village/snowy/houses"), VampirismWorldGen.BiomeType.SNOWY));
    }

    /**
     * @return a map that maps {@link StructureTemplatePool}s that should be modified to a map that maps temple {@link StructurePoolElement}s to modified temple {@link StructurePoolElement}s
     */
    private static Map<ResourceLocation, Map<String, StructurePoolElement>> getTempleReplacements() {
        return new HashMap<>() {
            {
                this.put(new ResourceLocation("village/plains/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), VampirismWorldGen.singleJigsawPiece("village/plains/houses/plains_temple_3", ProcessorLists.MOSSIFY_10_PERCENT), VampirismWorldGen.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), VampirismWorldGen.singleJigsawPiece("village/plains/houses/plains_temple_4", ProcessorLists.MOSSIFY_10_PERCENT)));
                this.put(new ResourceLocation("village/desert/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), VampirismWorldGen.singleJigsawPiece("village/desert/houses/desert_temple_1"), VampirismWorldGen.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), VampirismWorldGen.singleJigsawPiece("village/desert/houses/desert_temple_2")));
                this.put(new ResourceLocation("village/savanna/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), VampirismWorldGen.singleJigsawPiece("village/savanna/houses/savanna_temple_1"), VampirismWorldGen.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), VampirismWorldGen.singleJigsawPiece("village/savanna/houses/savanna_temple_2")));
                this.put(new ResourceLocation("village/taiga/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/taiga/houses/taiga_temple_1"), VampirismWorldGen.singleJigsawPiece("village/taiga/houses/taiga_temple_1", ProcessorLists.MOSSIFY_10_PERCENT)));
                this.put(new ResourceLocation("village/snowy/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), VampirismWorldGen.singleJigsawPiece("village/snowy/houses/snowy_temple_1")));
                this.put(new ResourceLocation("village/plains/zombie/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), VampirismWorldGen.singleJigsawPiece("village/plains/houses/plains_temple_3", ProcessorLists.ZOMBIE_PLAINS), VampirismWorldGen.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), VampirismWorldGen.singleJigsawPiece("village/plains/houses/plains_temple_4", ProcessorLists.ZOMBIE_PLAINS)));
                this.put(new ResourceLocation("village/desert/zombie/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), VampirismWorldGen.singleJigsawPiece("village/desert/houses/desert_temple_1", ProcessorLists.ZOMBIE_DESERT), VampirismWorldGen.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), VampirismWorldGen.singleJigsawPiece("village/desert/houses/desert_temple_2", ProcessorLists.ZOMBIE_DESERT)));
                this.put(new ResourceLocation("village/savanna/zombie/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), VampirismWorldGen.singleJigsawPiece("village/savanna/houses/savanna_temple_1", ProcessorLists.ZOMBIE_SAVANNA), VampirismWorldGen.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), VampirismWorldGen.singleJigsawPiece("village/savanna/houses/savanna_temple_2", ProcessorLists.ZOMBIE_SAVANNA)));
                this.put(new ResourceLocation("village/taiga/zombie/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/taiga/zombie/houses/taiga_temple_1"), VampirismWorldGen.singleJigsawPiece("village/taiga/houses/taiga_temple_1", ProcessorLists.ZOMBIE_TAIGA)));
                this.put(new ResourceLocation("village/snowy/zombie/houses"), ImmutableMap.of(VampirismWorldGen.singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), VampirismWorldGen.singleJigsawPiece("village/snowy/houses/snowy_temple_1", ProcessorLists.ZOMBIE_SNOWY)));
            }
        };
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
                            int targetCountHalf = oldPieces.size();
                            if(targetCountHalf>1){
                                targetCountHalf = (int) (((double)targetCountHalf)*0.6d);
                            }
                            for (int i = 0; i < targetCountHalf; i++) {
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
                                int targetCountHalf2 = oldPieces.size();
                                if(targetCountHalf2>1){
                                    targetCountHalf2 = (int) (((double)targetCountHalf2)*0.6d);
                                }
                                weightedElementList.add(new Pair<>(originalEntry.getFirst(), targetCountHalf2));
                                weightedElementList.add(new Pair<>(modified, targetCountHalf2));
                            });
                            pattern.rawTemplates = weightedElementList;
                        });
                    }));
        });
    }

    /**
     * adds a hunter trainer house to each village
     */
    private static void addHunterTrainerHouse(RegistryAccess reg, Map<ResourceLocation, VampirismWorldGen.BiomeType> pools) {
        // get jigsaw registry
        reg.registry(BuiltinRegistries.TEMPLATE_POOL.key()).ifPresent(patternRegistry -> {
            // for every desired pools
            pools.forEach((pool, type) -> {
                // get the pool if present
                patternRegistry.getOptional(pool).ifPresent(pattern -> {
                    // create trainer house piece with desired village type
                    StructurePoolElement piece = singleJigsawPiece("village/" + type.path + "/houses/hunter_trainer", ProcessorLists.EMPTY);
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

    private static Holder<StructureProcessorList> registerStructureProcessor(String pId, ImmutableList<StructureProcessor> pProcessors) {
        ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, pId);
        StructureProcessorList structureprocessorlist = new StructureProcessorList(pProcessors);
        return BuiltinRegistries.register(BuiltinRegistries.PROCESSOR_LIST, resourcelocation, structureprocessorlist);
    }

    private static void addTotem(RegistryAccess reg, Map<ResourceLocation, VampirismWorldGen.BiomeType> pools) {
        StructureProcessor factionProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockState(new RandomBlockMatchTest(ModBlocks.totem_top, (VampirismConfig.COMMON.villageTotemFactionChance.get()).floatValue()), AlwaysTrueTest.INSTANCE, ModBlocks.totem_top.defaultBlockState(), TotemTopBlock.getBlocks().stream().filter((totemx) -> totemx != ModBlocks.totem_top && !totemx.isCrafted()).map(Block::defaultBlockState).collect(Collectors.toList()))));
        StructureProcessor biomeTopBlockProcessor = new BiomeTopBlockProcessor(Blocks.DIRT.defaultBlockState());
        TOTEM_FACTION_PROCESSOR = registerStructureProcessor("totem_faction", ImmutableList.of(factionProcessor, biomeTopBlockProcessor));


        StructurePoolElement totem = singleJigsawPiece("village/totem", TOTEM_FACTION_PROCESSOR);

        reg.registry(BuiltinRegistries.TEMPLATE_POOL.key()).ifPresent((patternRegistry) -> {
            pools.forEach((pool, type) -> {
                // get the pool if present
                patternRegistry.getOptional(pool).ifPresent((pattern) -> {
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
        List<ResourceLocation> list = Lists.newArrayList(new ResourceLocation("vampirism", "village/totem"));
        list.addAll(Arrays.stream(VampirismWorldGen.BiomeType.values()).map((type) -> new ResourceLocation("vampirism", "village/" + type.path + "/houses/hunter_trainer")).toList());
        MixinHooks.addSingleInstanceStructure(list);
    }

    private static SinglePoolElement singleJigsawPiece(@Nonnull String path) {
        return singleJigsawPiece(path, ProcessorLists.EMPTY);
    }

    private static SinglePoolElement singleJigsawPiece(@Nonnull String path, @Nonnull Holder<StructureProcessorList> processors) {
        return SinglePoolElement.single("vampirism:" + path, processors).apply(StructureTemplatePool.Projection.RIGID);
    }

    private static Function<StructureTemplatePool.Projection, SinglePoolElement> singleJigsawPieceFunction(@Nonnull String path) {
        return singleJigsawPieceFunction(path, ProcessorLists.EMPTY);
    }

    private static Function<StructureTemplatePool.Projection, SinglePoolElement> singleJigsawPieceFunction(@Nonnull String path, @Nonnull Holder<StructureProcessorList> processors) {
        return SinglePoolElement.single("vampirism:" + path, processors);
    }

    private static String singleJigsawString(String resourceLocation) {
        return "Single[Left[" + resourceLocation + "]]";
    }

    private static String singleLegacyJigsawString(String resourceLocation) {
        return "LegacySingle[Left[" + resourceLocation + "]]";
    }

    private enum BiomeType {
        PLAINS("plains"), TAIGA("taiga"), DESERT("desert"), SNOWY("snowy"), SAVANNA("savanna"), BADLANDS("badlands"), BIRCH("birch"), DARK_FOREST("dark_forest"), CRIMSON("crimson"), GIANT_TAIGA("giant_taiga"), JUNGLE("jungle"), MOUNTAINS("mountains"), OAK("oak"), SWAMP("swamp"), WARPED("warped");

        public final String path;

        BiomeType(String path) {
            this.path = path;
        }
    }
}
