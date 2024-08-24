package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.core.tags.ModBiomeTags;
import de.teamlapen.vampirism.world.gen.structure.crypt.CryptStructurePieces;
import de.teamlapen.vampirism.world.gen.structure.huntercamp.HunterCampPieces;
import de.teamlapen.vampirism.world.gen.structure.huntercamp.HunterCampStructure;
import de.teamlapen.vampirism.world.gen.structure.hunteroutpost.*;
import de.teamlapen.vampirism.world.gen.structure.mother.MotherPiece;
import de.teamlapen.vampirism.world.gen.structure.mother.MotherStructure;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.BiomeTopBlockProcessor;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomBlockStateRule;
import de.teamlapen.vampirism.world.gen.structure.templatesystem.RandomStructureProcessor;
import de.teamlapen.vampirism.world.gen.structure.vampirealtar.VampireAltarPieces;
import de.teamlapen.vampirism.world.gen.structure.vampirealtar.VampireAltarStructure;
import de.teamlapen.vampirism.world.gen.structure.vampirehut.VampireHutPieces;
import de.teamlapen.vampirism.world.gen.structure.vampirehut.VampireHutStructure;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.teamlapen.vampirism.world.gen.VanillaStructureModifications.singleJigsawPieceFunction;

public class ModStructures {

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, REFERENCE.MODID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister.create(Registries.STRUCTURE_PIECE, REFERENCE.MODID);
    public static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, REFERENCE.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<HunterCampStructure>> HUNTER_CAMP_TYPE = STRUCTURE_TYPES.register("hunter_camp", () -> () -> HunterCampStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<VampireHutStructure>> VAMPIRE_HUT_TYPE = STRUCTURE_TYPES.register("vampire_hut", () -> () -> VampireHutStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<VampireAltarStructure>> VAMPIRE_ALTAR_TYPE = STRUCTURE_TYPES.register("vampire_altar", () -> () -> VampireAltarStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<MotherStructure>> MOTHER_TYPE = STRUCTURE_TYPES.register("mother", () -> () -> MotherStructure.CODEC);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> HUNTER_CAMP_FIREPLACE = STRUCTURE_PIECES.register("hunter_camp_fireplace", () -> (StructurePieceType.ContextlessType) HunterCampPieces.Fireplace::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> HUNTER_CAMP_TENT = STRUCTURE_PIECES.register("hunter_camp_tent", () -> (StructurePieceType.ContextlessType) HunterCampPieces.Tent::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> HUNTER_CAMP_SPECIAL = STRUCTURE_PIECES.register("hunter_camp_craftingtable", () -> (StructurePieceType.ContextlessType) HunterCampPieces.SpecialBlock::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> VAMPIRE_HUT_PIECE = STRUCTURE_PIECES.register("vampire_hut", () -> (StructurePieceType.StructureTemplateType) VampireHutPieces.VampireHutPiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> VAMPIRE_ALTAR_PIECE = STRUCTURE_PIECES.register("vampire_altar", () -> (StructurePieceType.StructureTemplateType) VampireAltarPieces.VampireAltarPiece::new);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> MOTHER_PIECE = STRUCTURE_PIECES.register("mother", () -> (StructurePieceType.ContextlessType) MotherPiece::new);

    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<RandomStructureProcessor>> RANDOM_SELECTOR = STRUCTURE_PROCESSOR_TYPES.register("random_selector", () -> () -> RandomStructureProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>, StructureProcessorType<BiomeTopBlockProcessor>> BIOME_BASED = STRUCTURE_PROCESSOR_TYPES.register("biome_based", () -> () -> BiomeTopBlockProcessor.CODEC);

    public static final ResourceKey<Structure> HUNTER_CAMP = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("hunter_camp"));
    public static final ResourceKey<Structure> VAMPIRE_HUT = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("vampire_hut"));
    public static final ResourceKey<Structure> HUNTER_OUTPOST_PLAINS = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("hunter_outpost_plains"));
    public static final ResourceKey<Structure> HUNTER_OUTPOST_DESERT = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("hunter_outpost_desert"));
    public static final ResourceKey<Structure> HUNTER_OUTPOST_VAMPIRE_FOREST = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("hunter_outpost_vampire_forest"));
    public static final ResourceKey<Structure> HUNTER_OUTPOST_BADLANDS = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("hunter_outpost_badlands"));
    public static final ResourceKey<Structure> VAMPIRE_ALTAR = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("vampire_altar"));
    public static final ResourceKey<Structure> MOTHER = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("mother"));
    public static final ResourceKey<Structure> CRYPT = ResourceKey.create(Registries.STRUCTURE, VResourceLocation.mod("crypt"));

    public static final ResourceKey<StructureTemplatePool> HUNTER_TRAINER = createTemplatePool("village/entities/hunter_trainer");
    public static final ResourceKey<StructureProcessorList> TOTEM_FACTION = createProcessorList("totem_faction");

