package de.teamlapen.vampirism.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModFeatures;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import de.teamlapen.vampirism.world.gen.modifier.ExtendedAddSpawnsBiomeModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.DataGenerator;
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
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BiomeGenerator {

    private final DataGenerator gen;
    private final ExistingFileHelper exFileHelper;
    private final @NotNull RegistryOps<JsonElement> ops;
    private final @NotNull Registry<Biome> biomes;
    private final @NotNull Registry<PlacedFeature> placedFeatures;
    private final @NotNull Registry<Structure> structures;

    public BiomeGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        this.gen = gen;
        this.exFileHelper = exFileHelper;
        RegistryAccess access = RegistryAccess.builtinCopy();
        this.ops = RegistryOps.create(JsonOps.INSTANCE, access);
        this.biomes = this.ops.registry(Registry.BIOME_REGISTRY).orElseThrow();
        this.placedFeatures = this.ops.registry(Registry.PLACED_FEATURE_REGISTRY).orElseThrow();
        this.structures = this.ops.registry(Registry.STRUCTURE_REGISTRY).orElseThrow();
    }

    public static void register(@NotNull GatherDataEvent event, @NotNull DataGenerator generator) {
        BiomeGenerator biomeGenerator = new BiomeGenerator(generator, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), biomeGenerator.modifierGenerator());
        generator.addProvider(event.includeServer(), biomeGenerator.structureSetGenerator());
    }

    public @NotNull JsonCodecProvider<BiomeModifier> modifierGenerator() {
        return JsonCodecProvider.forDatapackRegistry(this.gen, this.exFileHelper, REFERENCE.MODID, this.ops, ForgeRegistries.Keys.BIOME_MODIFIERS, getBiomeModifier());
    }

    public @NotNull JsonCodecProvider<StructureSet> structureSetGenerator() {
        return JsonCodecProvider.forDatapackRegistry(this.gen, this.exFileHelper, REFERENCE.MODID, this.ops, Registry.STRUCTURE_SET_REGISTRY, getStructureSets());
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
        data.put(new ResourceLocation(REFERENCE.MODID, "hunter_camp"), new StructureSet(structure(ModFeatures.HUNTER_CAMP_KEY), new RandomSpreadStructurePlacement(9, 4, RandomSpreadType.LINEAR, 1724616580)));
        return data;
    }

    private @NotNull HolderSet<Biome> biome(@NotNull TagKey<Biome> key) {
        return biomes.getOrCreateTag(key);
    }

    private @NotNull HolderSet<PlacedFeature> placedFeature(@SuppressWarnings("SameParameterValue") @NotNull RegistryObject<PlacedFeature> placedFeature) {
        return HolderSet.direct(this.placedFeatures.getHolderOrThrow(Objects.requireNonNull(placedFeature.getKey())));
    }

    private @NotNull Holder<Structure> structure(@SuppressWarnings("SameParameterValue") ResourceKey<Structure> structure) {
        return this.structures.getHolderOrThrow(Objects.requireNonNull(structure));
    }
}
