package de.teamlapen.vampirism.api;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.datamaps.IConverterEntry;
import de.teamlapen.vampirism.api.datamaps.IEntityBlood;
import de.teamlapen.vampirism.api.datamaps.IFluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.IItemBlood;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.RegistryManager;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Registry keys for all Vampirism registries and registry access for api usages
 */
@SuppressWarnings({"unused", "unchecked", "rawtypes"})
public class VampirismRegistries {

    // builtin registries registries
    public static final ResourceKey<Registry<ISkill<?>>> SKILLS_ID = key("skills");
    public static final ResourceKey<Registry<IAction<?>>> ACTIONS_ID = key("actions");
    public static final ResourceKey<Registry<IEntityAction>> ENTITY_ACTIONS_ID = key("entityactions");
    public static final ResourceKey<Registry<IMinionTask<?, ?>>> MINION_TASKS_ID = key("miniontasks");
    public static final ResourceKey<Registry<IRefinement>> REFINEMENT_ID = key("refinement");
    public static final ResourceKey<Registry<IRefinementSet>> REFINEMENT_SET_ID = key("refinement_set");
    public static final ResourceKey<Registry<IOil>> OILS_ID = key("oil");
    public static final ResourceKey<Registry<Codec<? extends TaskReward>>> TASK_REWARD_ID = key("task_reward");
    public static final ResourceKey<Registry<Codec<? extends TaskUnlocker>>> TASK_UNLOCKER_ID = key("task_unlocker");
    public static final ResourceKey<Registry<Codec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENT_ID = key("task_requirement");
    public static final ResourceKey<Registry<Codec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCE_ID = key("task_reward_instance");
    public static final ResourceKey<Registry<Codec<? extends Converter>>> ENTITY_CONVERTER_ID = key("converting_handler");

    // data pack registries
    public static final ResourceKey<Registry<Task>> TASK_ID = key("tasks");
    public static final ResourceKey<Registry<ISkillNode>> SKILL_NODE_ID = key("skill_node");
    public static final ResourceKey<Registry<ISkillTree>> SKILL_TREE_ID = key("skill_tree");

    // data maps
    public static final ResourceLocation ITEM_BLOOD_MAP_ID = new ResourceLocation(VReference.MODID, "item_blood");
    public static final ResourceLocation ENTITY_BLOOD_MAP_ID = new ResourceLocation(VReference.MODID, "entity_blood");
    public static final ResourceLocation FLUID_BLOOD_CONVERSION_MAP_ID = new ResourceLocation(VReference.MODID, "fluid_blood_conversion");
    public static final ResourceLocation ENTITY_CONVERTER_MAP_ID = new ResourceLocation(VReference.MODID, "entity_converter");

    // for registry access in the api
    public static final Supplier<Registry<ISkill<?>>> SKILLS = Suppliers.memoize(() -> (Registry<ISkill<?>>) BuiltInRegistries.REGISTRY.get((ResourceKey) SKILLS_ID));
    public static final Supplier<Registry<IAction<?>>> ACTIONS = Suppliers.memoize(() -> (Registry<IAction<?>>) BuiltInRegistries.REGISTRY.get((ResourceKey) ACTIONS_ID));
    public static final Supplier<Registry<IEntityAction>> ENTITY_ACTIONS = Suppliers.memoize(() -> (Registry<IEntityAction>) BuiltInRegistries.REGISTRY.get((ResourceKey) ENTITY_ACTIONS_ID));
    public static final Supplier<Registry<IMinionTask<?, ?>>> MINION_TASKS = Suppliers.memoize(() -> (Registry<IMinionTask<?, ?>>) BuiltInRegistries.REGISTRY.get((ResourceKey) MINION_TASKS_ID));
    public static final Supplier<Registry<IRefinement>> REFINEMENTS = Suppliers.memoize(() -> (Registry<IRefinement>) BuiltInRegistries.REGISTRY.get((ResourceKey) REFINEMENT_ID));
    public static final Supplier<Registry<IRefinementSet>> REFINEMENT_SETS = Suppliers.memoize(() -> (Registry<IRefinementSet>) BuiltInRegistries.REGISTRY.get((ResourceKey) REFINEMENT_SET_ID));
    public static final Supplier<Registry<IOil>> OILS = Suppliers.memoize(() -> (Registry<IOil>) BuiltInRegistries.REGISTRY.get((ResourceKey) OILS_ID));
    public static final Supplier<Registry<Codec<? extends TaskReward>>> TASK_REWARDS = Suppliers.memoize(() -> (Registry<Codec<? extends TaskReward>>) BuiltInRegistries.REGISTRY.get((ResourceKey) TASK_REWARD_ID));
    public static final Supplier<Registry<Codec<? extends TaskUnlocker>>> TASK_UNLOCKER = Suppliers.memoize(() -> (Registry<Codec<? extends TaskUnlocker>>) BuiltInRegistries.REGISTRY.get((ResourceKey) TASK_UNLOCKER_ID));
    public static final Supplier<Registry<Codec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENTS = Suppliers.memoize(() -> (Registry<Codec<? extends TaskRequirement.Requirement<?>>>) BuiltInRegistries.REGISTRY.get((ResourceKey) TASK_REQUIREMENT_ID));
    public static final Supplier<Registry<Codec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCES = Suppliers.memoize(() -> (Registry<Codec<? extends ITaskRewardInstance>>) BuiltInRegistries.REGISTRY.get((ResourceKey) TASK_REWARD_INSTANCE_ID));
    public static final Supplier<Registry<Codec<? extends Converter>>> ENTITY_CONVERTER = Suppliers.memoize(() -> (Registry<Codec<? extends Converter>>) BuiltInRegistries.REGISTRY.get((ResourceKey) ENTITY_CONVERTER_ID));

    // for data maps access in the api
    public static final Supplier<DataMapType<Item, IItemBlood>> ITEM_BLOOD_MAP = Suppliers.memoize(() -> (DataMapType<Item, IItemBlood>) RegistryManager.getDataMap(Registries.ITEM, ITEM_BLOOD_MAP_ID));
    public static final Supplier<DataMapType<EntityType<?>, IEntityBlood>> ENTITY_BLOOD_MAP = Suppliers.memoize(() -> (DataMapType<EntityType<?>, IEntityBlood>) RegistryManager.getDataMap(Registries.ENTITY_TYPE, ENTITY_BLOOD_MAP_ID));
    public static final Supplier<DataMapType<Fluid, IFluidBloodConversion>> FLUID_BLOOD_CONVERSION_MAP = Suppliers.memoize(() -> (DataMapType<Fluid, IFluidBloodConversion>) RegistryManager.getDataMap(Registries.FLUID, FLUID_BLOOD_CONVERSION_MAP_ID));
    public static final Supplier<DataMapType<EntityType<?>, IConverterEntry>> ENTITY_CONVERTER_MAP = Suppliers.memoize(() -> (DataMapType<EntityType<?>, IConverterEntry>) RegistryManager.getDataMap(Registries.ENTITY_TYPE, ENTITY_CONVERTER_MAP_ID));

    private static <T> @NotNull ResourceKey<Registry<T>> key(@NotNull String name) {
        return ResourceKey.createRegistryKey(new ResourceLocation(VReference.MODID, name));
    }

}