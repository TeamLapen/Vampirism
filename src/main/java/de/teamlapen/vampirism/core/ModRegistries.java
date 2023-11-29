package de.teamlapen.vampirism.core;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.world.gen.VampirismFeatures;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

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

    public static final Supplier<IForgeRegistry<ISkill<?>>> SKILLS = DEFERRED_SKILLS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<IAction<?>>> ACTIONS = DEFERRED_ACTIONS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<IEntityAction>> ENTITY_ACTIONS = DEFERRED_ENTITY_ACTIONS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<IMinionTask<?, ?>>> MINION_TASKS = DEFERRED_MINION_TASKS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<IRefinement>> REFINEMENTS = DEFERRED_REFINEMENTS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<IRefinementSet>> REFINEMENT_SETS = DEFERRED_REFINEMENT_SETS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<IOil>> OILS = DEFERRED_OILS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Codec<? extends TaskReward>>> TASK_REWARDS = DEFERRED_TASK_REWARDS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Codec<? extends TaskUnlocker>>> TASK_UNLOCKER = DEFERRED_TASK_UNLOCKER.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Codec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENTS = DEFERRED_TASK_REQUIREMENTS.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Codec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCES = DEFERRED_TASK_REWARD_INSTANCES.makeRegistry(RegistryBuilder::new);
    public static final Supplier<IForgeRegistry<Codec<? extends Converter>>> ENTIITY_CONVERTER = DEFERRED_ENTITY_CONVERTER.makeRegistry(RegistryBuilder::new);

    public static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder()
            .add(Registries.BIOME, ModBiomes::createBiomes)
            .add(Registries.CONFIGURED_FEATURE, VampirismFeatures::createConfiguredFeatures)
            .add(Registries.PLACED_FEATURE, VampirismFeatures::createPlacedFeatures)
            .add(Registries.STRUCTURE, ModStructures::createStructures)
            .add(Registries.PROCESSOR_LIST, ModStructures::createStructureProcessorLists)
            .add(Registries.TEMPLATE_POOL, ModStructures::createStructurePoolTemplates)
            .add(Registries.STRUCTURE_SET, ModStructures::createStructureSets)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, VampirismFeatures::createBiomeModifier)
            .add(Registries.DAMAGE_TYPE, ModDamageTypes::createDamageTypes)
            .add(TASK_ID, ModTasks::createTasks)
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
    }

    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(TASK_ID, Task.CODEC, Task.CODEC);
    }
}
