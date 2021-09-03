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
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.structures.SinglePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class VampirismWorldGen {
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
     * @return a map the maps {@link StructurePoolElement}s that should be modified to the type village type of added objects
     */
    private static Map<ResourceLocation, BiomeType> getDefaultPools() {
        return Map.of(
                new ResourceLocation("village/plains/houses"), BiomeType.PLAINS,
                new ResourceLocation("village/desert/houses"), BiomeType.DESERT,
                new ResourceLocation("village/savanna/houses"), BiomeType.SAVANNA,
                new ResourceLocation("village/taiga/houses"), BiomeType.TAIGA,
                new ResourceLocation("village/snowy/houses"), BiomeType.SNOWY);
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
                    StructurePoolElement piece = singleJigsawPiece(type.path + "/houses/hunter_trainer", new StructureProcessorList(Collections.emptyList()));
                    // add hunter trainer house with weight
                    for (int i = 0; i < VampirismConfig.COMMON.villageHunterTrainerWeight.get(); i++) {
                        pattern.templates.add(piece);
                    }
                });
            });
        });
    }

    /**
     * adds totem to every village
     */
    private static void addTotem(RegistryAccess reg, Map<ResourceLocation, BiomeType> pools) {
        // create totem piece
        StructureProcessor totemProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockState(new RandomBlockMatchTest(ModBlocks.totem_top, VampirismConfig.COMMON.villageTotemFactionChance.get().floatValue()), AlwaysTrueTest.INSTANCE, ModBlocks.totem_top.defaultBlockState(), TotemTopBlock.getBlocks().stream().filter(totem -> totem != ModBlocks.totem_top && !totem.isCrafted()).map(Block::defaultBlockState).collect(Collectors.toList()))));
        StructureProcessor totemTopBlock = new BiomeTopBlockProcessor(Blocks.BRICK_WALL.defaultBlockState());
        StructurePoolElement totem = singleJigsawPiece("village/totem", new StructureProcessorList(Lists.newArrayList(totemProcessor, totemTopBlock)));

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
                });
            });
        });
    }

    /**
     * ensure single generation of following structures
     */
    private static void setupSingleJigsawPieceGeneration() {
        MixinHooks.addSingleInstanceStructure(Lists.newArrayList(
                singleJigsawString("vampirism:village/totem"),
                singleJigsawString("vampirism:village/desert/houses/hunter_trainer"),
                singleJigsawString("vampirism:village/plains/houses/hunter_trainer"),
                singleJigsawString("vampirism:village/snowy/houses/hunter_trainer"),
                singleJigsawString("vampirism:village/savanna/houses/hunter_trainer"),
                singleJigsawString("vampirism:village/taiga/houses/hunter_trainer")));
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
        PLAINS("village/plains"), TAIGA("village/taiga"), DESERT("village/desert"), SNOWY("village/snowy"), SAVANNA("village/savanna");

        public final String path;

        BiomeType(String path) {
            this.path = path;
        }
    }

}
