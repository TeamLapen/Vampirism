package de.teamlapen.vampirism.api;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.datamaps.FluidBloodConversion;
import de.teamlapen.vampirism.api.datamaps.ItemBlood;
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
    public static final ResourceKey<Registry<Codec<? extends Converter>>> ENTITY_CONVERTER_ID = key("vampirism:converting_handler");

    // data pack registries
    public static final ResourceKey<Registry<Task>> TASK_ID = key("vampirism:tasks");
    public static final ResourceKey<Registry<ISkillNode>> SKILL_NODE_ID = key("vampirism:skill_node");
    public static final ResourceKey<Registry<ISkillTree>> SKILL_TREE_ID = key("vampirism:skill_tree");

    // data maps
    public static final ResourceLocation ITEM_BLOOD_VALUE_ID = new ResourceLocation(VReference.MODID, "item_blood_value");
    public static final ResourceLocation ENTITY_BLOOD_VALUE_ID = new ResourceLocation(VReference.MODID, "entity_blood_value");
    public static final ResourceLocation FLUID_BLOOD_CONVERSION_ID = new ResourceLocation(VReference.MODID, "fluid_blood_conversion");

    public static final Supplier<Registry<ISkill<?>>> SKILLS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) SKILLS_ID));
    public static final Supplier<Registry<IAction<?>>> ACTIONS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) ACTIONS_ID));
    public static final Supplier<Registry<IEntityAction>> ENTITY_ACTIONS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) ENTITY_ACTIONS_ID));
    public static final Supplier<Registry<IMinionTask<?, ?>>> MINION_TASKS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) MINION_TASKS_ID));
    public static final Supplier<Registry<IRefinement>> REFINEMENTS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) REFINEMENT_ID));
    public static final Supplier<Registry<IRefinementSet>> REFINEMENT_SETS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) REFINEMENT_SET_ID));
    public static final Supplier<Registry<IOil>> OILS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) OILS_ID));
    public static final Supplier<Registry<Codec<? extends TaskReward>>> TASK_REWARDS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) TASK_REWARD_ID));
    public static final Supplier<Registry<Codec<? extends TaskUnlocker>>> TASK_UNLOCKER = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) TASK_UNLOCKER_ID));
    public static final Supplier<Registry<Codec<? extends TaskRequirement.Requirement<?>>>> TASK_REQUIREMENTS = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) TASK_REQUIREMENT_ID));
    public static final Supplier<Registry<Codec<? extends ITaskRewardInstance>>> TASK_REWARD_INSTANCES = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) TASK_REWARD_INSTANCE_ID));
    public static final Supplier<Registry<Codec<? extends Converter>>> ENTITY_CONVERTER = Suppliers.memoize(() -> BuiltInRegistries.REGISTRY.get((ResourceKey) ENTITY_CONVERTER_ID));

    public static final Supplier<DataMapType<Item, ItemBlood>> ITEM_BLOOD_VALUES = Suppliers.memoize(() -> (DataMapType<Item, ItemBlood>) RegistryManager.getDataMap(Registries.ITEM, ITEM_BLOOD_VALUE_ID));
    public static final Supplier<DataMapType<Fluid, FluidBloodConversion>> FLUID_BLOOD_CONVERSION = Suppliers.memoize(() -> (DataMapType<Fluid, FluidBloodConversion>) RegistryManager.getDataMap(Registries.FLUID, FLUID_BLOOD_CONVERSION_ID));

    private static <T> @NotNull ResourceKey<Registry<T>> key(@NotNull String name) {
        return ResourceKey.createRegistryKey(new ResourceLocation(name));
    }

}