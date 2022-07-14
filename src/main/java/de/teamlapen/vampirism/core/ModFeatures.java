package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import de.teamlapen.vampirism.world.gen.VanillaStructureModifications;
import de.teamlapen.vampirism.world.gen.features.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.structures.huntercamp.HunterCampStructure;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.data.worldgen.Structures.structure;

/**
 * For new dynamic registry related things see {@link VampirismFeatures} and {@link VanillaStructureModifications}
 */
public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, REFERENCE.MODID);
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registry.STRUCTURE_TYPE_REGISTRY, REFERENCE.MODID);

    public static final ResourceKey<Structure> HUNTER_CAMP_KEY = ResourceKey.create(Registry.STRUCTURE_REGISTRY, new ResourceLocation(REFERENCE.MODID, "hunter_camp"));
    public static final RegistryObject<StructureType<HunterCampStructure>> HUNTER_CAMP = STRUCTURE_TYPES.register( "hunter_camp", () -> () -> HunterCampStructure.CODEC);
    //features
    public static final RegistryObject<VampireDungeonFeature> VAMPIRE_DUNGEON = FEATURES.register("vampire_dungeon", () -> new VampireDungeonFeature(NoneFeatureConfiguration.CODEC));
    public static final Holder<Structure> HUNTER_CAMP_HOLDER = BuiltinRegistries.register(BuiltinRegistries.STRUCTURES, HUNTER_CAMP_KEY, new HunterCampStructure(structure(ModTags.Biomes.HAS_HUNTER_TENT, TerrainAdjustment.NONE)));
    //structures
//    public static final RegistryObject<StructureFeature<NoneFeatureConfiguration>> HUNTER_CAMP = STRUCTURE_FEATURES.register("hunter_camp", () -> {
//                StructureFeature<NoneFeatureConfiguration> feature = new HunterCampStructure(NoneFeatureConfiguration.CODEC);
//                StructureFeature.STEP.put(feature, GenerationStep.Decoration.SURFACE_STRUCTURES);
//                return feature;
//            });

    static void registerFeaturesAndStructures(IEventBus bus) {
        FEATURES.register(bus);
        STRUCTURE_TYPES.register(bus);
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {
    }

}
