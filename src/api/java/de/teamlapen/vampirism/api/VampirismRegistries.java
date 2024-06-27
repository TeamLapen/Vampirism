package de.teamlapen.vampirism.api;

import com.mojang.serialization.MapCodec;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.ISkillNode;
import de.teamlapen.vampirism.api.entity.factions.ISkillTree;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntry;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.task.*;
import de.teamlapen.vampirism.api.items.oil.IOil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

import static de.teamlapen.vampirism.api.APIUtil.registryKey;
import static de.teamlapen.vampirism.api.APIUtil.supplyRegistry;

/**
 * Registry keys for all Vampirism registries and registry access for api usages
 */
@SuppressWarnings("unused")
public class VampirismRegistries {

    // for registry access in the api
    public static final Supplier<Registry<ISkill<?>>> SKILL = supplyRegistry(Keys.SKILL);
    public static final Supplier<Registry<IAction<?>>> ACTION = supplyRegistry(Keys.ACTION);
    public static final Supplier<Registry<IMinionTask<?, ?>>> MINION_TASK = supplyRegistry(Keys.MINION_TASK);
    public static final Supplier<Registry<IRefinement>> REFINEMENT = supplyRegistry(Keys.REFINEMENT);
    public static final Supplier<Registry<IRefinementSet>> REFINEMENT_SET = supplyRegistry(Keys.REFINEMENT_SET);
    public static final Supplier<Registry<IOil>> OIL = supplyRegistry(Keys.OIL);
    public static final Supplier<Registry<MapCodec<? extends TaskReward>>> TASK_REWARD = supplyRegistry(Keys.TASK_REWARD);
    public static final Supplier<Registry<MapCodec<? extends TaskUnlocker>>> TASK_UNLOCKER = supplyRegistry(Keys.TASK_UNLOCKER);
    public static final Supplier<Registry<MapCodec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENT = supplyRegistry(Keys.TASK_REQUIREMENT);
    public static final Supplier<Registry<MapCodec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCE = supplyRegistry(Keys.TASK_REWARD_INSTANCE);
    public static final Supplier<Registry<MapCodec<? extends Converter>>> ENTITY_CONVERTER = supplyRegistry(Keys.ENTITY_CONVERTER);
    public static final Supplier<Registry<IFaction<?>>> FACTION = supplyRegistry(Keys.FACTION);
    public static final Supplier<Registry<IMinionEntry<?, ?>>> MINION = supplyRegistry(Keys.MINION);

    public static class Keys {

        // builtin registries
        public static final ResourceKey<Registry<ISkill<?>>> SKILL = registryKey("skills");
        public static final ResourceKey<Registry<IAction<?>>> ACTION = registryKey("actions");
        public static final ResourceKey<Registry<IMinionTask<?, ?>>> MINION_TASK = registryKey("miniontasks");
        public static final ResourceKey<Registry<IRefinement>> REFINEMENT = registryKey("refinement");
        public static final ResourceKey<Registry<IRefinementSet>> REFINEMENT_SET = registryKey("refinement_set");
        public static final ResourceKey<Registry<IOil>> OIL = registryKey("oil");
        public static final ResourceKey<Registry<MapCodec<? extends TaskReward>>> TASK_REWARD = registryKey("task_reward");
        public static final ResourceKey<Registry<MapCodec<? extends TaskUnlocker>>> TASK_UNLOCKER = registryKey("task_unlocker");
        public static final ResourceKey<Registry<MapCodec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENT = registryKey("task_requirement");
        public static final ResourceKey<Registry<MapCodec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCE = registryKey("task_reward_instance");
        public static final ResourceKey<Registry<MapCodec<? extends Converter>>> ENTITY_CONVERTER = registryKey("converting_handler");
        public static final ResourceKey<Registry<IFaction<?>>> FACTION = registryKey("faction");
        public static final ResourceKey<Registry<IMinionEntry<?, ?>>> MINION = registryKey("minion");

        // data pack registries
        public static final ResourceKey<Registry<Task>> TASK = registryKey("tasks");
        public static final ResourceKey<Registry<ISkillNode>> SKILL_NODE = registryKey("skill_node");
        public static final ResourceKey<Registry<ISkillTree>> SKILL_TREE = registryKey("skill_tree");
    }


}