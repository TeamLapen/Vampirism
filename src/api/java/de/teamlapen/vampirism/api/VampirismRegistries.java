package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class VampirismRegistries {

    public static final ResourceKey<Registry<ISkill<?>>> SKILLS_ID = key("vampirism:skills");
    public static final ResourceKey<Registry<IAction<?>>> ACTIONS_ID = key("vampirism:actions");
    public static final ResourceKey<Registry<IEntityAction>> ENTITY_ACTIONS_ID = key("vampirism:entityactions");
    public static final ResourceKey<Registry<IMinionTask<?, ?>>> MINION_TASKS_ID = key("vampirism:miniontasks");
    public static final ResourceKey<Registry<Task>> TASK_ID = key("vampirism:tasks");
    public static final ResourceKey<Registry<IRefinement>> REFINEMENT_ID = key("vampirism:refinement");
    public static final ResourceKey<Registry<IRefinementSet>> REFINEMENT_SET_ID = key("vampirism:refinement_set");

    static DeferredRegister<ISkill<?>> DEFERRED_SKILLS = DeferredRegister.create(SKILLS_ID, SKILLS_ID.location().getNamespace());
    static DeferredRegister<IAction<?>> DEFERRED_ACTIONS = DeferredRegister.create(ACTIONS_ID, ACTIONS_ID.location().getNamespace());
    static DeferredRegister<IEntityAction> DEFERRED_ENTITY_ACTIONS = DeferredRegister.create(ENTITY_ACTIONS_ID, ENTITY_ACTIONS_ID.location().getNamespace());
    static DeferredRegister<IMinionTask<?, ?>> DEFERRED_MINION_TASKS = DeferredRegister.create(MINION_TASKS_ID, MINION_TASKS_ID.location().getNamespace());
    static DeferredRegister<Task> DEFERRED_TASKS = DeferredRegister.create(TASK_ID, TASK_ID.location().getNamespace());
    static DeferredRegister<IRefinement> DEFERRED_REFINEMENTS = DeferredRegister.create(REFINEMENT_ID, REFINEMENT_ID.location().getNamespace());
    static DeferredRegister<IRefinementSet> DEFERRED_REFINEMENT_SETS = DeferredRegister.create(REFINEMENT_SET_ID, REFINEMENT_SET_ID.location().getNamespace());

    public static Supplier<IForgeRegistry<ISkill<?>>> SKILLS = DEFERRED_SKILLS.makeRegistry(RegistryBuilder::new);
    public static Supplier<IForgeRegistry<IAction<?>>> ACTIONS = DEFERRED_ACTIONS.makeRegistry(RegistryBuilder::new);
    public static Supplier<IForgeRegistry<IEntityAction>> ENTITY_ACTIONS = DEFERRED_ENTITY_ACTIONS.makeRegistry(RegistryBuilder::new);
    public static Supplier<IForgeRegistry<IMinionTask<?, ?>>> MINION_TASKS = DEFERRED_MINION_TASKS.makeRegistry(RegistryBuilder::new);
    public static Supplier<IForgeRegistry<Task>> TASKS = DEFERRED_TASKS.makeRegistry(RegistryBuilder::new);
    public static Supplier<IForgeRegistry<IRefinement>> REFINEMENTS = DEFERRED_REFINEMENTS.makeRegistry(RegistryBuilder::new);
    public static Supplier<IForgeRegistry<IRefinementSet>> REFINEMENT_SETS = DEFERRED_REFINEMENT_SETS.makeRegistry(RegistryBuilder::new);

    public static void init(IEventBus bus) {
        DEFERRED_SKILLS.register(bus);
        DEFERRED_ACTIONS.register(bus);
        DEFERRED_ENTITY_ACTIONS.register(bus);
        DEFERRED_MINION_TASKS.register(bus);
        DEFERRED_TASKS.register(bus);
        DEFERRED_REFINEMENTS.register(bus);
        DEFERRED_REFINEMENT_SETS.register(bus);
    }

    private static <T> ResourceKey<Registry<T>> key(String name)
    {
        return ResourceKey.createRegistryKey(new ResourceLocation(name));
    }

}