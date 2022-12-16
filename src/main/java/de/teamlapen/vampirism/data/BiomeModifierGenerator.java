package de.teamlapen.vampirism.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import de.teamlapen.vampirism.world.gen.modifier.ExtendedAddSpawnsBiomeModifier;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BiomeModifierGenerator {

    private final PackOutput packOutput;
    private final ExistingFileHelper exFileHelper;
    private final @NotNull RegistryOps<JsonElement> ops;
    private final @NotNull HolderLookup.RegistryLookup<Biome> biomes;
    private final @NotNull HolderLookup.RegistryLookup<PlacedFeature> placedFeatures;
    private final @NotNull HolderLookup.RegistryLookup<Structure> structures;

    public BiomeModifierGenerator(PackOutput packOutput, ExistingFileHelper exFileHelper, HolderLookup.Provider lookupProvider) {
        this.packOutput = packOutput;
        this.exFileHelper = exFileHelper;
        this.ops = RegistryOps.create(JsonOps.INSTANCE, lookupProvider);
        this.biomes = lookupProvider.lookupOrThrow(Registries.BIOME);
        this.placedFeatures = lookupProvider.lookupOrThrow(Registries.PLACED_FEATURE);
        this.structures = lookupProvider.lookupOrThrow(Registries.STRUCTURE);
    }

    public static void register(@NotNull GatherDataEvent event, @NotNull DataGenerator generator, PackOutput packOutput, HolderLookup.Provider lookupProvider) {
        BiomeModifierGenerator biomeGenerator = new BiomeModifierGenerator(packOutput, event.getExistingFileHelper(), lookupProvider);
        generator.addProvider(event.includeServer(), biomeGenerator.modifierGenerator());
        generator.addProvider(event.includeServer(), biomeGenerator.structureSetGenerator());
    }

    public @NotNull JsonCodecProvider<BiomeModifier> modifierGenerator() {
        return JsonCodecProvider.forDatapackRegistry(this.packOutput, this.exFileHelper, REFERENCE.MODID, this.ops, ForgeRegistries.Keys.BIOME_MODIFIERS, getBiomeModifier());
    }

    public @NotNull JsonCodecProvider<StructureSet> structureSetGenerator() {
        return JsonCodecProvider.forDatapackRegistry(this.packOutput, this.exFileHelper, REFERENCE.MODID, this.ops, Registries.STRUCTURE_SET, getStructureSets());
    }

    private @NotNull Map<ResourceLocation, BiomeModifier> getBiomeModifier() {
        Map<ResourceLocation, BiomeModifier> data = new HashMap<>();
        data.put(new ResourceLocation(REFERENCE.MODID, "spawn/vampire_spawns"), ExtendedAddSpawnsBiomeModifier.singleSpawn(biome(ModTags.Biomes.HasSpawn.VAMPIRE), biome(ModTags.Biomes.NoSpawn.VAMPIRE), new ExtendedAddSpawnsBiomeModifier.ExtendedSpawnData(ModEntities.VAMPIRE.get(), 80, 1, 3, MobCategory.MONSTER)));
        data.put(new ResourceLocation(REFERENCE.MODID, "spawn/advanced_vampire_spawns"), ExtendedAddSpawnsBiomeModifier.singleSpawn(biome(ModTags.Biomes.HasSpawn.ADVANCED_VAMPIRE), biome(ModTags.Biomes.NoSpawn.ADVANCED_VAMPIRE), new ExtendedAddSpawnsBiomeModifier.ExtendedSpawnData(ModEntities.ADVANCED_VAMPIRE.get(), 25, 1, 3, MobCategory.MONSTER)));
        data.put(new ResourceLocation(REFERENCE.MODID, "spawn/hunter_spawns"), ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(biome(ModTags.Biomes.HasSpawn.HUNTER), new MobSpawnSettings.SpawnerData(ModEntities.HUNTER.get(), 0, 1, 3)));
        data.put(new ResourceLocation(REFERENCE.MODID, "spawn/advanced_hunter_spawns"), ForgeBiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(biome(ModTags.Biomes.HasSpawn.ADVANCED_HUNTER), new MobSpawnSettings.SpawnerData(ModEntities.ADVANCED_HUNTER.get(), 0, 1, 1)));

        data.put(new ResourceLocation(REFERENCE.MODID, "feature/vampire_dungeon"), new ForgeBiomeModifiers.AddFeaturesBiomeModifier(biome(ModTags.Biomes.HasStructure.VAMPIRE_DUNGEON), placedFeature(VampirismFeatures.VAMPIRE_DUNGEON_PLACED), GenerationStep.Decoration.UNDERGROUND_STRUCTURES));
        return data;
    }

    private @NotNull Map<ResourceLocation, StructureSet> getStructureSets() {
        Map<ResourceLocation, StructureSet> data = new HashMap<>();
        data.put(new ResourceLocation(REFERENCE.MODID, "hunter_camp"), new StructureSet(structure(ModFeatures.HUNTER_CAMP), new RandomSpreadStructurePlacement(9, 4, RandomSpreadType.LINEAR, 1724616580)));
        return data;
    }

    private @NotNull HolderSet<Biome> biome(@NotNull TagKey<Biome> key) {
        return biomes.getOrThrow(key);
    }

    private @NotNull HolderSet<PlacedFeature> placedFeature(@SuppressWarnings("SameParameterValue") @NotNull ResourceKey<PlacedFeature> placedFeature) {
        return HolderSet.direct(this.placedFeatures.getOrThrow(Objects.requireNonNull(placedFeature)));
    }

    private @NotNull Holder<Structure> structure(@SuppressWarnings("SameParameterValue") ResourceKey<Structure> structure) {
        return this.structures.getOrThrow(Objects.requireNonNull(structure));
    }
}
