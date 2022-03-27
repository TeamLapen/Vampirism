package de.teamlapen.vampirism.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.util.MixinHooks;
import de.teamlapen.vampirism.world.gen.util.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.util.RandomBlockState;
import de.teamlapen.vampirism.world.gen.util.RandomStructureProcessor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VampirismWorldGen {

    private static Holder<StructureProcessorList> TOTEM_FACTION_PROCESSOR;

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
        return Map.ofEntries(Map.entry(new ResourceLocation("village/plains/houses"), VampirismWorldGen.BiomeType.PLAINS), Map.entry(new ResourceLocation("village/desert/houses"), VampirismWorldGen.BiomeType.DESERT), Map.entry(new ResourceLocation("village/savanna/houses"), VampirismWorldGen.BiomeType.SAVANNA), Map.entry(new ResourceLocation("village/taiga/houses"), VampirismWorldGen.BiomeType.TAIGA), Map.entry(new ResourceLocation("village/snowy/houses"), VampirismWorldGen.BiomeType.SNOWY), Map.entry(new ResourceLocation("repurposed_structures", "village/badlands/houses"), VampirismWorldGen.BiomeType.BADLANDS), Map.entry(new ResourceLocation("repurposed_structures", "village/birch/houses"), VampirismWorldGen.BiomeType.BIRCH), Map.entry(new ResourceLocation("repurposed_structures", "village/dark_forest/houses"), VampirismWorldGen.BiomeType.DARK_FOREST), Map.entry(new ResourceLocation("repurposed_structures", "village/jungle/houses"), VampirismWorldGen.BiomeType.JUNGLE), Map.entry(new ResourceLocation("repurposed_structures", "village/mountains/houses"), VampirismWorldGen.BiomeType.MOUNTAINS), Map.entry(new ResourceLocation("repurposed_structures", "village/giant_taiga/houses"), VampirismWorldGen.BiomeType.GIANT_TAIGA), Map.entry(new ResourceLocation("repurposed_structures", "village/oak/houses"), VampirismWorldGen.BiomeType.OAK), Map.entry(new ResourceLocation("repurposed_structures", "village/swamp/houses"), VampirismWorldGen.BiomeType.SWAMP), Map.entry(new ResourceLocation("repurposed_structures", "village/crimson/houses"), VampirismWorldGen.BiomeType.CRIMSON), Map.entry(new ResourceLocation("repurposed_structures", "village/warped/houses"), VampirismWorldGen.BiomeType.WARPED));
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
