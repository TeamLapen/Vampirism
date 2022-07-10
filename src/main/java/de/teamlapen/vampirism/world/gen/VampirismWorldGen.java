package de.teamlapen.vampirism.world.gen;

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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.template.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VampirismWorldGen {
    public static boolean debug = false;

    public static void createJigsawPool() {
        VampirismWorldGen.setupSingleJigsawPieceGeneration();

        JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation(REFERENCE.MODID, "village/entities/hunter_trainer"), new ResourceLocation("empty"), Lists.newArrayList(Pair.of(singleJigsawPieceFunction("village/entities/hunter_trainer"), 1)), JigsawPattern.PlacementBehaviour.RIGID));
    }

    public static void addVillageStructures(DynamicRegistries dynamicRegistries) {
        VampirismWorldGen.addHunterTrainerHouse(dynamicRegistries, getDefaultPools());
        VampirismWorldGen.addTotem(dynamicRegistries, getDefaultPools());
        VampirismWorldGen.replaceTemples(dynamicRegistries, getTempleReplacements());
    }

    /**
     * @return a map the maps {@link JigsawPattern}s that should be modified to the type village type of added objects
     */
    private static Map<ResourceLocation, BiomeType> getDefaultPools() {
        return Collections.unmodifiableMap(new HashMap<ResourceLocation, BiomeType>() {{
            put(new ResourceLocation("village/plains/houses"), BiomeType.PLAINS);
            put(new ResourceLocation("village/desert/houses"), BiomeType.DESERT);
            put(new ResourceLocation("village/savanna/houses"), BiomeType.SAVANNA);
            put(new ResourceLocation("village/taiga/houses"), BiomeType.TAIGA);
            put(new ResourceLocation("village/snowy/houses"), BiomeType.SNOWY);
            //TODO add new hunter trainer variants
            put(new ResourceLocation("repurposed_structures", "village/badlands/houses"), BiomeType.BADLANDS);
            put(new ResourceLocation("repurposed_structures", "village/birch/houses"), BiomeType.BIRCH);
            put(new ResourceLocation("repurposed_structures", "village/dark_forest/houses"), BiomeType.DARK_FOREST);
            put(new ResourceLocation("repurposed_structures", "village/jungle/houses"), BiomeType.JUNGLE);
            put(new ResourceLocation("repurposed_structures", "village/mountains/houses"), BiomeType.MOUNTAINS);
            put(new ResourceLocation("repurposed_structures", "village/giant_taiga/houses"), BiomeType.GIANT_TAIGA);
            put(new ResourceLocation("repurposed_structures", "village/oak/houses"), BiomeType.OAK);
            put(new ResourceLocation("repurposed_structures", "village/swamp/houses"), BiomeType.SWAMP);
            put(new ResourceLocation("repurposed_structures", "village/crimson/houses"), BiomeType.CRIMSON);
            put(new ResourceLocation("repurposed_structures", "village/warped/houses"), BiomeType.WARPED);
        }});
    }

    /**
     * @return a map that maps {@link JigsawPattern}s that should be modified to a map that maps temple {@link JigsawPiece}s to modified temple {@link JigsawPiece}s
     */
    private static Map<ResourceLocation, Map<String, JigsawPiece>> getTempleReplacements() {
        return new HashMap<ResourceLocation, Map<String, JigsawPiece>>() {{
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
    private static void replaceTemples(DynamicRegistries dynamicRegistries, Map<ResourceLocation, Map<String, JigsawPiece>> patternReplacements) {
        // return if temples should not be modified
        if (!VampirismConfig.COMMON.villageReplaceTemples.get()) return;
        // get jigsaw registry
        dynamicRegistries.registry(Registry.TEMPLATE_POOL_REGISTRY).ifPresent(jigsawRegistry -> {
            // for every desired pools
            patternReplacements.forEach((pool, replacements) ->
                    // get the pool if present
                    jigsawRegistry.getOptional(pool).ifPresent(pattern -> {
                        // for each replacement of the pool
                        replacements.forEach((original, modified) -> {
                            // remove & count the original pieces
                            List<JigsawPiece> oldPieces = new ArrayList<>();
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
                            List<Pair<JigsawPiece, Integer>> weightedElementList = new ArrayList<>(pattern.rawTemplates);
                            Optional<Pair<JigsawPiece, Integer>> originalPiece = weightedElementList.stream().filter(entry -> entry.getFirst().toString().equals(original)).findAny();
                            originalPiece.ifPresent(originalEntry -> {
                                // remove original
                                weightedElementList.remove(originalPiece.get());

                                // Readd original at reduced weight and new piece as well.
                                weightedElementList.add(new Pair<>(originalEntry.getFirst(), (int)(originalEntry.getSecond() * 0.6)));
                                weightedElementList.add(new Pair<>(modified, (int)(originalEntry.getSecond() * 0.6)));
                            });
                            pattern.rawTemplates = weightedElementList;
                        });
                    }));
        });
    }

    /**
     * adds a hunter trainer house to each village
     */
    private static void addHunterTrainerHouse(DynamicRegistries reg, Map<ResourceLocation, BiomeType> pools) {
        // get jigsaw registry
        reg.registry(Registry.TEMPLATE_POOL_REGISTRY).ifPresent(patternRegistry -> {
            // for every desired pools
            pools.forEach((pool, type) -> {
                // get the pool if present
                patternRegistry.getOptional(pool).ifPresent(pattern -> {
                    // create trainer house piece with desired village type
                    JigsawPiece piece = singleJigsawPiece("village/" + type.path + "/houses/hunter_trainer", new StructureProcessorList(Collections.emptyList()));
                    // add hunter trainer house with weight
                    for (int i = 0; i < VampirismConfig.COMMON.villageHunterTrainerWeight.get(); i++) {
                        pattern.templates.add(piece);
                    }

                    // Add hunter trainer house to the weighted list for better mod compat if other mods read this field instead of templates
                    // (ex: Repurposed Structures)
                    List<Pair<JigsawPiece, Integer>> weightedElementList = new ArrayList<>(pattern.rawTemplates);
                    weightedElementList.add(new Pair<>(piece, VampirismConfig.COMMON.villageHunterTrainerWeight.get()));
                    pattern.rawTemplates = weightedElementList;
                });
            });
        });
    }

    /**
     * adds totem to every village
     */
    private static void addTotem(DynamicRegistries reg, Map<ResourceLocation, BiomeType> pools) {
        // create totem piece
        StructureProcessor factionProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockState(new RandomBlockMatchRuleTest(ModBlocks.TOTEM_TOP.get(), VampirismConfig.COMMON.villageTotemFactionChance.get().floatValue()), AlwaysTrueRuleTest.INSTANCE, ModBlocks.TOTEM_TOP.get().defaultBlockState(), TotemTopBlock.getBlocks().stream().filter(totem -> totem != ModBlocks.TOTEM_TOP.get() && !totem.isCrafted()).map(Block::defaultBlockState).collect(Collectors.toList()))));
        StructureProcessor biomeTopBlockProcessor = new BiomeTopBlockProcessor(Blocks.DIRT.defaultBlockState());
        JigsawPiece totem = singleJigsawPiece("village/totem", new StructureProcessorList(Lists.newArrayList(factionProcessor, biomeTopBlockProcessor)));

        // get jigsaw registry
        reg.registry(Registry.TEMPLATE_POOL_REGISTRY).ifPresent(patternRegistry -> {
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
                    List<Pair<JigsawPiece, Integer>> weightedElementList = new ArrayList<>(pattern.rawTemplates);
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
        list.addAll(Arrays.stream(BiomeType.values()).map(type -> new ResourceLocation(REFERENCE.MODID, "village/" + type.path + "/houses/hunter_trainer")).collect(Collectors.toList()));
        MixinHooks.addSingleInstanceStructure(list);
    }

    private static SingleJigsawPiece singleJigsawPiece(@Nonnull String path) {
        return singleJigsawPiece(path, new StructureProcessorList(Collections.emptyList()));
    }

    private static SingleJigsawPiece singleJigsawPiece(@Nonnull String path, @Nonnull StructureProcessorList processors) {
        return SingleJigsawPiece.single(REFERENCE.MODID + ":" + path, processors).apply(JigsawPattern.PlacementBehaviour.RIGID);
    }

    private static Function<JigsawPattern.PlacementBehaviour, SingleJigsawPiece> singleJigsawPieceFunction(@SuppressWarnings("SameParameterValue") @Nonnull String path) {
        return singleJigsawPieceFunction(path, new StructureProcessorList(Collections.emptyList()));
    }

    private static Function<JigsawPattern.PlacementBehaviour, SingleJigsawPiece> singleJigsawPieceFunction(@Nonnull String path, @Nonnull StructureProcessorList processors) {
        return SingleJigsawPiece.single(REFERENCE.MODID + ":" + path, processors);
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
