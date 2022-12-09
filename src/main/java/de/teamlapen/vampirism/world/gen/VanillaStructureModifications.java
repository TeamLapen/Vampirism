package de.teamlapen.vampirism.world.gen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.mixin.ProcessorListsAccessor;
import de.teamlapen.vampirism.mixin.VanillaRegistriesAccessor;
import de.teamlapen.vampirism.util.MixinHooks;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomBlockStateRule;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomStructureProcessor;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
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
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Vanilla structures are modified in the following ways:
 * 1) Village structures are modified to include totem and hunter trainer house. Furthermore, some temples are replaced with custom versions. This is done during common setup.
 * 2) Explicitely specified structture pieces are limit to once per structure  via {@link de.teamlapen.vampirism.mixin.MixinJigsawPlacer}.
 */
public class VanillaStructureModifications {

    public static void setup() {
        createJigsawPool();
        createStructureProcessorLists();
    }

    private static void createStructureProcessorLists() {
        VanillaRegistriesAccessor.getBuilder().add(Registries.PROCESSOR_LIST, VanillaStructureModifications::structureProcessorListProvider);
    }

    private static void createJigsawPool() {
        setupSingleJigsawPieceGeneration();
        VanillaRegistriesAccessor.getBuilder().add(Registries.TEMPLATE_POOL, VanillaStructureModifications::structureTemplatePoolProvider);
    }

