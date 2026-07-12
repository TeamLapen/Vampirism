package de.teamlapen.vampirism.common.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.features.GiantRootConfiguration;
import de.teamlapen.vampirism.common.world.features.GiantRootFeature;
import de.teamlapen.vampirism.common.world.features.VampireDungeonFeature;
import de.teamlapen.vampirism.common.world.features.decorators.TallGrassNearTreeDecorator;
import de.teamlapen.vampirism.common.world.features.decorators.TrunkCursedVineDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, REFERENCE.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<Feature<?>, VampireDungeonFeature> VAMPIRE_DUNGEON = FEATURES.register("vampire_dungeon", () -> new VampireDungeonFeature(NoneFeatureConfiguration.CODEC));
    public static final DeferredHolder<Feature<?>, GiantRootFeature> GIANT_ROOT = FEATURES.register("giant_root", () -> new GiantRootFeature(GiantRootConfiguration.CODEC));

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<TrunkCursedVineDecorator>> TRUNK_CURSED_VINE = TREE_DECORATORS.register("trunk_cursed_vine", () -> new TreeDecoratorType<>(TrunkCursedVineDecorator.CODEC));
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<TallGrassNearTreeDecorator>> TALL_GRASS_NEAR_TREE = TREE_DECORATORS.register("tall_grass_near_tree", () -> new TreeDecoratorType<>(TallGrassNearTreeDecorator.CODEC));

    static void register(IEventBus bus) {
        FEATURES.register(bus);
        TREE_DECORATORS.register(bus);
    }
}
