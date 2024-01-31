package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.datamaps.FluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.ItemBlood;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.entity.player.skills.SkillNode;
import de.teamlapen.vampirism.entity.player.skills.SkillTree;
import de.teamlapen.vampirism.api.util.SkillCallbacks;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.Collection;

import static de.teamlapen.vampirism.api.VampirismRegistries.*;

public class ModRegistries {

    static final DeferredRegister<ISkill<?>> DEFERRED_SKILLS = DeferredRegister.create(SKILLS_ID, SKILLS_ID.location().getNamespace());
    static final DeferredRegister<IAction<?>> DEFERRED_ACTIONS = DeferredRegister.create(ACTIONS_ID, ACTIONS_ID.location().getNamespace());
    static final DeferredRegister<IEntityAction> DEFERRED_ENTITY_ACTIONS = DeferredRegister.create(ENTITY_ACTIONS_ID, ENTITY_ACTIONS_ID.location().getNamespace());
    static final DeferredRegister<IMinionTask<?, ?>> DEFERRED_MINION_TASKS = DeferredRegister.create(MINION_TASKS_ID, MINION_TASKS_ID.location().getNamespace());
    static final DeferredRegister<Task> DEFERRED_TASKS = DeferredRegister.create(TASK_ID, TASK_ID.location().getNamespace());
    static final DeferredRegister<IRefinement> DEFERRED_REFINEMENTS = DeferredRegister.create(REFINEMENT_ID, REFINEMENT_ID.location().getNamespace());
    static final DeferredRegister<IRefinementSet> DEFERRED_REFINEMENT_SETS = DeferredRegister.create(REFINEMENT_SET_ID, REFINEMENT_SET_ID.location().getNamespace());
    static final DeferredRegister<IOil> DEFERRED_OILS = DeferredRegister.create(OILS_ID, OILS_ID.location().getNamespace());
    static final DeferredRegister<Codec<? extends TaskReward>> DEFERRED_TASK_REWARDS = DeferredRegister.create(TASK_REWARD_ID, TASK_REWARD_ID.location().getNamespace());
    static final DeferredRegister<Codec<? extends TaskUnlocker>> DEFERRED_TASK_UNLOCKER = DeferredRegister.create(TASK_UNLOCKER_ID, TASK_UNLOCKER_ID.location().getNamespace());
    static final DeferredRegister<Codec<? extends TaskRequirement.Requirement<?>>> DEFERRED_TASK_REQUIREMENTS = DeferredRegister.create(TASK_REQUIREMENT_ID, TASK_REQUIREMENT_ID.location().getNamespace());
    static final DeferredRegister<Codec<? extends ITaskRewardInstance>> DEFERRED_TASK_REWARD_INSTANCES = DeferredRegister.create(TASK_REWARD_INSTANCE_ID, TASK_REWARD_INSTANCE_ID.location().getNamespace());
    static final DeferredRegister<Codec<? extends Converter>> DEFERRED_ENTITY_CONVERTER = DeferredRegister.create(ENTITY_CONVERTER_ID, ENTITY_CONVERTER_ID.location().getNamespace());
    static final DeferredRegister<ISkillNode> DEFERRED_SKILL_NODES = DeferredRegister.create(SKILL_NODE_ID, SKILL_NODE_ID.location().getNamespace());
    static final DeferredRegister<ISkillTree> DEFERRED_SKILL_TREES = DeferredRegister.create(SKILL_TREE_ID, SKILL_TREE_ID.location().getNamespace());


