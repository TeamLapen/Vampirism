package de.teamlapen.vampirism.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.registries.RegistryProvider;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.entity.convertible.Converter;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampireVision;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.world.features.VampirismFeatures;
import de.teamlapen.vampirism.common.world.items.component.VampireBook;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static de.teamlapen.vampirism.api.VampirismRegistries.Keys.VAMPIRE_BOOK;

public class ModRegistries {

    public static final Registry<IOil> OILS = new RegistryBuilder<>(VampirismRegistries.Keys.OIL).sync(true).create();
    public static final Registry<MapCodec<? extends Converter>> ENTITY_CONVERTER = new RegistryBuilder<>(VampirismRegistries.Keys.ENTITY_CONVERTER).create();
    public static final Registry<IVampireVision> VAMPIRE_VISION = new RegistryBuilder<>(VampirismRegistries.Keys.VAMPIRE_VISION).sync(true).create();

    public static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder()
            .add(Registries.BIOME, ModBiomes::createBiomes)
            .add(Registries.CONFIGURED_FEATURE, VampirismFeatures::createConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, VampirismFeatures::createPlacedFeatures)
            .add(Registries.STRUCTURE, ModStructures::createStructures)
            .add(Registries.PROCESSOR_LIST, ModStructures::createStructureProcessorLists)
            .add(Registries.TEMPLATE_POOL, ModStructures::createStructurePoolTemplates)
            .add(Registries.STRUCTURE_SET, ModStructures::createStructureSets)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, VampirismFeatures::createBiomeModifier)
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::createDamageTypes)
            .add(FactionRegistries.Keys.TASK, ModTasks::createTasks)
            .add(FactionRegistries.Keys.SKILL_NODE, ModSkills::createSkillNodes)
            .add(FactionRegistries.Keys.SKILL_TREE, ModSkills::createSkillTrees)
            .add(VAMPIRE_BOOK, ModVampireBooks::createVampireBooks)
            .add(Registries.ENCHANTMENT, ModEnchantments::createEnchantments)
            .add(Registries.TIMELINE, ModVillage::createTimelines)
            .add(Registries.VILLAGER_TRADE, ModTrades::bootstrap)
            .add(Registries.TRADE_SET, ModTrades::bootstrapTradeSets)
            .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapDimensionTypes)
            .add(Registries.NOISE_SETTINGS, ModDimensions::bootstrapNoise)
            .add(Registries.DENSITY_FUNCTION, ModDimensions::bootstrapDensityFunctions)
            ;

    static void registerRegistries(NewRegistryEvent event) {
        event.register(OILS);
        event.register(ENTITY_CONVERTER);
        event.register(VAMPIRE_VISION);
    }

    static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(VAMPIRE_BOOK, VampireBook.CODEC, VampireBook.CODEC);
    }

    static {
        RegistryProvider.register(OILS, ENTITY_CONVERTER, VAMPIRE_VISION);
    }
}
