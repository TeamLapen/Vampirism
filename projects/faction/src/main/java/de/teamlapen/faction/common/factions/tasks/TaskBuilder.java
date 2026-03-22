package de.teamlapen.faction.common.factions.tasks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.factions.tasks.TaskRequirement;
import de.teamlapen.faction.api.factions.tasks.TaskReward;
import de.teamlapen.faction.api.factions.tasks.TaskUnlocker;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerBooleanSupplier;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.faction.common.factions.tasks.requirements.*;
import de.teamlapen.faction.common.factions.tasks.reward.ConsumerReward;
import de.teamlapen.faction.common.factions.tasks.reward.ItemReward;
import de.teamlapen.faction.common.factions.tasks.unlock.ParentUnlocker;
import de.teamlapen.faction.common.util.RegUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class TaskBuilder {

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    private final Map<Identifier, TaskRequirement.Requirement<?>> requirement = new HashMap<>();
    private final List<TaskUnlocker> unlocker = Lists.newArrayList();
    @Nullable
    private TaskReward reward;
    @Nullable
    private Component title;
    @Nullable
    private Component description;

    private TaskBuilder() {
    }

    public TaskBuilder addRequirement(EntityType<?> entityType, int amount, Component description) {
        return this.addRequirement(new EntityRequirement(entityType, amount, description));
    }

    public TaskBuilder addRequirement(EntityType<?> entityType, int amount) {
        return this.addRequirement(new EntityRequirement(entityType, amount, Component.translatable(Util.makeDescriptionId("entity", RegUtil.id(entityType)))));
    }

    public TaskBuilder addRequirement(TagKey<EntityType<?>> entityType, int amount, Component description) {
        return this.addRequirement(new EntityTypeRequirement(entityType, amount, description));
    }

    public TaskBuilder addRequirement(TagKey<EntityType<?>> entityType, int amount) {
        return this.addRequirement(new EntityTypeRequirement(entityType, amount, Component.translatable(Util.makeDescriptionId("task_tag", entityType.location()))));
    }

    public TaskBuilder addRequirement(Identifier stat, int amount, Component description) {
        return this.addRequirement(new StatRequirement(stat, amount, description));
    }

    public TaskBuilder addRequirement( Identifier stat, int amount) {
        return this.addRequirement(new StatRequirement(stat, amount, Component.translatable(Util.makeDescriptionId("stat", stat))));
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
        return this.addRequirement(new BooleanRequirement(function, Component.translatable(Util.makeDescriptionId("faction_boolean_supplier", function.getKey().identifier()))));
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

    public void build(BiConsumer<ResourceKey<Task>, Task> consumer, ResourceKey<Task> key) {
        build(consumer, key, false);
    }

    public void build(BiConsumer<ResourceKey<Task>, Task> consumer, ResourceKey<Task> key, boolean withDescription) {
        if (this.title == null) {
            this.title = Component.translatable(Util.makeDescriptionId("task", key.identifier()));
        }
        if (this.description == null && withDescription) {
            this.description = Component.translatable(Util.makeDescriptionId("task", key.identifier().withPath(x -> x + ".description")));
        }
        Preconditions.checkArgument(!this.requirement.isEmpty(), "Task needs requirements");
        Preconditions.checkArgument(this.reward != null, "Task needs a reward");
        consumer.accept(key, new Task(new TaskRequirement(this.requirement.values()), this.reward, this.unlocker, Optional.ofNullable(this.description), this.title));
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

    public TaskBuilder requireParent(Holder<Task> parentTask) {
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
}