    public static final Registry<ISkill<?>> SKILLS = DEFERRED_SKILLS.makeRegistry(builder -> builder.callback(new SkillCallbacks()));
    public static final Registry<IAction<?>> ACTIONS = DEFERRED_ACTIONS.makeRegistry(builder -> {});
    public static final Registry<IEntityAction> ENTITY_ACTIONS = DEFERRED_ENTITY_ACTIONS.makeRegistry(builder -> {});
    public static final Registry<IMinionTask<?, ?>> MINION_TASKS = DEFERRED_MINION_TASKS.makeRegistry(builder -> {});
    public static final Registry<IRefinement> REFINEMENTS = DEFERRED_REFINEMENTS.makeRegistry(builder -> {});
    public static final Registry<IRefinementSet> REFINEMENT_SETS = DEFERRED_REFINEMENT_SETS.makeRegistry(builder -> {});
    public static final Registry<IOil> OILS = DEFERRED_OILS.makeRegistry(builder -> {});
    public static final Registry<Codec<? extends TaskReward>> TASK_REWARDS = DEFERRED_TASK_REWARDS.makeRegistry(builder -> {});
    public static final Registry<Codec<? extends TaskUnlocker>> TASK_UNLOCKER = DEFERRED_TASK_UNLOCKER.makeRegistry(builder -> {});
    public static final Registry<Codec<? extends TaskRequirement.Requirement<?>>> TASK_REQUIREMENTS = DEFERRED_TASK_REQUIREMENTS.makeRegistry(builder -> {});
    public static final Registry<Codec<? extends ITaskRewardInstance>> TASK_REWARD_INSTANCES = DEFERRED_TASK_REWARD_INSTANCES.makeRegistry(builder -> {});
    public static final Registry<Codec<? extends Converter>> ENTIITY_CONVERTER = DEFERRED_ENTITY_CONVERTER.makeRegistry(builder -> {});
    public static final DataMapType<Item, ItemBlood> ITEM_BLOOD = DataMapType.builder(VampirismRegistries.ITEM_BLOOD_VALUE_ID, Registries.ITEM, ItemBlood.CODEC).synced(ItemBlood.NETWORK_CODEC, true).build();
    public static final DataMapType<Fluid, FluidBloodConversion> FLUID_BLOOD_CONVERSION = DataMapType.builder(VampirismRegistries.FLUID_BLOOD_CONVERSION_ID, Registries.FLUID, FluidBloodConversion.CODEC).synced(FluidBloodConversion.NETWORK_CODEC, true).build();

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
            .add(TASK_ID, ModTasks::createTasks)
            .add(SKILL_NODE_ID, ModSkills::createSkillNodes)
            .add(SKILL_TREE_ID, ModSkills::createSkillTrees)
            ;

    static void init(IEventBus bus) {
        DEFERRED_SKILLS.register(bus);
        DEFERRED_ACTIONS.register(bus);
        DEFERRED_ENTITY_ACTIONS.register(bus);
        DEFERRED_MINION_TASKS.register(bus);
        DEFERRED_TASKS.register(bus);
        DEFERRED_REFINEMENTS.register(bus);
        DEFERRED_REFINEMENT_SETS.register(bus);
        DEFERRED_OILS.register(bus);
        DEFERRED_TASK_REWARDS.register(bus);
        DEFERRED_TASK_UNLOCKER.register(bus);
        DEFERRED_TASK_REQUIREMENTS.register(bus);
        DEFERRED_TASK_REWARD_INSTANCES.register(bus);
        DEFERRED_ENTITY_CONVERTER.register(bus);
        DEFERRED_SKILL_NODES.register(bus);
        DEFERRED_SKILL_TREES.register(bus);
    }

    static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(TASK_ID, Task.CODEC, Task.CODEC);
        event.dataPackRegistry(SKILL_TREE_ID, SkillTree.CODEC, SkillTree.CODEC);
        event.dataPackRegistry(SKILL_NODE_ID, SkillNode.CODEC, SkillNode.CODEC);
    }

    static void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(ITEM_BLOOD);
        event.register(FLUID_BLOOD_CONVERSION);
    }

    public static Collection<DeferredHolder<IAction<?>, ? extends IAction<?>>> allActions() {
        return DEFERRED_ACTIONS.getEntries();
    }

}
