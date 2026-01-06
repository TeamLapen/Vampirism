package de.teamlapen.faction.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.Factions;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.refinements.IRefinement;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillPointProvider;
import de.teamlapen.faction.api.factions.tasks.*;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntry;
import de.teamlapen.faction.api.world.entities.minion.IMinionTask;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerBooleanSupplier;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.faction.common.factions.minions.MinionEntryCallbacks;
import de.teamlapen.faction.common.factions.skills.SkillCallbacks;
import de.teamlapen.faction.common.factions.skills.SkillNode;
import de.teamlapen.faction.common.factions.skills.SkillTree;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ModRegistries {
    public static final Registry<ISkill<?>> SKILLS = new RegistryBuilder<>(FactionRegistries.Keys.SKILL).callback(new SkillCallbacks()).sync(true).create();
    public static final Registry<IAction<?>> ACTIONS = new RegistryBuilder<>(FactionRegistries.Keys.ACTION).sync(true).create();
    public static final Registry<IMinionTask<?, ?>> MINION_TASKS = new RegistryBuilder<>(FactionRegistries.Keys.MINION_TASK).sync(true).create();
    public static final Registry<IRefinement> REFINEMENTS = new RegistryBuilder<>(FactionRegistries.Keys.REFINEMENT).sync(true).create();
    public static final Registry<IRefinementSet> REFINEMENT_SETS = new RegistryBuilder<>(FactionRegistries.Keys.REFINEMENT_SET).sync(true).create();
    public static final Registry<FactionPlayerConsumer> FACTION_PLAYER_CONSUMERS = new RegistryBuilder<>(FactionRegistries.Keys.FACTION_PLAYER_CONSUMER).sync(true).create();
    public static final Registry<FactionPlayerBooleanSupplier> FACTION_PLAYER_BOOLEAN_SUPPLIERS = new RegistryBuilder<>(FactionRegistries.Keys.FACTION_PLAYER_BOOLEAN_SUPPLIER).sync(true).create();
    public static final Registry<ISkillPointProvider> SKILL_POINT_PROVIDERS = new RegistryBuilder<>(FactionRegistries.Keys.SKILL_POINT_PROVIDER).sync(true).create();

    public static final Registry<MapCodec<? extends TaskReward>> TASK_REWARDS = new RegistryBuilder<>(FactionRegistries.Keys.TASK_REWARD).create();
    public static final Registry<MapCodec<? extends TaskUnlocker>> TASK_UNLOCKER = new RegistryBuilder<>(FactionRegistries.Keys.TASK_UNLOCKER).create();
    public static final Registry<MapCodec<? extends TaskRequirement.Requirement<?>>> TASK_REQUIREMENTS = new RegistryBuilder<>(FactionRegistries.Keys.TASK_REQUIREMENT).create();
    public static final Registry<MapCodec<? extends ITaskRewardInstance>> TASK_REWARD_INSTANCES = new RegistryBuilder<>(FactionRegistries.Keys.TASK_REWARD_INSTANCE).create();

    public static final Registry<IFaction<?>> FACTIONS = new RegistryBuilder<>(FactionRegistries.Keys.FACTION).sync(true).defaultKey(Factions.NEUTRAL.getRawKey()).create();
    public static final Registry<IMinionEntry<?, ?>> MINIONS = new RegistryBuilder<>(FactionRegistries.Keys.MINION).callback(new MinionEntryCallbacks()).sync(true).create();

    public static final RegistrySetBuilder DATA_BUILDER = new RegistrySetBuilder()
            .add(FactionRegistries.Keys.TASK, FactionTasks::createTasks)
            .add(FactionRegistries.Keys.SKILL_NODE, FactionSkillNodes::createSkillNodes)
            .add(FactionRegistries.Keys.SKILL_TREE, FactionSkillTrees::createSkillTrees)
            .add(Registries.DAMAGE_TYPE, FactionDamageTypes::createDamageTypes);

    static void registerRegistries(NewRegistryEvent event) {
        event.register(SKILLS);
        event.register(ACTIONS);
        event.register(MINION_TASKS);
        event.register(REFINEMENTS);
        event.register(REFINEMENT_SETS);
        event.register(TASK_REWARDS);
        event.register(TASK_UNLOCKER);
        event.register(TASK_REQUIREMENTS);
        event.register(TASK_REWARD_INSTANCES);
        event.register(FACTIONS);
        event.register(MINIONS);
        event.register(FACTION_PLAYER_CONSUMERS);
        event.register(SKILL_POINT_PROVIDERS);
        event.register(FACTION_PLAYER_BOOLEAN_SUPPLIERS);
    }

    static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(FactionRegistries.Keys.TASK, Task.CODEC, Task.CODEC);
        event.dataPackRegistry(FactionRegistries.Keys.SKILL_TREE, SkillTree.CODEC, SkillTree.CODEC);
        event.dataPackRegistry(FactionRegistries.Keys.SKILL_NODE, SkillNode.CODEC, SkillNode.CODEC);
    }
}
