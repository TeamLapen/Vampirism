package de.teamlapen.factions.common.tasks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.entities.player.FactionPlayerBooleanSupplier;
import de.teamlapen.factions.api.entities.player.FactionPlayerConsumer;
import de.teamlapen.factions.api.tasks.Task;
import de.teamlapen.factions.api.tasks.TaskRequirement;
import de.teamlapen.factions.api.tasks.TaskReward;
import de.teamlapen.factions.api.tasks.TaskUnlocker;
import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.core.ModRegistries;
import de.teamlapen.factions.common.tasks.requirements.*;
import de.teamlapen.factions.common.tasks.reward.ConsumerReward;
import de.teamlapen.factions.common.tasks.reward.ItemReward;
import de.teamlapen.factions.common.tasks.unlock.ParentUnlocker;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class TaskBuilder {
    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    /**
     * @deprecated this method is available to support legacy ids.
     */
    @Deprecated
    public static TaskBuilder builder(ResourceKey<Task> taskKey) {
        return new TaskBuilder(taskKey);
    }

    private final Map<ResourceLocation, TaskRequirement.Requirement<?>> requirement = new HashMap<>();
    private final List<TaskUnlocker> unlocker = Lists.newArrayList();
    @Nullable
    private TaskReward reward;
    @Nullable
    private Component title;
    @Nullable
    private Component description;

    @Nullable
    private ResourceLocation taskId;

    private TaskBuilder() {
    }

    private TaskBuilder(ResourceKey<Task> taskKey) {
        this.taskId = taskKey.location();
    }

    @Deprecated
    public TaskBuilder addRequirement(String name, EntityType<?> entityType, int amount) {
        return this.addRequirement(new EntityRequirement(FResourceLocation.loc(modId(), name), entityType, amount, requirementDescription(name)));
    }

    public TaskBuilder addRequirement(EntityType<?> entityType, int amount, Component description) {
        return this.addRequirement(new EntityRequirement(entityType, amount, description));
    }

    public TaskBuilder addRequirement(EntityType<?> entityType, int amount) {
        return this.addRequirement(new EntityRequirement(entityType, amount, Component.translatable(Util.makeDescriptionId("entity", RegUtil.id(entityType)))));
    }

    @Deprecated
    public TaskBuilder addRequirement(String name, TagKey<EntityType<?>> entityType, int amount) {
        return this.addRequirement(new EntityTypeRequirement(FResourceLocation.loc(modId(), name), entityType, amount, requirementDescription(name)));
    }

    public TaskBuilder addRequirement(TagKey<EntityType<?>> entityType, int amount, Component description) {
        return this.addRequirement(new EntityTypeRequirement(entityType, amount, description));
    }

    public TaskBuilder addRequirement(TagKey<EntityType<?>> entityType, int amount) {
        return this.addRequirement(new EntityTypeRequirement(entityType, amount, Component.translatable(Util.makeDescriptionId("entity_tag", entityType.location()))));
    }

    @Deprecated
    public TaskBuilder addRequirement(String name, ResourceLocation stat, int amount) {
        return this.addRequirement(new StatRequirement(FResourceLocation.loc(modId(), name), stat, amount, requirementDescription(name)));
    }

    public TaskBuilder addRequirement(ResourceLocation stat, int amount, Component description) {
        return this.addRequirement(new StatRequirement(stat, amount, description));
    }

    public TaskBuilder addRequirement( ResourceLocation stat, int amount) {
        return this.addRequirement(new StatRequirement(stat, amount, Component.translatable(Util.makeDescriptionId("stat", stat))));
    }

    @Deprecated
    public TaskBuilder addRequirement(String name, ItemStack itemStack) {
        return this.addRequirement(new ItemRequirement(FResourceLocation.loc(modId(), name), itemStack, requirementDescription(name)));
    }

    public TaskBuilder addRequirement(ItemStack itemStack, Component description) {
        return this.addRequirement(new ItemRequirement(itemStack, description));
    }

    public TaskBuilder addRequirement(ItemStack itemStack) {
        return this.addRequirement(new ItemRequirement(itemStack, Component.translatable(Util.makeDescriptionId("item", RegUtil.id(itemStack.getItem())))));
    }

    public TaskBuilder addRequirement(Holder<FactionPlayerBooleanSupplier> function, Component description) {
        return this.addRequirement(new BooleanRequirement(function, description));
    }

    public TaskBuilder addRequirement(Holder<FactionPlayerBooleanSupplier> function) {
        return this.addRequirement(new BooleanRequirement(function, Component.translatable(Util.makeDescriptionId("faction_boolean_supplier", function.getKey().location()))));
    }

    public TaskBuilder addRequirement(TaskRequirement.Requirement<?> requirement) {
        Preconditions.checkArgument(!this.requirement.containsKey(requirement.id()), String.format("Requirement %s already exists", requirement.id()));
        this.requirement.put(requirement.id(), requirement);
        return this;
    }

    public TaskBuilder setTitle(Component title) {
        this.title = title;
        return this;
    }

    public TaskBuilder setTitle(ResourceLocation title) {
        this.title = Component.translatable(Util.makeDescriptionId("task", title));
        return this;
    }

    @Deprecated
    public TaskBuilder defaultTitle() {
        Preconditions.checkArgument(this.taskId != null, "If you want to use legacy naming, you need to provide the builder with a task key");
        this.title = Component.translatable(Util.makeDescriptionId("task", this.taskId));
        return this;
    }

    @Deprecated
    private Component requirementDescription(String name) {
        Preconditions.checkArgument(this.taskId != null, "If you want to use legacy naming, you need to provide the builder with a task key");
        return Component.translatable(Util.makeDescriptionId("task", this.taskId) + ".req." + FResourceLocation.loc(modId(), name).toString().replace(":", "."));
    }


    public Task build() {
        Preconditions.checkArgument(!this.requirement.isEmpty(), "Task needs requirements");
        Preconditions.checkArgument(this.reward != null, "Task needs a reward");
        Preconditions.checkArgument(this.title != null, "Task needs a title");
        return new Task(new TaskRequirement(this.requirement.values()), this.reward, this.unlocker, Optional.ofNullable(this.description), this.title);
    }

    public TaskBuilder setDescription(Component description) {
        this.description = description;
        return this;
    }

    public TaskBuilder setDescription(ResourceLocation title) {
        this.description = Component.translatable(Util.makeDescriptionId("task", title) + ".desc");
        return this;
    }

    public TaskBuilder requireParent(@Nullable Holder<Task> parentTask) {
        this.unlocker.add(new ParentUnlocker(parentTask));
        return this;
    }

    public TaskBuilder setReward(ItemStack reward) {
        this.reward = new ItemReward(reward);
        return this;
    }

    public TaskBuilder setReward(TaskReward reward) {
        this.reward = reward;
        return this;
    }

    public TaskBuilder setReward(FactionPlayerConsumer reward, Component description) {
        return setReward(new ConsumerReward(reward, description));
    }

    public TaskBuilder unlockedBy(TaskUnlocker unlocker) {
        this.unlocker.add(unlocker);
        return this;
    }

    protected String modId() {
        return REFERENCE.MOD_ID;
    }
}
