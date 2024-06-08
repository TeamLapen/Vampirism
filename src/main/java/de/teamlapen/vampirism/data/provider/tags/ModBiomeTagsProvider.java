package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.tags.ModBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends BiomeTagsProvider {

    public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, REFERENCE.MODID, existingFileHelper);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.@NotNull Provider holderProvider) {
        tag(ModBiomeTags.HasStructure.HUNTER_TENT).addTags(BiomeTags.IS_BADLANDS, BiomeTags.IS_FOREST, BiomeTags.IS_TAIGA).add(Biomes.PLAINS, Biomes.DESERT, Biomes.MEADOW, Biomes.SNOWY_PLAINS, Biomes.SPARSE_JUNGLE);
        tag(ModBiomeTags.HasFaction.IS_FACTION_BIOME).addTags(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME, ModBiomeTags.HasFaction.IS_HUNTER_BIOME);
        tag(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME).add(ModBiomes.VAMPIRE_FOREST);
        tag(ModBiomeTags.HasFaction.IS_HUNTER_BIOME);
        tag(ModBiomeTags.HasStructure.VAMPIRE_ALTAR).addTags(Tags.Biomes.IS_WASTELAND, Tags.Biomes.IS_PLATEAU, Tags.Biomes.IS_RARE, Tags.Biomes.IS_SPOOKY, Tags.Biomes.IS_SWAMP, ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME);
        tag(ModBiomeTags.HasStructure.HUNTER_OUTPOST_PLAINS).addTags(Tags.Biomes.IS_PLAINS, BiomeTags.IS_FOREST);
        tag(ModBiomeTags.HasStructure.HUNTER_OUTPOST_DESERT).addTags(Tags.Biomes.IS_DESERT);
        tag(ModBiomeTags.HasStructure.HUNTER_OUTPOST_VAMPIRE_FOREST).add(ModBiomes.VAMPIRE_FOREST);
        tag(ModBiomeTags.HasStructure.HUNTER_OUTPOST_BADLANDS).addTags(BiomeTags.IS_BADLANDS);
        tag(BiomeTags.IS_FOREST).add(ModBiomes.VAMPIRE_FOREST);
        tag(BiomeTags.IS_OVERWORLD).add(ModBiomes.VAMPIRE_FOREST);
        tag(Tags.Biomes.IS_DENSE_VEGETATION_OVERWORLD).add(ModBiomes.VAMPIRE_FOREST);
        tag(Tags.Biomes.IS_MAGICAL).add(ModBiomes.VAMPIRE_FOREST);
        tag(Tags.Biomes.IS_SPOOKY).add(ModBiomes.VAMPIRE_FOREST);
        tag(ModBiomeTags.HasStructure.VAMPIRE_DUNGEON).addTags(BiomeTags.IS_OVERWORLD);
        tag(ModBiomeTags.HasSpawn.VAMPIRE).addTags(BiomeTags.IS_OVERWORLD);
        tag(ModBiomeTags.NoSpawn.VAMPIRE).addTags(ModBiomeTags.HasFaction.IS_FACTION_BIOME, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_MUSHROOM);
        tag(ModBiomeTags.HasSpawn.ADVANCED_VAMPIRE).addTags(BiomeTags.IS_OVERWORLD);
        tag(ModBiomeTags.NoSpawn.ADVANCED_VAMPIRE).addTags(ModBiomeTags.HasFaction.IS_FACTION_BIOME, Tags.Biomes.IS_UNDERGROUND, Tags.Biomes.IS_MUSHROOM);
        tag(ModBiomeTags.HasSpawn.HUNTER).addTags(BiomeTags.IS_OVERWORLD);
        tag(ModBiomeTags.NoSpawn.HUNTER).addTags(ModBiomeTags.HasFaction.IS_FACTION_BIOME);
        tag(ModBiomeTags.HasSpawn.ADVANCED_HUNTER).addTags(BiomeTags.IS_OVERWORLD);
        tag(ModBiomeTags.NoSpawn.ADVANCED_HUNTER).addTags(ModBiomeTags.HasFaction.IS_FACTION_BIOME);
        tag(ModBiomeTags.HasStructure.VAMPIRE_HUT).addTags(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME);
        tag(ModBiomeTags.HasStructure.MOTHER).addTag(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME);
        tag(ModBiomeTags.HasStructure.CRYPT).addTag(ModBiomeTags.HasFaction.IS_VAMPIRE_BIOME);
        tag(Tags.Biomes.NO_DEFAULT_MONSTERS).add(ModBiomes.VAMPIRE_FOREST);
    }
}