    public static final ResourceKey<StructureSet> HUNTER_CAMP_SET = createStructureSetKey("hunter_camp");
    public static final ResourceKey<StructureSet> VAMPIRE_HUT_SET = createStructureSetKey("vampire_hut");
    public static final ResourceKey<StructureSet> VAMPIRE_ALTAR_SET = createStructureSetKey("vampire_altar");
    public static final ResourceKey<StructureSet> HUNTER_OUTPOST = createStructureSetKey("hunter_outpost");
    public static final ResourceKey<StructureSet> MOTHER_SET = createStructureSetKey("mother");
    public static final ResourceKey<StructureSet> CRYPT_SET = createStructureSetKey("crypt");

    private static ResourceKey<StructureSet> createStructureSetKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, VResourceLocation.mod(name));
    }

    public static ResourceKey<StructureTemplatePool> createTemplatePool(@SuppressWarnings("SameParameterValue") String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, VResourceLocation.mod(name));
    }

    private static ResourceKey<StructureProcessorList> createProcessorList(@SuppressWarnings("SameParameterValue") String name) {
        return ResourceKey.create(Registries.PROCESSOR_LIST, VResourceLocation.mod(name));
    }

    static void register(IEventBus bus) {
        STRUCTURE_TYPES.register(bus);
        STRUCTURE_PIECES.register(bus);
        STRUCTURE_PROCESSOR_TYPES.register(bus);
    }

    static void createStructurePoolTemplates(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureTemplatePool> holderGetter = context.lookup(Registries.TEMPLATE_POOL);
        HolderGetter<StructureProcessorList> processorList = context.lookup(Registries.PROCESSOR_LIST);

        Holder<StructureTemplatePool> empty = holderGetter.getOrThrow(Pools.EMPTY);

        context.register(HUNTER_TRAINER, new StructureTemplatePool(empty, Lists.newArrayList(Pair.of(singleJigsawPieceFunction(processorList, "village/entities/hunter_trainer"), 1)), StructureTemplatePool.Projection.RIGID));
        CryptStructurePieces.bootstrap(context);
        HunterOutpostPools.bootstrap(context);
    }

    static void createStructureProcessorLists(BootstrapContext<StructureProcessorList> context) {
        StructureProcessor factionProcessor = new RandomStructureProcessor(ImmutableList.of(new RandomBlockStateRule(new RandomBlockMatchTest(ModBlocks.TOTEM_TOP.get(), 0.6f), AlwaysTrueTest.INSTANCE, ModBlocks.TOTEM_TOP.get().defaultBlockState(), TotemTopBlock.getBlocks().stream().filter((totemx) -> totemx != ModBlocks.TOTEM_TOP.get() && !totemx.isCrafted()).map(Block::defaultBlockState).collect(Collectors.toList()))));
        StructureProcessor biomeTopBlockProcessor = new BiomeTopBlockProcessor(Blocks.DIRT.defaultBlockState());

        context.register(TOTEM_FACTION, new StructureProcessorList(ImmutableList.of(factionProcessor, biomeTopBlockProcessor)));
    }

    static void createStructureSets(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structureLookup = context.lookup(Registries.STRUCTURE);
        HolderGetter<StructureSet> structureSetLookup = context.lookup(Registries.STRUCTURE_SET);
        var villageSet = structureSetLookup.getOrThrow(BuiltinStructureSets.VILLAGES);
        // hunter camp holder is not available in data generation see ModFeatures#createStructures
//         context.register(HUNTER_CAMP_SET, new StructureSet(structureLookup.getOrThrow(HUNTER_CAMP), new RandomSpreadStructurePlacement(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, 1724616580, Optional.of(new StructurePlacement.ExclusionZone(villageSet,2)),9, 4, RandomSpreadType.LINEAR)));
        context.register(VAMPIRE_HUT_SET, new StructureSet(structureLookup.getOrThrow(VAMPIRE_HUT), new RandomSpreadStructurePlacement(32, 10, RandomSpreadType.LINEAR, 1937195837)));
        context.register(VAMPIRE_ALTAR_SET, new StructureSet(structureLookup.getOrThrow(VAMPIRE_ALTAR), new RandomSpreadStructurePlacement(32, 15, RandomSpreadType.LINEAR, 573190874)));
        context.register(MOTHER_SET, new StructureSet(structureLookup.getOrThrow(MOTHER), new RandomSpreadStructurePlacement(48, 6, RandomSpreadType.TRIANGULAR, 1897236459)));
        context.register(CRYPT_SET, new StructureSet(structureLookup.getOrThrow(CRYPT), new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 643510199)));
        context.register(HUNTER_OUTPOST, new StructureSet(List.of(StructureSet.entry(structureLookup.getOrThrow(HUNTER_OUTPOST_PLAINS)), StructureSet.entry(structureLookup.getOrThrow(HUNTER_OUTPOST_DESERT)), StructureSet.entry(structureLookup.getOrThrow(HUNTER_OUTPOST_VAMPIRE_FOREST)), StructureSet.entry(structureLookup.getOrThrow(HUNTER_OUTPOST_BADLANDS))), new RandomSpreadStructurePlacement(45, 25, RandomSpreadType.LINEAR, 36413509)));
    }

    @SuppressWarnings("UnreachableCode")
    static void createStructures(BootstrapContext<Structure> context) {
        HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> lookup1 = context.lookup(Registries.TEMPLATE_POOL);

        // it is currently not possible to create a not holder in datagen see https://github.com/MinecraftForge/MinecraftForge/issues/9629
        // this file is not generated, but added through the main source set
//        context.register(HUNTER_CAMP, new HunterCampStructure(new Structure.StructureSettings.Builder(new AndHolderSet<>(List.of(lookup.getOrThrow(ModBiomeTags.HasStructure.HUNTER_TENT), new NotHolderSet<>(context.registryLookup(Registries.BIOME).orElseThrow(),lookup.getOrThrow(ModBiomeTags.IS_FACTION_BIOME))))).terrainAdapation(TerrainAdjustment.BEARD_THIN).build()));
        context.register(VAMPIRE_HUT, new VampireHutStructure(new Structure.StructureSettings.Builder(lookup.getOrThrow(ModBiomeTags.HasStructure.VAMPIRE_HUT)).terrainAdapation(TerrainAdjustment.NONE).build()));
        context.register(HUNTER_OUTPOST_PLAINS, new JigsawStructure(new Structure.StructureSettings.Builder(lookup.getOrThrow(ModBiomeTags.HasStructure.HUNTER_OUTPOST_PLAINS)).spawnOverrides(Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(ModEntities.HUNTER.get(), 80, 2, 4), new MobSpawnSettings.SpawnerData(ModEntities.ADVANCED_HUNTER.get(), 20, 1, 22))))).generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES).terrainAdapation(TerrainAdjustment.BEARD_THIN).build(), lookup1.getOrThrow(PlainsHunterOutpostPools.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        context.register(HUNTER_OUTPOST_DESERT, new JigsawStructure(new Structure.StructureSettings.Builder(lookup.getOrThrow(ModBiomeTags.HasStructure.HUNTER_OUTPOST_DESERT)).spawnOverrides(Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(ModEntities.HUNTER.get(), 80, 2, 4), new MobSpawnSettings.SpawnerData(ModEntities.ADVANCED_HUNTER.get(), 20, 1, 22))))).generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES).terrainAdapation(TerrainAdjustment.BEARD_THIN).build(), lookup1.getOrThrow(DesertHunterOutpostPools.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        context.register(HUNTER_OUTPOST_VAMPIRE_FOREST, new JigsawStructure(new Structure.StructureSettings.Builder(lookup.getOrThrow(ModBiomeTags.HasStructure.HUNTER_OUTPOST_VAMPIRE_FOREST)).spawnOverrides(Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(ModEntities.HUNTER.get(), 80, 2, 4), new MobSpawnSettings.SpawnerData(ModEntities.ADVANCED_HUNTER.get(), 20, 1, 22))))).generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES).terrainAdapation(TerrainAdjustment.BEARD_THIN).build(), lookup1.getOrThrow(VampireForestHunterOutpostPools.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        context.register(HUNTER_OUTPOST_BADLANDS, new JigsawStructure(new Structure.StructureSettings.Builder(lookup.getOrThrow(ModBiomeTags.HasStructure.HUNTER_OUTPOST_BADLANDS)).spawnOverrides(Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(ModEntities.HUNTER.get(), 80, 2, 4), new MobSpawnSettings.SpawnerData(ModEntities.ADVANCED_HUNTER.get(), 20, 1, 22))))).generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES).terrainAdapation(TerrainAdjustment.BEARD_THIN).build(), lookup1.getOrThrow(BadlandsHunterOutpostPools.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        context.register(VAMPIRE_ALTAR, new VampireAltarStructure(new Structure.StructureSettings.Builder(lookup.getOrThrow(ModBiomeTags.HasStructure.VAMPIRE_ALTAR)).terrainAdapation(TerrainAdjustment.BEARD_BOX).build()));
        context.register(MOTHER, new MotherStructure(new Structure.StructureSettings.Builder(lookup.getOrThrow(ModBiomeTags.HasStructure.MOTHER)).terrainAdapation(TerrainAdjustment.NONE).build()));
        context.register(CRYPT, new JigsawStructure(new Structure.StructureSettings.Builder(lookup.getOrThrow(ModBiomeTags.HasStructure.CRYPT)).terrainAdapation(TerrainAdjustment.BEARD_THIN).build(), lookup1.getOrThrow(CryptStructurePieces.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), false, Heightmap.Types.WORLD_SURFACE_WG));
    }
}
