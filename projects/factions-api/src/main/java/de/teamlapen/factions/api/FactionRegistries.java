package de.teamlapen.factions.api;

import com.mojang.serialization.MapCodec;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.actions.IAction;
import de.teamlapen.factions.api.factions.refinements.IRefinement;
import de.teamlapen.factions.api.factions.refinements.IRefinementSet;
import de.teamlapen.factions.api.factions.skills.ISkill;
import de.teamlapen.factions.api.factions.skills.ISkillNode;
import de.teamlapen.factions.api.factions.skills.ISkillPointProvider;
import de.teamlapen.factions.api.factions.skills.ISkillTree;
import de.teamlapen.factions.api.factions.tasks.*;
import de.teamlapen.factions.api.registries.RegistryProvider;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.world.entities.minion.IMinionEntry;
import de.teamlapen.factions.api.world.entities.minion.IMinionTask;
import de.teamlapen.factions.api.world.entities.player.FactionPlayerBooleanSupplier;
import de.teamlapen.factions.api.world.entities.player.FactionPlayerConsumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static de.teamlapen.factions.api.registries.ApiRegistryProvider.registryKey;
import static de.teamlapen.factions.api.registries.ApiRegistryProvider.retrieveRegistry;

@SuppressWarnings("unused")
public class FactionRegistries {

    public static final RegistryProvider<IFaction<?>> FACTION = retrieveRegistry(Keys.FACTION);
    public static final RegistryProvider<ISkill<?>> SKILL = retrieveRegistry(Keys.SKILL);
    public static final RegistryProvider<IAction<?>> ACTION = retrieveRegistry(Keys.ACTION);
    public static final RegistryProvider<ISkillPointProvider> SKILL_POINT_PROVIDER = retrieveRegistry(Keys.SKILL_POINT_PROVIDER);

    public static final RegistryProvider<FactionPlayerConsumer> FACTION_PLAYER_CONSUMER = retrieveRegistry(Keys.FACTION_PLAYER_CONSUMER);
    public static final RegistryProvider<FactionPlayerBooleanSupplier> FACTION_PLAYER_BOOLEAN_SUPPLIER = retrieveRegistry(Keys.FACTION_PLAYER_BOOLEAN_SUPPLIER);

    public static final RegistryProvider<IMinionEntry<?, ?>> MINION = retrieveRegistry(Keys.MINION);
    public static final RegistryProvider<IMinionTask<?, ?>> MINION_TASK = retrieveRegistry(Keys.MINION_TASK);

    public static final RegistryProvider<IRefinement> REFINEMENT = retrieveRegistry(Keys.REFINEMENT);
    public static final RegistryProvider<IRefinementSet> REFINEMENT_SET = retrieveRegistry(Keys.REFINEMENT_SET);

    public static final RegistryProvider<MapCodec<? extends TaskReward>> TASK_REWARD = retrieveRegistry(Keys.TASK_REWARD);
    public static final RegistryProvider<MapCodec<? extends TaskUnlocker>> TASK_UNLOCKER = retrieveRegistry(Keys.TASK_UNLOCKER);
    public static final RegistryProvider<MapCodec<? extends TaskRequirement.Requirement<?>>> TASK_REQUIREMENT = retrieveRegistry(Keys.TASK_REQUIREMENT);
    public static final RegistryProvider<MapCodec<? extends ITaskRewardInstance>> TASK_REWARD_INSTANCE = retrieveRegistry(Keys.TASK_REWARD_INSTANCE);


    public static class Keys {
        public static final ResourceKey<Registry<IFaction<?>>> FACTION = registryKey(FResourceLocation.mod("faction"));
        public static final ResourceKey<Registry<ISkill<?>>> SKILL = registryKey(FResourceLocation.mod("skills"));
        public static final ResourceKey<Registry<IAction<?>>> ACTION = registryKey(FResourceLocation.mod("actions"));
        public static final ResourceKey<Registry<ISkillPointProvider>> SKILL_POINT_PROVIDER = registryKey(FResourceLocation.mod("skill_point_provider"));
        public static final ResourceKey<Registry<FactionPlayerConsumer>> FACTION_PLAYER_CONSUMER = registryKey(FResourceLocation.mod("faction_player_consumer"));
        public static final ResourceKey<Registry<FactionPlayerBooleanSupplier>> FACTION_PLAYER_BOOLEAN_SUPPLIER = registryKey(FResourceLocation.mod("faction_player_boolean_supplier"));

        public static final ResourceKey<Registry<IMinionEntry<?, ?>>> MINION = registryKey(FResourceLocation.mod("minion"));
        public static final ResourceKey<Registry<IMinionTask<?, ?>>> MINION_TASK = registryKey(FResourceLocation.mod("miniontasks"));

        public static final ResourceKey<Registry<IRefinement>> REFINEMENT = registryKey(FResourceLocation.mod("refinement"));
        public static final ResourceKey<Registry<IRefinementSet>> REFINEMENT_SET = registryKey(FResourceLocation.mod("refinement_set"));

        public static final ResourceKey<Registry<MapCodec<? extends TaskReward>>> TASK_REWARD = registryKey(FResourceLocation.mod("task_reward"));
        public static final ResourceKey<Registry<MapCodec<? extends TaskUnlocker>>> TASK_UNLOCKER = registryKey(FResourceLocation.mod("task_unlocker"));
        public static final ResourceKey<Registry<MapCodec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENT = registryKey(FResourceLocation.mod("task_requirement"));
        public static final ResourceKey<Registry<MapCodec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCE = registryKey(FResourceLocation.mod("task_reward_instance"));

        // Data-Gen registries
        public static final ResourceKey<Registry<Task>> TASK = registryKey(FResourceLocation.mod("tasks"));
        public static final ResourceKey<Registry<ISkillNode>> SKILL_NODE = registryKey(FResourceLocation.mod("skill_node"));
        public static final ResourceKey<Registry<ISkillTree>> SKILL_TREE = registryKey(FResourceLocation.mod("skill_tree"));
    }
}
