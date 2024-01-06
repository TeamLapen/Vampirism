package de.teamlapen.vampirism.core;


import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.gen.feature.VampireDungeonFeature;
import de.teamlapen.vampirism.world.gen.feature.treedecorators.TrunkCursedVineDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, REFERENCE.MODID);
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR = DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<Feature<?>, VampireDungeonFeature> VAMPIRE_DUNGEON = FEATURES.register("vampire_dungeon", () -> new VampireDungeonFeature(NoneFeatureConfiguration.CODEC));

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<TrunkCursedVineDecorator>> TRUNK_CURSED_VINE = TREE_DECORATOR.register("trunk_cursed_vine", () -> new TreeDecoratorType<>(TrunkCursedVineDecorator.CODEC));

    static void register(IEventBus bus) {
        FEATURES.register(bus);
        TREE_DECORATOR.register(bus);
    }
}
