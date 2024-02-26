package de.teamlapen.vampirism.api;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Registry keys for all Vampirism registries and registry access for api usages
 */
@SuppressWarnings({"unused", "unchecked", "rawtypes"})
public class VampirismRegistries {

    // for registry access in the api
    public static final Supplier<Registry<ISkill<?>>> SKILL = Suppliers.memoize(() -> (Registry<ISkill<?>>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.SKILL));
    public static final Supplier<Registry<IAction<?>>> ACTION = Suppliers.memoize(() -> (Registry<IAction<?>>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.ACTION));
    public static final Supplier<Registry<IEntityAction>> ENTITY_ACTION = Suppliers.memoize(() -> (Registry<IEntityAction>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.ENTITY_ACTION));
    public static final Supplier<Registry<IMinionTask<?, ?>>> MINION_TASK = Suppliers.memoize(() -> (Registry<IMinionTask<?, ?>>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.MINION_TASK));
    public static final Supplier<Registry<IRefinement>> REFINEMENT = Suppliers.memoize(() -> (Registry<IRefinement>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.REFINEMENT));
    public static final Supplier<Registry<IRefinementSet>> REFINEMENT_SET = Suppliers.memoize(() -> (Registry<IRefinementSet>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.REFINEMENT_SET));
    public static final Supplier<Registry<IOil>> OIL = Suppliers.memoize(() -> (Registry<IOil>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.OIL));
    public static final Supplier<Registry<Codec<? extends TaskReward>>> TASK_REWARD = Suppliers.memoize(() -> (Registry<Codec<? extends TaskReward>>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.TASK_REWARD));
    public static final Supplier<Registry<Codec<? extends TaskUnlocker>>> TASK_UNLOCKER = Suppliers.memoize(() -> (Registry<Codec<? extends TaskUnlocker>>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.TASK_UNLOCKER));
    public static final Supplier<Registry<Codec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENT = Suppliers.memoize(() -> (Registry<Codec<? extends TaskRequirement.Requirement<?>>>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.TASK_REQUIREMENT));
    public static final Supplier<Registry<Codec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCE = Suppliers.memoize(() -> (Registry<Codec<? extends ITaskRewardInstance>>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.TASK_REWARD_INSTANCE));
    public static final Supplier<Registry<Codec<? extends Converter>>> ENTITY_CONVERTER = Suppliers.memoize(() -> (Registry<Codec<? extends Converter>>) BuiltInRegistries.REGISTRY.get((ResourceKey) Keys.ENTITY_CONVERTER));

    public static class Keys {

        // builtin registries
        public static final ResourceKey<Registry<ISkill<?>>> SKILL = key("skills");
        public static final ResourceKey<Registry<IAction<?>>> ACTION = key("actions");
        public static final ResourceKey<Registry<IEntityAction>> ENTITY_ACTION = key("entityactions");
        public static final ResourceKey<Registry<IMinionTask<?, ?>>> MINION_TASK = key("miniontasks");
        public static final ResourceKey<Registry<IRefinement>> REFINEMENT = key("refinement");
        public static final ResourceKey<Registry<IRefinementSet>> REFINEMENT_SET = key("refinement_set");
        public static final ResourceKey<Registry<IOil>> OIL = key("oil");
        public static final ResourceKey<Registry<Codec<? extends TaskReward>>> TASK_REWARD = key("task_reward");
        public static final ResourceKey<Registry<Codec<? extends TaskUnlocker>>> TASK_UNLOCKER = key("task_unlocker");
        public static final ResourceKey<Registry<Codec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENT = key("task_requirement");
        public static final ResourceKey<Registry<Codec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCE = key("task_reward_instance");
        public static final ResourceKey<Registry<Codec<? extends Converter>>> ENTITY_CONVERTER = key("converting_handler");

        // data pack registries
        public static final ResourceKey<Registry<Task>> TASK = key("tasks");
        public static final ResourceKey<Registry<ISkillNode>> SKILL_NODE = key("skill_node");
        public static final ResourceKey<Registry<ISkillTree>> SKILL_TREE = key("skill_tree");

        private static <T> @NotNull ResourceKey<Registry<T>> key(@NotNull String name) {
            return ResourceKey.createRegistryKey(new ResourceLocation(VReference.MODID, name));
        }
    }


}