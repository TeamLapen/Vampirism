package de.teamlapen.vampirism.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntry;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.api.util.SkillCallbacks;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.entity.player.skills.SkillTree;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import static de.teamlapen.vampirism.api.VampirismRegistries.Keys.*;

public class ModRegistries {

    public static final Registry<ISkill<?>> SKILLS = new RegistryBuilder<>(VampirismRegistries.Keys.SKILL).callback(new SkillCallbacks()).sync(true).create();
    public static final Registry<IAction<?>> ACTIONS = new RegistryBuilder<>(VampirismRegistries.Keys.ACTION).sync(true).create();
    public static final Registry<IMinionTask<?, ?>> MINION_TASKS = new RegistryBuilder<>(VampirismRegistries.Keys.MINION_TASK).sync(true).create();
    public static final Registry<IRefinement> REFINEMENTS = new RegistryBuilder<>(VampirismRegistries.Keys.REFINEMENT).sync(true).create();
    public static final Registry<IRefinementSet> REFINEMENT_SETS = new RegistryBuilder<>(VampirismRegistries.Keys.REFINEMENT_SET).sync(true).create();
    public static final Registry<IOil> OILS = new RegistryBuilder<>(VampirismRegistries.Keys.OIL).sync(true).create();
    public static final Registry<MapCodec<? extends TaskReward>> TASK_REWARDS = new RegistryBuilder<>(VampirismRegistries.Keys.TASK_REWARD).create();
    public static final Registry<MapCodec<? extends TaskUnlocker>> TASK_UNLOCKER = new RegistryBuilder<>(VampirismRegistries.Keys.TASK_UNLOCKER).create();
    public static final Registry<MapCodec<? extends TaskRequirement.Requirement<?>>> TASK_REQUIREMENTS = new RegistryBuilder<>(VampirismRegistries.Keys.TASK_REQUIREMENT).create();
    public static final Registry<MapCodec<? extends ITaskRewardInstance>> TASK_REWARD_INSTANCES = new RegistryBuilder<>(VampirismRegistries.Keys.TASK_REWARD_INSTANCE).create();
    public static final Registry<MapCodec<? extends Converter>> ENTITY_CONVERTER = new RegistryBuilder<>(VampirismRegistries.Keys.ENTITY_CONVERTER).create();
    public static final Registry<IFaction<?>> FACTIONS = new RegistryBuilder<>(VampirismRegistries.Keys.FACTION).sync(true).defaultKey(VampirismFactions.NEUTRAL.getRawKey()).create();
    public static final Registry<IMinionEntry<?, ?>> MINIONS = new RegistryBuilder<>(VampirismRegistries.Keys.MINION).callback(((FactionRegistry) VampirismAPI.factionRegistry()).getMinionCallback()).sync(true).create();

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
            .add(TASK, ModTasks::createTasks)
            .add(SKILL_NODE, ModSkills::createSkillNodes)
            .add(SKILL_TREE, ModSkills::createSkillTrees)
            .add(Registries.ENCHANTMENT, ModEnchantments::createEnchantments)
            .add(Registries.DIMENSION_TYPE, ModDimensions::bootstrapTypes)
            .add(Registries.LEVEL_STEM, ModDimensions::bootstrapLevels);

    static void registerRegistries(NewRegistryEvent event) {
        event.register(SKILLS);
        event.register(ACTIONS);
        event.register(MINION_TASKS);
        event.register(REFINEMENTS);
        event.register(REFINEMENT_SETS);
        event.register(OILS);
        event.register(TASK_REWARDS);
        event.register(TASK_UNLOCKER);
        event.register(TASK_REQUIREMENTS);
        event.register(TASK_REWARD_INSTANCES);
        event.register(ENTITY_CONVERTER);
        event.register(FACTIONS);
        event.register(MINIONS);
    }

    static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(TASK, Task.CODEC, Task.CODEC);
        event.dataPackRegistry(SKILL_TREE, SkillTree.CODEC, SkillTree.CODEC);
        event.dataPackRegistry(SKILL_NODE, SkillNode.CODEC, SkillNode.CODEC);
    }
}
