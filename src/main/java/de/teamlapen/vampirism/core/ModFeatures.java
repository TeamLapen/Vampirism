package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.mixin.StructuresAccessor;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import de.teamlapen.vampirism.world.gen.VanillaStructureModifications;
import de.teamlapen.vampirism.world.gen.feature.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.feature.treedecorators.TrunkCursedVineDecorator;
import de.teamlapen.vampirism.world.gen.structure.huntercamp.HunterCampStructure;
import de.teamlapen.vampirism.world.gen.structure.hunteroutpost.HunterOutpostStructure;
import de.teamlapen.vampirism.world.gen.structure.vampirealtar.VampireAltarStructure;
import de.teamlapen.vampirism.world.gen.structure.vampirehut.VampireHutStructure;
import net.minecraft.core.HolderGetter;
import de.teamlapen.vampirism.world.gen.structure.mother.MotherStructure;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

/**
 * For new dynamic registry related things see {@link VampirismFeatures} and {@link VanillaStructureModifications}
 */
public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, REFERENCE.MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, REFERENCE.MODID);

    public static final ResourceKey<Structure> HUNTER_CAMP = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(REFERENCE.MODID, "hunter_camp"));
    public static final ResourceKey<Structure> VAMPIRE_HUT = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(REFERENCE.MODID, "vampire_hut"));
    public static final ResourceKey<Structure> HUNTER_OUTPOST = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(REFERENCE.MODID, "hunter_outpost"));
    public static final ResourceKey<Structure> VAMPIRE_ALTAR = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(REFERENCE.MODID, "vampire_altar"));

    public static final RegistryObject<StructureType<HunterCampStructure>> HUNTER_CAMP_TYPE = STRUCTURE_TYPES.register("hunter_camp", () -> () -> HunterCampStructure.CODEC);
    public static final RegistryObject<StructureType<VampireHutStructure>> VAMPIRE_HUT_TYPE = STRUCTURE_TYPES.register("vampire_hut", () -> () -> VampireHutStructure.CODEC);
    public static final RegistryObject<StructureType<HunterOutpostStructure>> HUNTER_OUTPOST_TYPE = STRUCTURE_TYPES.register("outpost", () -> () -> HunterOutpostStructure.CODEC);
    public static final RegistryObject<StructureType<VampireAltarStructure>> VAMPIRE_ALTAR_TYPE = STRUCTURE_TYPES.register("vampire_altar", () -> () -> VampireAltarStructure.CODEC);

    public static final RegistryObject<VampireDungeonFeature> VAMPIRE_DUNGEON = FEATURES.register("vampire_dungeon", () -> new VampireDungeonFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<TreeDecoratorType<TrunkCursedVineDecorator>> trunk_cursed_vine = TREE_DECORATOR.register("trunk_cursed_vine", () -> new TreeDecoratorType<>(TrunkCursedVineDecorator.CODEC));

    public static final ResourceKey<Structure> MOTHER_KEY = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(REFERENCE.MODID, "mother"));
    public static final RegistryObject<StructureType<MotherStructure>> MOTHER = STRUCTURE_TYPES.register("mother", () -> () -> MotherStructure.CODEC);

    static void register(IEventBus bus) {
        FEATURES.register(bus);
        STRUCTURE_TYPES.register(bus);
        TREE_DECORATOR.register(bus);
    }

    public static void createStructures(BootstapContext<Structure> context) {
        HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);

        // it is currently not possible to create a not holder in datagen see https://github.com/MinecraftForge/MinecraftForge/issues/9629
        // this file is not generated, but added through the main source set
        // context.register(ModFeatures.HUNTER_CAMP, new HunterCampStructure(StructuresAccessor.structure(new AndHolderSet<>(List.of(lookup.getOrThrow(ModTags.Biomes.HasStructure.HUNTER_TENT), new NotHolderSet<>(context.registryLookup(Registries.BIOME).orElseThrow(),lookup.getOrThrow(ModTags.Biomes.IS_FACTION_BIOME)))), TerrainAdjustment.BEARD_THIN)));
        context.register(ModFeatures.VAMPIRE_HUT, new VampireHutStructure(StructuresAccessor.structure(lookup.getOrThrow(ModTags.Biomes.HasStructure.VAMPIRE_HUT), TerrainAdjustment.NONE)));
        context.register(ModFeatures.HUNTER_OUTPOST, new HunterOutpostStructure(new Structure.StructureSettings(lookup.getOrThrow(ModTags.Biomes.HasStructure.HUNTER_OUTPOST), Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(ModEntities.HUNTER.get(), 60, 1, 2), new MobSpawnSettings.SpawnerData(ModEntities.ADVANCED_HUNTER.get(), 20, 1, 1)))) , GenerationStep.Decoration.SURFACE_STRUCTURES,TerrainAdjustment.BEARD_BOX)));
        context.register(ModFeatures.VAMPIRE_ALTAR, new VampireAltarStructure(StructuresAccessor.structure(lookup.getOrThrow(ModTags.Biomes.HasStructure.VAMPIRE_ALTAR), TerrainAdjustment.BEARD_BOX)));
        context.register(ModFeatures.MOTHER_KEY, new MotherStructure(StructuresAccessor.structure(lookup.getOrThrow(ModTags.Biomes.HasStructure.MOTHER), TerrainAdjustment.NONE)));
    }
}
