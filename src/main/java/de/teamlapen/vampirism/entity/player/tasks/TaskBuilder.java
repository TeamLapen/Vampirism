package de.teamlapen.vampirism.entity.player.tasks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.FactionPlayerBooleanSupplier;
import de.teamlapen.vampirism.api.entity.player.FactionPlayerConsumer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.entity.player.tasks.req.*;
import de.teamlapen.vampirism.entity.player.tasks.reward.ConsumerReward;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.entity.player.tasks.unlock.ParentUnlocker;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class TaskBuilder {
    public static @NotNull TaskBuilder builder() {
        return new TaskBuilder();
    }

    /**
     * @deprecated this method is available to support legacy ids.
     */
    @Deprecated
    public static @NotNull TaskBuilder builder(ResourceKey<Task> taskKey) {
        return new TaskBuilder(taskKey);
    }

    @NotNull
    private final Map<ResourceLocation, TaskRequirement.Requirement<?>> requirement = new HashMap<>();
    @NotNull
    private final List<TaskUnlocker> unlocker = Lists.newArrayList();
    @Nullable
    private TaskReward reward;
    private Component title;
    private Component description;

    @Nullable
    private ResourceLocation taskId;

    private TaskBuilder() {
    }

    private TaskBuilder(@NotNull ResourceKey<Task> taskKey) {
        this.taskId = taskKey.location();
    }

    @Deprecated
    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, @NotNull EntityType<?> entityType, int amount) {
        return this.addRequirement(new EntityRequirement(VResourceLocation.loc(modId(), name), entityType, amount, requirementDescription(name)));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull EntityType<?> entityType, int amount, Component description) {
        return this.addRequirement(new EntityRequirement(entityType, amount, description));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull EntityType<?> entityType, int amount) {
        return this.addRequirement(new EntityRequirement(entityType, amount, Component.translatable(Util.makeDescriptionId("entity", RegUtil.id(entityType)))));
    }

    @Deprecated
    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, @NotNull TagKey<EntityType<?>> entityType, int amount) {
        return this.addRequirement(new EntityTypeRequirement(VResourceLocation.loc(modId(), name), entityType, amount, requirementDescription(name)));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull TagKey<EntityType<?>> entityType, int amount, Component description) {
        return this.addRequirement(new EntityTypeRequirement(entityType, amount, description));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull TagKey<EntityType<?>> entityType, int amount) {
        return this.addRequirement(new EntityTypeRequirement(entityType, amount, Component.translatable(Util.makeDescriptionId("entity_tag", entityType.location()))));
    }

    @Deprecated
    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, @NotNull ResourceLocation stat, int amount) {
        return this.addRequirement(new StatRequirement(VResourceLocation.loc(modId(), name), stat, amount, requirementDescription(name)));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull ResourceLocation stat, int amount, Component description) {
        return this.addRequirement(new StatRequirement(stat, amount, description));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull ResourceLocation stat, int amount) {
        return this.addRequirement(new StatRequirement(stat, amount, Component.translatable(Util.makeDescriptionId("stat", stat))));
    }

    @Deprecated
    public TaskBuilder addRequirement(@NotNull String name, ItemStack itemStack) {
        return this.addRequirement(new ItemRequirement(VResourceLocation.loc(modId(), name), itemStack, requirementDescription(name)));
    }

    public TaskBuilder addRequirement(ItemStack itemStack, Component description) {
        return this.addRequirement(new ItemRequirement(itemStack, description));
    }

    public TaskBuilder addRequirement(ItemStack itemStack) {
        return this.addRequirement(new ItemRequirement(itemStack, Component.translatable(Util.makeDescriptionId("item", RegUtil.id(itemStack.getItem())))));
    }

    @Deprecated
    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, @NotNull FactionPlayerBooleanSupplier function) {
        return this.addRequirement(new BooleanRequirement(VResourceLocation.loc(modId(), name), function, requirementDescription(name)));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull FactionPlayerBooleanSupplier function, Component description) {
        return this.addRequirement(new BooleanRequirement(function, description));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull FactionPlayerBooleanSupplier function) {
        return this.addRequirement(new BooleanRequirement(function, Component.translatable(Util.makeDescriptionId("faction_boolean_supplier", FactionPlayerBooleanSupplier.getId(function)))));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull TaskRequirement.Requirement<?> requirement) {
        Preconditions.checkArgument(!this.requirement.containsKey(requirement.id()), String.format("Requirement %s already exists", requirement.id()));
        this.requirement.put(requirement.id(), requirement);
        return this;
    }

    @NotNull
    public TaskBuilder setTitle(@NotNull Component title) {
        this.title = title;
        return this;
    }

    @NotNull
    public TaskBuilder setTitle(@NotNull ResourceLocation title) {
        this.title = Component.translatable(Util.makeDescriptionId("task", title));
        return this;
    }

    @Deprecated
    @NotNull
    public TaskBuilder defaultTitle() {
        Preconditions.checkArgument(this.taskId != null, "If you want to use legacy naming, you need to provide the builder with a task key");
        this.title = Component.translatable(Util.makeDescriptionId("task", this.taskId));
        return this;
    }

    @Deprecated
    private Component requirementDescription(String name) {
        Preconditions.checkArgument(this.taskId != null, "If you want to use legacy naming, you need to provide the builder with a task key");
        return Component.translatable(Util.makeDescriptionId("task", this.taskId) + ".req." + VResourceLocation.loc(modId(), name).toString().replace(":", "."));
    }


    @NotNull
    public Task build() {
        Preconditions.checkArgument(!this.requirement.isEmpty(), "Task needs requirements");
        Preconditions.checkArgument(this.reward != null, "Task needs a reward");
        Preconditions.checkArgument(this.title != null, "Task needs a title");
        return new Task(new TaskRequirement(this.requirement.values()), this.reward, this.unlocker.toArray(new TaskUnlocker[]{}), this.description, this.title);
    }

    @NotNull
    public TaskBuilder setDescription(Component description) {
        this.description = description;
        return this;
    }

    @NotNull
    public TaskBuilder setDescription(ResourceLocation title) {
        this.description = Component.translatable(Util.makeDescriptionId("task", title) + ".desc");
        return this;
    }

    @NotNull
    public TaskBuilder requireParent(@Nullable Holder<Task> parentTask) {
        this.unlocker.add(new ParentUnlocker(parentTask));
        return this;
    }

    @NotNull
    public TaskBuilder setReward(ItemStack reward) {
        this.reward = new ItemReward(reward);
        return this;
    }

    @NotNull
    public TaskBuilder setReward(TaskReward reward) {
        this.reward = reward;
        return this;
    }

    @NotNull
    public TaskBuilder setReward(FactionPlayerConsumer reward, Component description) {
        return setReward(new ConsumerReward(reward, description));
    }

    @NotNull
    public TaskBuilder unlockedBy(TaskUnlocker unlocker) {
        this.unlocker.add(unlocker);
        return this;
    }

    protected @NotNull String modId() {
        return REFERENCE.MODID;
    }
}
