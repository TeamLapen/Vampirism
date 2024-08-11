package de.teamlapen.vampirism.world.gen;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.mixin.accessor.ProcessorListsAccessor;
import de.teamlapen.vampirism.mixin.accessor.StructureTemplatePoolAccessor;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 * Vanilla structures are modified in the following ways:
 * 1) Village structures are modified to include totem and hunter trainer house. Furthermore, some temples are replaced with custom versions. This is done during common setup.
 * 2) Explicitely specified structture pieces are limit to once per structure  via {@link de.teamlapen.vampirism.mixin.MixinJigsawPlacer}.
 */
public class VanillaStructureModifications {

    public static void addVillageStructures(@NotNull RegistryAccess dynamicRegistries) {
        addHunterTrainerHouse(dynamicRegistries, getDefaultPools());
        addTotem(dynamicRegistries, getDefaultPools());
        replaceTemples(dynamicRegistries, getTempleReplacements(dynamicRegistries.lookupOrThrow(Registries.PROCESSOR_LIST)));
    }

    /**
     * @return a map the maps {@link StructurePoolElement}s that should be modified to the type village type of added objects
     */
    private static @NotNull Map<ResourceLocation, BiomeType> getDefaultPools() {
        return Map.ofEntries(Map.entry(VResourceLocation.mc("village/plains/houses"), VanillaStructureModifications.BiomeType.PLAINS), Map.entry(VResourceLocation.mc("village/desert/houses"), VanillaStructureModifications.BiomeType.DESERT), Map.entry(VResourceLocation.mc("village/savanna/houses"), VanillaStructureModifications.BiomeType.SAVANNA), Map.entry(VResourceLocation.mc("village/taiga/houses"), VanillaStructureModifications.BiomeType.TAIGA), Map.entry(VResourceLocation.mc("village/snowy/houses"), VanillaStructureModifications.BiomeType.SNOWY));
    }

