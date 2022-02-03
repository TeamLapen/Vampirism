package de.teamlapen.vampirism.player.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.api.util.NonnullSupplier;
import de.teamlapen.vampirism.player.tasks.req.*;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.player.tasks.unlock.ParentUnlocker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class TaskBuilder {
    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    @Nonnull
    private final Map<TaskRequirement.Type, List<TaskRequirement.Requirement<?>>> requirement = Maps.newHashMapWithExpectedSize(TaskRequirement.Type.values().length);
    @Nonnull
    private final List<TaskUnlocker> unlocker = Lists.newArrayList();
    @Nullable
    private TaskReward reward;
    @Nonnull
    private Supplier<IPlayableFaction<?>> faction = () -> null;
    @Nonnull
    private Task.Variant variant = Task.Variant.REPEATABLE;
    private boolean useDescription = false;

    private TaskBuilder() {
    }

    @Nonnull
    public TaskBuilder addRequirement(String name, @Nonnull EntityType<?> entityType, int amount) {
        return this.addRequirement(new EntityRequirement(new ResourceLocation(modId(), name), entityType, amount));
    }

    @Nonnull
    public TaskBuilder addRequirement(String name, @Nonnull TagKey<EntityType<?>> entityType, int amount) {
        return this.addRequirement(new EntityTypeRequirement(new ResourceLocation(modId(), name), entityType, amount));
    }

    @Nonnull
    public TaskBuilder addRequirement(String name, @Nonnull ResourceLocation stat, int amount) {
        return this.addRequirement(new StatRequirement(new ResourceLocation(modId(), name), stat, amount));
    }

    @Nonnull
    public TaskBuilder addRequirement(String name, NonnullSupplier<ItemStack> itemStack) {
        return this.addRequirement(new ItemRequirement(new ResourceLocation(modId(), name), itemStack));
    }

    @Nonnull
    public TaskBuilder addRequirement(String name, @Nonnull BooleanRequirement.BooleanSupplier function) {
        return this.addRequirement(new BooleanRequirement(new ResourceLocation(modId(), name), function));
    }

    @Nonnull
    public TaskBuilder addRequirement(@Nonnull TaskRequirement.Requirement<?> requirement) {
        this.requirement.computeIfAbsent(requirement.getType(), type -> Lists.newArrayListWithExpectedSize(3)).add(requirement);
        return this;
    }

    @Nonnull
    public Task build() {
        if (requirement.isEmpty()) throw new IllegalStateException("Task needs requirements");
        if (reward == null) throw new IllegalStateException("Task needs a reward");
        return new Task(this.variant, this.faction, new TaskRequirement(this.requirement), this.reward, this.unlocker.toArray(new TaskUnlocker[]{}), this.useDescription);
    }

    @Nonnull
    public TaskBuilder enableDescription() {
        this.useDescription = true;
        return this;
    }

    @Nonnull
    public TaskBuilder requireParent(@Nonnull Task parentTask) {
        return this.requireParent(() -> parentTask);
    }

    @Nonnull
    public TaskBuilder requireParent(@Nullable Supplier<Task> parentTask) {
        this.unlocker.add(new ParentUnlocker(parentTask));
        return this;
    }

    @Nonnull
    public TaskBuilder setReward(NonnullSupplier<ItemStack> reward) {
        this.reward = new ItemReward(reward);
        return this;
    }

    @Nonnull
    public TaskBuilder setReward(TaskReward reward) {
        this.reward = reward;
        return this;
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

    /**
     * @deprecated use {@link #withFaction(Supplier)}
     */
    @Deprecated
    @Nonnull
    public TaskBuilder withFaction(@Nullable IPlayableFaction<?> faction) {
        this.faction = () -> faction;
        return this;
    }

    @Nonnull
    public TaskBuilder withFaction(@Nullable Supplier<IPlayableFaction<?>> faction) {
        this.faction = faction;
        return this;
    }

    protected String modId() {
        return REFERENCE.MODID;
    }
}
