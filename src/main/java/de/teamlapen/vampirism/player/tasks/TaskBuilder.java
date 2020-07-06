package de.teamlapen.vampirism.player.tasks;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.player.tasks.req.EntityRequirement;
import de.teamlapen.vampirism.player.tasks.req.EntityTypeRequirement;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.player.tasks.req.StatRequirement;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.player.tasks.unlock.ParentUnlocker;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class TaskBuilder {
    @Nonnull
    private final List<TaskRequirement.Requirement<?>> requirement = Lists.newArrayList();
    @Nullable
    private TaskReward reward;
    @Nullable
    private IPlayableFaction<?> faction;
    @Nonnull
    private final List<TaskUnlocker> unlocker = Lists.newArrayList();
    @Nonnull
    private Task.Variant variant = Task.Variant.REPEATABLE;
    private boolean useDescription = false;

    private TaskBuilder() {
    }

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    @Nonnull
    public TaskBuilder withFaction(@Nullable IPlayableFaction<?> faction) {
        this.faction = faction;
        return this;
    }

    @Nonnull
    public TaskBuilder requireParent(@Nonnull Task parentTask) {
        return this.requireParent(()->parentTask);
    }

    @Nonnull
    public TaskBuilder setUnique() {
        this.variant = Task.Variant.UNIQUE;
        return this;
    }

    @Nonnull
    public TaskBuilder unlockedBy(TaskUnlocker unlocker) {
        this.unlocker.add(unlocker);
        return this;
    }

    @Nonnull
    public TaskBuilder requireParent(@Nullable Supplier<Task> parentTask) {
        this.unlocker.add(new ParentUnlocker(parentTask));
        return this;
    }

    @Nonnull
    public TaskBuilder addRequirement(String name, @Nonnull EntityType<?> entityType, int amount) {
        this.requirement.add(new EntityRequirement(new ResourceLocation(modId(),name), entityType, amount));
        return this;
    }

    @Nonnull
    public TaskBuilder addRequirement(String name, @Nonnull Tag<EntityType<?>> entityType, int amount) {
        this.requirement.add(new EntityTypeRequirement(new ResourceLocation(modId(), name), entityType, amount));
        return this;
    }

    @Nonnull
    public <T extends IForgeRegistryEntry<?>> TaskBuilder setRequirement(String name, @Nonnull ResourceLocation stat, int amount) {
        this.requirement.add(new StatRequirement(new ResourceLocation(modId(), name), stat, amount));
        return this;
    }

    @Nonnull
    public TaskBuilder addRequirement(String name, @Nonnull ItemStack itemStack) {
        this.requirement.add(new ItemRequirement(new ResourceLocation(modId(),name), itemStack));
        return this;
    }

    @Nonnull
    public TaskBuilder addRequirement(@Nonnull TaskRequirement.Requirement<?> requirement) {
        this.requirement.add(requirement);
        return this;
    }

    @Nonnull
    public TaskBuilder setReward(@Nonnull ItemStack reward) {
        this.reward = new ItemReward(reward);
        return this;
    }

    @Nonnull
    public TaskBuilder setReward(@Nonnull TaskReward reward) {
        this.reward = reward;
        return this;
    }

    @Nonnull
    public TaskBuilder enableDescription() {
        this.useDescription = true;
        return this;
    }

    @Nonnull
    public Task build(ResourceLocation registryName) {
        if (requirement.isEmpty()) throw new IllegalStateException("The task " + registryName + " needs requirements");
        return new Task(this.variant, this.faction, new TaskRequirement(this.requirement.toArray(new TaskRequirement.Requirement[]{})), this.reward == null ? player -> {
        } : this.reward, this.unlocker.toArray(new TaskUnlocker[]{}), this.useDescription).setRegistryName(registryName);
    }

    protected String modId() {
        return REFERENCE.MODID;
    }

    @Nonnull
    public Task build(String modId, String name) {
        return this.build(new ResourceLocation(modId, name));
    }

    @Nonnull
    public Task build(String name) {
        return this.build(new ResourceLocation(modId(), name));
    }
}