    /**
     * @return a map that maps {@link StructureTemplatePool}s that should be modified to a map that maps temple {@link StructurePoolElement}s to modified temple {@link StructurePoolElement}s
     */
    private static @NotNull Map<ResourceLocation, Map<String, StructurePoolElement>> getTempleReplacements(@NotNull HolderGetter<StructureProcessorList> processorList) {
        return new HashMap<>() {
            {
                this.put(VResourceLocation.mc("village/plains/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/plains/houses/plains_temple_3", ProcessorLists.MOSSIFY_10_PERCENT), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/plains/houses/plains_temple_4", ProcessorLists.MOSSIFY_10_PERCENT)));
                this.put(VResourceLocation.mc("village/desert/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/desert/houses/desert_temple_1"), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/desert/houses/desert_temple_2")));
                this.put(VResourceLocation.mc("village/savanna/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/savanna/houses/savanna_temple_1"), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/savanna/houses/savanna_temple_2")));
                this.put(VResourceLocation.mc("village/taiga/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/taiga/houses/taiga_temple_1"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/taiga/houses/taiga_temple_1", ProcessorLists.MOSSIFY_10_PERCENT)));
                this.put(VResourceLocation.mc("village/snowy/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/snowy/houses/snowy_temple_1")));
                this.put(VResourceLocation.mc("village/plains/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_3"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/plains/houses/plains_temple_3", ProcessorLists.ZOMBIE_PLAINS), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/plains/houses/plains_temple_4"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/plains/houses/plains_temple_4", ProcessorLists.ZOMBIE_PLAINS)));
                this.put(VResourceLocation.mc("village/desert/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_1"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/desert/houses/desert_temple_1", ProcessorLists.ZOMBIE_DESERT), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/desert/houses/desert_temple_2"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/desert/houses/desert_temple_2", ProcessorLists.ZOMBIE_DESERT)));
                this.put(VResourceLocation.mc("village/savanna/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_1"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/savanna/houses/savanna_temple_1", ProcessorLists.ZOMBIE_SAVANNA), VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/savanna/houses/savanna_temple_2"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/savanna/houses/savanna_temple_2", ProcessorLists.ZOMBIE_SAVANNA)));
                this.put(VResourceLocation.mc("village/taiga/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/taiga/zombie/houses/taiga_temple_1"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/taiga/houses/taiga_temple_1", ProcessorLists.ZOMBIE_TAIGA)));
                this.put(VResourceLocation.mc("village/snowy/zombie/houses"), ImmutableMap.of(VanillaStructureModifications.singleLegacyJigsawString("minecraft:village/snowy/houses/snowy_temple_1"), VanillaStructureModifications.singleJigsawPiece(processorList,"village/snowy/houses/snowy_temple_1", ProcessorLists.ZOMBIE_SNOWY)));
            }
        };
    }

    /**
     * replaces half of the temples with temples with church altar
     */
    private static void replaceTemples(@NotNull RegistryAccess dynamicRegistries, @NotNull Map<ResourceLocation, Map<String, StructurePoolElement>> patternReplacements) {
        // return if temples should not be modified
        if (!VampirismConfig.SERVER.villageReplaceTemples.get()) return;
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
                            ((StructureTemplatePoolAccessor) pattern).getTemplates().removeIf(piece -> {
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
                                var templates = ((StructureTemplatePoolAccessor) pattern).getTemplates();
                                templates.add(modified);
                                templates.add(oldPieces.get(i));
                            }

                            // Add modified temple pieces to the weighted list for better mod compat if other mods read this field instead of templates
                            // (ex: Repurposed Structures)
                            List<Pair<StructurePoolElement, Integer>> weightedElementList = new ArrayList<>(((StructureTemplatePoolAccessor) pattern).getRawTemplates());
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
                            ((StructureTemplatePoolAccessor) pattern).setRawTemplates(weightedElementList);
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
                    StructurePoolElement piece = singleJigsawPiece(reg.lookupOrThrow(Registries.PROCESSOR_LIST), "village/" + type.path + "/houses/hunter_trainer");
                    // add hunter trainer house with weight
                    for (int i = 0; i < VampirismConfig.SERVER.villageHunterTrainerWeight.get(); i++) {
                        ((StructureTemplatePoolAccessor) pattern).getTemplates().add(piece);
                    }

                    // Add hunter trainer house to the weighted list for better mod compat if other mods read this field instead of templates
                    // (ex: Repurposed Structures)
                    List<Pair<StructurePoolElement, Integer>> weightedElementList = new ArrayList<>(((StructureTemplatePoolAccessor) pattern).getRawTemplates());
                    weightedElementList.add(new Pair<>(piece, VampirismConfig.SERVER.villageHunterTrainerWeight.get()));
                    ((StructureTemplatePoolAccessor) pattern).setRawTemplates(weightedElementList);
                });
            });
        });

    }

    private static void addTotem(@NotNull RegistryAccess reg, @NotNull Map<ResourceLocation, VanillaStructureModifications.BiomeType> pools) {
        ResourceKey<StructureProcessorList> TOTEM_FACTION_PROCESSOR = ResourceKey.create(Registries.PROCESSOR_LIST, VResourceLocation.mod("totem_faction"));


        StructurePoolElement totem = singleJigsawPiece(reg.lookupOrThrow(Registries.PROCESSOR_LIST),"village/totem", TOTEM_FACTION_PROCESSOR);

        reg.registry(Registries.TEMPLATE_POOL).ifPresent((patternRegistry) -> pools.forEach((pool, type) -> {
            // get the pool if present
            patternRegistry.getOptional(pool).ifPresent((pattern) -> {
                // add totem with weight
                for (int i = 0; i < VampirismConfig.SERVER.villageTotemWeight.get(); ++i) {
                    ((StructureTemplatePoolAccessor) pattern).getTemplates().add(totem);
                }

                // Add totem house to the weighted list for better mod compat if other mods read this field instead of templates
                // (ex: Repurposed Structures)
                List<Pair<StructurePoolElement, Integer>> weightedElementList = new ArrayList<>(((StructureTemplatePoolAccessor) pattern).getRawTemplates());
                weightedElementList.add(new Pair<>(totem, VampirismConfig.SERVER.villageTotemWeight.get()));
                ((StructureTemplatePoolAccessor) pattern).setRawTemplates(weightedElementList);
            });
        }));
    }

    private static SinglePoolElement singleJigsawPiece(@NotNull HolderGetter<StructureProcessorList> processorList, @NotNull String path) {
        return singleJigsawPiece(processorList, path, ProcessorListsAccessor.getEmpty());
    }

    private static SinglePoolElement singleJigsawPiece(@NotNull HolderGetter<StructureProcessorList> processorList, @NotNull String path, @NotNull ResourceKey<StructureProcessorList> processors) {
        var holder = processorList.getOrThrow(processors);
        return SinglePoolElement.single("vampirism:" + path, holder).apply(StructureTemplatePool.Projection.RIGID);
    }

    public static @NotNull Function<StructureTemplatePool.Projection, SinglePoolElement> singleJigsawPieceFunction(@NotNull HolderGetter<StructureProcessorList> processorList, @NotNull String path) {
        return singleJigsawPieceFunction(processorList, path, ProcessorListsAccessor.getEmpty());
    }

    public static @NotNull Function<StructureTemplatePool.Projection, SinglePoolElement> singleJigsawPieceFunction(@NotNull HolderGetter<StructureProcessorList> processorList, @NotNull String path, @NotNull ResourceKey<StructureProcessorList> processors) {
        var holder = processorList.getOrThrow(processors);
        return SinglePoolElement.single("vampirism:" + path, holder);
    }

    private static @NotNull String singleJigsawString(String resourceLocation) {
        return "Single[Left[" + resourceLocation + "]]";
    }

    private static @NotNull String singleLegacyJigsawString(String resourceLocation) {
        return "LegacySingle[Left[" + resourceLocation + "]]";
    }

    public enum BiomeType {
        PLAINS("plains"), TAIGA("taiga"), DESERT("desert"), SNOWY("snowy"), SAVANNA("savanna"), BADLANDS("badlands"), BIRCH("birch"), DARK_FOREST("dark_forest"), CRIMSON("crimson"), GIANT_TAIGA("giant_taiga"), JUNGLE("jungle"), MOUNTAINS("mountains"), OAK("oak"), SWAMP("swamp"), WARPED("warped");

        public final String path;

        BiomeType(String path) {
            this.path = path;
        }
    }
}
