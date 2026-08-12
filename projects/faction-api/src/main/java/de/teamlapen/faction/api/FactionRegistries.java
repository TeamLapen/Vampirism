package de.teamlapen.faction.api;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.factions.skills.*;
import de.teamlapen.faction.api.factions.tasks.*;
import de.teamlapen.faction.api.registries.RegistryProvider;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntry;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerBooleanSupplier;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.faction.api.world.items.consume.IFactionFoodBehavior;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import static de.teamlapen.faction.api.registries.ApiRegistryProvider.registryKey;
import static de.teamlapen.faction.api.registries.ApiRegistryProvider.retrieveRegistry;

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

    public static final RegistryProvider<IFactionFoodBehavior> FOOD_BEHAVIOUR = retrieveRegistry(Keys.FOOD_BEHAVIOUR);


    public static class Keys {
        public static final ResourceKey<Registry<IFaction<?>>> FACTION = registryKey(FIdentifier.mod("faction"));
        public static final ResourceKey<Registry<ISkill<?>>> SKILL = registryKey(FIdentifier.mod("skills"));
        public static final ResourceKey<Registry<IAction<?>>> ACTION = registryKey(FIdentifier.mod("actions"));
        public static final ResourceKey<Registry<ISkillPointProvider>> SKILL_POINT_PROVIDER = registryKey(FIdentifier.mod("skill_point_provider"));
        public static final ResourceKey<Registry<FactionPlayerConsumer>> FACTION_PLAYER_CONSUMER = registryKey(FIdentifier.mod("faction_player_consumer"));
        public static final ResourceKey<Registry<FactionPlayerBooleanSupplier>> FACTION_PLAYER_BOOLEAN_SUPPLIER = registryKey(FIdentifier.mod("faction_player_boolean_supplier"));

        public static final ResourceKey<Registry<IMinionEntry<?, ?>>> MINION = registryKey(FIdentifier.mod("minion"));
        public static final ResourceKey<Registry<IMinionTask<?, ?>>> MINION_TASK = registryKey(FIdentifier.mod("miniontasks"));

        public static final ResourceKey<Registry<IRefinement>> REFINEMENT = registryKey(FIdentifier.mod("refinement"));
        public static final ResourceKey<Registry<IRefinementSet>> REFINEMENT_SET = registryKey(FIdentifier.mod("refinement_set"));

        public static final ResourceKey<Registry<MapCodec<? extends TaskReward>>> TASK_REWARD = registryKey(FIdentifier.mod("task_reward"));
        public static final ResourceKey<Registry<MapCodec<? extends TaskUnlocker>>> TASK_UNLOCKER = registryKey(FIdentifier.mod("task_unlocker"));
        public static final ResourceKey<Registry<MapCodec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENT = registryKey(FIdentifier.mod("task_requirement"));
        public static final ResourceKey<Registry<MapCodec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCE = registryKey(FIdentifier.mod("task_reward_instance"));

        public static final ResourceKey<Registry<IFactionFoodBehavior>> FOOD_BEHAVIOUR = registryKey(FIdentifier.mod("food_behaviour"));

        // Data-Gen registries
        public static final ResourceKey<Registry<Task>> TASK = registryKey(FIdentifier.mod("tasks"));
        public static final ResourceKey<Registry<ISkillSegment>> SKILL_SEGMENT = registryKey(FIdentifier.mod("skill_segment"));
        public static final ResourceKey<Registry<ISkillTree>> SKILL_TREE = registryKey(FIdentifier.mod("skill_tree"));
    }
}
