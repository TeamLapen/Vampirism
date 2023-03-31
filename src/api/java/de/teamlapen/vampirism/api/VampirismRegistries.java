package de.teamlapen.vampirism.api;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Registry keys for all Vampirism registries and registry access for api usages
 */
@SuppressWarnings("unused")
public class VampirismRegistries {

    public static final ResourceKey<Registry<ISkill<?>>> SKILLS_ID = key("vampirism:skills");
    public static final ResourceKey<Registry<IAction<?>>> ACTIONS_ID = key("vampirism:actions");
    public static final ResourceKey<Registry<IEntityAction>> ENTITY_ACTIONS_ID = key("vampirism:entityactions");
    public static final ResourceKey<Registry<IMinionTask<?, ?>>> MINION_TASKS_ID = key("vampirism:miniontasks");
    public static final ResourceKey<Registry<IRefinement>> REFINEMENT_ID = key("vampirism:refinement");
    public static final ResourceKey<Registry<IRefinementSet>> REFINEMENT_SET_ID = key("vampirism:refinement_set");
    public static final ResourceKey<Registry<IOil>> OILS_ID = key("vampirism:oil");
    public static final ResourceKey<Registry<Codec<? extends TaskReward>>> TASK_REWARD_ID = key("vampirism:task_reward");
    public static final ResourceKey<Registry<Codec<? extends TaskUnlocker>>> TASK_UNLOCKER_ID = key("vampirism:task_unlocker");
    public static final ResourceKey<Registry<Codec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENT_ID = key("vampirism:task_requirement");
    public static final ResourceKey<Registry<Codec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCE_ID = key("vampirism:task_reward_instance");
    public static final ResourceKey<Registry<IConvertingHandler<?>>> CONVERTING_HANDLER_ID = key("vampirism:converting_handler");

    // data pack registries
    public static final ResourceKey<Registry<Task>> TASK_ID = key("vampirism:tasks");

    public static final Supplier<IForgeRegistry<ISkill<?>>> SKILLS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(SKILLS_ID));
    public static final Supplier<IForgeRegistry<IAction<?>>> ACTIONS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(ACTIONS_ID));
    public static final Supplier<IForgeRegistry<IEntityAction>> ENTITY_ACTIONS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(ENTITY_ACTIONS_ID));
    public static final Supplier<IForgeRegistry<IMinionTask<?, ?>>> MINION_TASKS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(MINION_TASKS_ID));
    public static final Supplier<IForgeRegistry<IRefinement>> REFINEMENTS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(REFINEMENT_ID));
    public static final Supplier<IForgeRegistry<IRefinementSet>> REFINEMENT_SETS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(REFINEMENT_SET_ID));
    public static final Supplier<IForgeRegistry<IOil>> OILS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(OILS_ID));
    public static final Supplier<IForgeRegistry<Codec<? extends TaskReward>>> TASK_REWARDS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(TASK_REWARD_ID));
    public static final Supplier<IForgeRegistry<Codec<? extends TaskUnlocker>>> TASK_UNLOCKER = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(TASK_UNLOCKER_ID));
    public static final Supplier<IForgeRegistry<Codec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENTS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(TASK_REQUIREMENT_ID));
    public static final Supplier<IForgeRegistry<Codec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCES = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(TASK_REWARD_INSTANCE_ID));
    public static final Supplier<IForgeRegistry<IConvertingHandler<?>>> CONVERTING_HANDLERS = Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(CONVERTING_HANDLER_ID));

    private static <T> @NotNull ResourceKey<Registry<T>> key(@NotNull String name) {
        return ResourceKey.createRegistryKey(new ResourceLocation(name));
    }

}