package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.mixin.StructuresAccessor;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import de.teamlapen.vampirism.world.gen.VanillaStructureModifications;
import de.teamlapen.vampirism.world.gen.feature.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.feature.treedecorators.TrunkCursedVineDecorator;
import de.teamlapen.vampirism.world.gen.structure.huntercamp.HunterCampStructure;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * For new dynamic registry related things see {@link VampirismFeatures} and {@link VanillaStructureModifications}
 */
public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, REFERENCE.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, REFERENCE.MODID);

    public static final ResourceKey<Structure> HUNTER_CAMP = ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(REFERENCE.MODID, "hunter_camp"));

    public static final StructureType<HunterCampStructure> HUNTER_CAMP_TYPE = () -> HunterCampStructure.CODEC;

    public static final RegistryObject<VampireDungeonFeature> VAMPIRE_DUNGEON = FEATURES.register("vampire_dungeon", () -> new VampireDungeonFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<TreeDecoratorType<TrunkCursedVineDecorator>> trunk_cursed_vine = TREE_DECORATOR.register("trunk_cursed_vine", () -> new TreeDecoratorType<>(TrunkCursedVineDecorator.CODEC));

    static void register(IEventBus bus) {
        FEATURES.register(bus);
        TREE_DECORATOR.register(bus);
    }

    public static void registerStructureTypes(Registry<StructureType<?>> vanillaRegistry) {  //TODO 1.19 revert to registry objects
        Registry.register(vanillaRegistry, HUNTER_CAMP.location(), HUNTER_CAMP_TYPE);
    }

    public static void createStructures(BootstapContext<Structure> context) {
        HolderGetter<Biome> lookup = context.lookup(Registries.BIOME);

        context.register(ModFeatures.HUNTER_CAMP, new HunterCampStructure(StructuresAccessor.structure(lookup.getOrThrow(ModTags.Biomes.HasStructure.HUNTER_TENT), TerrainAdjustment.NONE)));
    }
}
