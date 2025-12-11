package de.teamlapen.factions.common.core;

import com.mojang.serialization.MapCodec;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.tasks.*;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.tasks.requirements.*;
import de.teamlapen.factions.common.tasks.reward.*;
import de.teamlapen.factions.common.tasks.unlock.LordLvlUnlocker;
import de.teamlapen.factions.common.tasks.unlock.LvlUnlocker;
import de.teamlapen.factions.common.tasks.unlock.ParentUnlocker;
import net.minecraft.data.worldgen.BootstrapContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionTasks {

    public static final DeferredRegister<MapCodec<? extends TaskUnlocker>> TASK_UNLOCKER = DeferredRegister.create(FactionRegistries.Keys.TASK_UNLOCKER, REFERENCE.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends TaskRequirement.Requirement<?>>> TASK_REQUIREMENTS = DeferredRegister.create(FactionRegistries.Keys.TASK_REQUIREMENT, REFERENCE.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends TaskReward>> TASK_REWARDS = DeferredRegister.create(FactionRegistries.Keys.TASK_REWARD, REFERENCE.MOD_ID);
    public static final DeferredRegister<MapCodec<? extends ITaskRewardInstance>> TASK_REWARD_INSTANCES = DeferredRegister.create(FactionRegistries.Keys.TASK_REWARD_INSTANCE, REFERENCE.MOD_ID);


    public static final DeferredHolder<MapCodec<? extends TaskUnlocker>, MapCodec<? extends TaskUnlocker>> LORD_LEVEL_UNLOCKER = TASK_UNLOCKER.register("lord_level", () -> LordLvlUnlocker.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskUnlocker>, MapCodec<? extends TaskUnlocker>> LEVEL_UNLOCKER = TASK_UNLOCKER.register("level", () -> LvlUnlocker.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskUnlocker>, MapCodec<? extends TaskUnlocker>> PARENT_UNLOCKER = TASK_UNLOCKER.register("parent", () -> ParentUnlocker.CODEC);

    public static final DeferredHolder<MapCodec<? extends TaskReward>, MapCodec<ItemReward>> ITEM_REWARD = TASK_REWARDS.register("item", () -> ItemReward.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskReward>, MapCodec<LordLevelReward>> LORD_LEVEL_REWARD = TASK_REWARDS.register("lord_level", () -> LordLevelReward.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskReward>, MapCodec<RefinementItemReward>> REFINEMENT_REWARD = TASK_REWARDS.register("refinement", () -> RefinementItemReward.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskReward>, MapCodec<ConsumerReward>> CONSUMER = TASK_REWARDS.register("consumer", () -> ConsumerReward.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskReward>, MapCodec<MapReward>> MAP_REWARD = TASK_REWARDS.register("map", () -> MapReward.CODEC);

    public static final DeferredHolder<MapCodec<? extends ITaskRewardInstance>, MapCodec<ItemReward.Instance>> ITEM_REWARD_INSTANCE = TASK_REWARD_INSTANCES.register("item", () -> ItemReward.Instance.CODEC);
    public static final DeferredHolder<MapCodec<? extends ITaskRewardInstance>, MapCodec<LordLevelReward>> LORD_LEVEL_REWARD_INSTANCE = TASK_REWARD_INSTANCES.register("lord_level", () -> LordLevelReward.CODEC);
    public static final DeferredHolder<MapCodec<? extends ITaskRewardInstance>, MapCodec<ConsumerReward>> CONSUMER_INSTANCE = TASK_REWARD_INSTANCES.register("consumer", () -> ConsumerReward.CODEC);
    public static final DeferredHolder<MapCodec<? extends ITaskRewardInstance>, MapCodec<MapReward.Instance>> MAP_REWARD_INSTANCE = TASK_REWARD_INSTANCES.register("map", () -> MapReward.Instance.CODEC);

    public static final DeferredHolder<MapCodec<? extends TaskRequirement.Requirement<?>>, MapCodec<? extends TaskRequirement.Requirement<?>>> BOOLEAN_REQUIREMENT = TASK_REQUIREMENTS.register("boolean", () -> BooleanRequirement.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskRequirement.Requirement<?>>, MapCodec<? extends TaskRequirement.Requirement<?>>> ENTITY_REQUIREMENT = TASK_REQUIREMENTS.register("entity", () -> EntityRequirement.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskRequirement.Requirement<?>>, MapCodec<? extends TaskRequirement.Requirement<?>>> ENTITY_TYPE_REQUIREMENT = TASK_REQUIREMENTS.register("entity_type", () -> EntityTypeRequirement.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskRequirement.Requirement<?>>, MapCodec<? extends TaskRequirement.Requirement<?>>> ITEM_REQUIREMENT = TASK_REQUIREMENTS.register("item", () -> ItemRequirement.CODEC);
    public static final DeferredHolder<MapCodec<? extends TaskRequirement.Requirement<?>>, MapCodec<? extends TaskRequirement.Requirement<?>>> STAT_REQUIREMENT = TASK_REQUIREMENTS.register("stat", () -> StatRequirement.CODEC);

    static void register(IEventBus bus) {
        TASK_UNLOCKER.register(bus);
        TASK_REWARDS.register(bus);
        TASK_REQUIREMENTS.register(bus);
        TASK_REWARD_INSTANCES.register(bus);
    }

    static void createTasks(BootstrapContext<Task> context) {

    }
}