    private static <T> void structureProcessorListProvider(BootstapContext<StructureProcessorList> bootstapContext) {
        StructureProcessor factionProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockStateRule(new RandomBlockMatchTest(ModBlocks.TOTEM_TOP.get(), (VampirismConfig.COMMON.villageTotemFactionChance.get()).floatValue()), AlwaysTrueTest.INSTANCE, ModBlocks.TOTEM_TOP.get().defaultBlockState(), TotemTopBlock.getBlocks().stream().filter((totemx) -> totemx != ModBlocks.TOTEM_TOP.get() && !totemx.isCrafted()).map(Block::defaultBlockState).collect(Collectors.toList()))));
        StructureProcessor biomeTopBlockProcessor = new BiomeTopBlockProcessor(Blocks.DIRT.defaultBlockState());
        bootstapContext.register(ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation(REFERENCE.MODID, "totem_faction")), new StructureProcessorList(ImmutableList.of(factionProcessor, biomeTopBlockProcessor)));
    }

    private static void structureTemplatePoolProvider(BootstapContext<StructureTemplatePool> bootstapContext) {
        HolderGetter<StructureTemplatePool> holderGetter = bootstapContext.lookup(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> empty = holderGetter.getOrThrow(Pools.EMPTY);
        Pools.register(bootstapContext, "vampirism:village/entities/hunter_trainer", new StructureTemplatePool(empty ,Lists.newArrayList(Pair.of(singleJigsawPieceFunction("village/entities/hunter_trainer"), 1)), StructureTemplatePool.Projection.RIGID));
    }


    public static void addVillageStructures(@NotNull RegistryAccess dynamicRegistries) {
        addHunterTrainerHouse(dynamicRegistries, getDefaultPools());
        addTotem(dynamicRegistries, getDefaultPools());
        replaceTemples(dynamicRegistries, getTempleReplacements());
    }

    /**
     * ensure single generation of following structures
     */
    private static void setupSingleJigsawPieceGeneration() {
        List<ResourceLocation> list = Lists.newArrayList(new ResourceLocation("vampirism", "village/totem"));
        list.addAll(Arrays.stream(VanillaStructureModifications.BiomeType.values()).map((type) -> new ResourceLocation("vampirism", "village/" + type.path + "/houses/hunter_trainer")).toList());
        MixinHooks.addSingleInstanceStructure(list);
    }

    /**
     * @return a map the maps {@link StructurePoolElement}s that should be modified to the type village type of added objects
     */
    private static @NotNull Map<ResourceLocation, BiomeType> getDefaultPools() {
        return Map.ofEntries(Map.entry(new ResourceLocation("village/plains/houses"), VanillaStructureModifications.BiomeType.PLAINS), Map.entry(new ResourceLocation("village/desert/houses"), VanillaStructureModifications.BiomeType.DESERT), Map.entry(new ResourceLocation("village/savanna/houses"), VanillaStructureModifications.BiomeType.SAVANNA), Map.entry(new ResourceLocation("village/taiga/houses"), VanillaStructureModifications.BiomeType.TAIGA), Map.entry(new ResourceLocation("village/snowy/houses"), VanillaStructureModifications.BiomeType.SNOWY));
    }

    /**
     * @return a map that maps {@link StructureTemplatePool}s that should be modified to a map that maps temple {@link StructurePoolElement}s to modified temple {@link StructurePoolElement}s
     */
    private static @NotNull Map<ResourceLocation, Map<String, StructurePoolElement>> getTempleReplacements() {
        return new HashMap<>() {
            {
                this.put(new ResourceLocation("village/plains/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), VanillaStructureModifications.singleJigsawPiece("village/plains/houses/plains_temple_3", ProcessorLists.MOSSIFY_10_PERCENT), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), VanillaStructureModifications.singleJigsawPiece("village/plains/houses/plains_temple_4", ProcessorLists.MOSSIFY_10_PERCENT)));
                this.put(new ResourceLocation("village/desert/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), VanillaStructureModifications.singleJigsawPiece("village/desert/houses/desert_temple_1"), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), VanillaStructureModifications.singleJigsawPiece("village/desert/houses/desert_temple_2")));
                this.put(new ResourceLocation("village/savanna/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), VanillaStructureModifications.singleJigsawPiece("village/savanna/houses/savanna_temple_1"), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), VanillaStructureModifications.singleJigsawPiece("village/savanna/houses/savanna_temple_2")));
                this.put(new ResourceLocation("village/taiga/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/taiga/houses/taiga_temple_1"), VanillaStructureModifications.singleJigsawPiece("village/taiga/houses/taiga_temple_1", ProcessorLists.MOSSIFY_10_PERCENT)));
                this.put(new ResourceLocation("village/snowy/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), VanillaStructureModifications.singleJigsawPiece("village/snowy/houses/snowy_temple_1")));
                this.put(new ResourceLocation("village/plains/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), VanillaStructureModifications.singleJigsawPiece("village/plains/houses/plains_temple_3", ProcessorLists.ZOMBIE_PLAINS), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), VanillaStructureModifications.singleJigsawPiece("village/plains/houses/plains_temple_4", ProcessorLists.ZOMBIE_PLAINS)));
                this.put(new ResourceLocation("village/desert/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), VanillaStructureModifications.singleJigsawPiece("village/desert/houses/desert_temple_1", ProcessorLists.ZOMBIE_DESERT), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), VanillaStructureModifications.singleJigsawPiece("village/desert/houses/desert_temple_2", ProcessorLists.ZOMBIE_DESERT)));
                this.put(new ResourceLocation("village/savanna/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), VanillaStructureModifications.singleJigsawPiece("village/savanna/houses/savanna_temple_1", ProcessorLists.ZOMBIE_SAVANNA), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), VanillaStructureModifications.singleJigsawPiece("village/savanna/houses/savanna_temple_2", ProcessorLists.ZOMBIE_SAVANNA)));
                this.put(new ResourceLocation("village/taiga/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/taiga/zombie/houses/taiga_temple_1"), VanillaStructureModifications.singleJigsawPiece("village/taiga/houses/taiga_temple_1", ProcessorLists.ZOMBIE_TAIGA)));
                this.put(new ResourceLocation("village/snowy/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), VanillaStructureModifications.singleJigsawPiece("village/snowy/houses/snowy_temple_1", ProcessorLists.ZOMBIE_SNOWY)));
            }
        };
    }

    /**
     * replaces half of the temples with temples with church altar
     */
    private static void replaceTemples(@NotNull RegistryAccess dynamicRegistries, @NotNull Map<ResourceLocation, Map<String, StructurePoolElement>> patternReplacements) {
        // return if temples should not be modified
        if (!VampirismConfig.COMMON.villageReplaceTemples.get()) return;
        // get jigsaw registry
        dynamicRegistries.registry(Registries.TEMPLATE_POOL).ifPresent(jigsawRegistry -> {
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
                            if (targetCountHalf > 1) {
                                targetCountHalf = (int) (((double) targetCountHalf) * 0.6d);
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
                                if (targetCountHalf2 > 1) {
                                    targetCountHalf2 = (int) (((double) targetCountHalf2) * 0.6d);
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
    private static void addHunterTrainerHouse(@NotNull RegistryAccess reg, @NotNull Map<ResourceLocation, VanillaStructureModifications.BiomeType> pools) {
        // get jigsaw registry
        reg.registry(Registries.TEMPLATE_POOL).ifPresent(patternRegistry -> {
            // for every desired pools
            pools.forEach((pool, type) -> {
                // get the pool if present
                patternRegistry.getOptional(pool).ifPresent(pattern -> {
                    // create trainer house piece with desired village type
                    StructurePoolElement piece = singleJigsawPiece("village/" + type.path + "/houses/hunter_trainer");
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

    private static void addTotem(@NotNull RegistryAccess reg, @NotNull Map<ResourceLocation, VanillaStructureModifications.BiomeType> pools) {
        ResourceKey<StructureProcessorList> TOTEM_FACTION_PROCESSOR = ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation(REFERENCE.MODID, "totem_faction"));


        StructurePoolElement totem = singleJigsawPiece("village/totem", TOTEM_FACTION_PROCESSOR);

        reg.registry(Registries.TEMPLATE_POOL).ifPresent((patternRegistry) -> pools.forEach((pool, type) -> {
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
        }));
    }

    private static SinglePoolElement singleJigsawPiece(@NotNull String path) {
        return singleJigsawPiece(path, ProcessorListsAccessor.getEmpty());
    }

    private static SinglePoolElement singleJigsawPiece(@NotNull String path, @NotNull ResourceKey<StructureProcessorList> processors) {
        var holder = VanillaRegistries.createLookup().lookupOrThrow(Registries.PROCESSOR_LIST).getOrThrow(processors);
        return SinglePoolElement.single("vampirism:" + path, holder).apply(StructureTemplatePool.Projection.RIGID);
    }

    private static @NotNull Function<StructureTemplatePool.Projection, SinglePoolElement> singleJigsawPieceFunction(@NotNull String path) {
        return singleJigsawPieceFunction(path, ProcessorListsAccessor.getEmpty());
    }

    private static @NotNull Function<StructureTemplatePool.Projection, SinglePoolElement> singleJigsawPieceFunction(@NotNull String path, @NotNull ResourceKey<StructureProcessorList> processors) {
        var holder = VanillaRegistries.createLookup().lookupOrThrow(Registries.PROCESSOR_LIST).getOrThrow(processors);
        return SinglePoolElement.single("vampirism:" + path, holder);
    }

    private static @NotNull String singleJigsawString(String resourceLocation) {
        return "Single[Left[" + resourceLocation + "]]";
    }

    private static @NotNull String singleLegacyJigsawString(String resourceLocation) {
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
