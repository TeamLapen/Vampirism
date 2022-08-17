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
import de.teamlapen.vampirism.api.util.NullableSupplier;
import de.teamlapen.vampirism.player.tasks.req.*;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.player.tasks.unlock.ParentUnlocker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class TaskBuilder {
    public static @NotNull TaskBuilder builder() {
        return new TaskBuilder();
    }

    @NotNull
    private final Map<TaskRequirement.Type, List<TaskRequirement.Requirement<?>>> requirement = Maps.newHashMapWithExpectedSize(TaskRequirement.Type.values().length);
    @NotNull
    private final List<TaskUnlocker> unlocker = Lists.newArrayList();
    @Nullable
    private TaskReward reward;
    @NotNull
    private NullableSupplier<IPlayableFaction<?>> faction = () -> null;
    @NotNull
    private Task.Variant variant = Task.Variant.REPEATABLE;
    private boolean useDescription = false;

    private TaskBuilder() {
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, @NotNull EntityType<?> entityType, int amount) {
        return this.addRequirement(new EntityRequirement(new ResourceLocation(modId(), name), entityType, amount));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, @NotNull TagKey<EntityType<?>> entityType, int amount) {
        return this.addRequirement(new EntityTypeRequirement(new ResourceLocation(modId(), name), entityType, amount));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, @NotNull ResourceLocation stat, int amount) {
        return this.addRequirement(new StatRequirement(new ResourceLocation(modId(), name), stat, amount));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, NonnullSupplier<ItemStack> itemStack) {
        return this.addRequirement(new ItemRequirement(new ResourceLocation(modId(), name), itemStack));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull String name, @NotNull BooleanRequirement.BooleanSupplier function) {
        return this.addRequirement(new BooleanRequirement(new ResourceLocation(modId(), name), function));
    }

    @NotNull
    public TaskBuilder addRequirement(@NotNull TaskRequirement.Requirement<?> requirement) {
        this.requirement.computeIfAbsent(requirement.getType(), type -> Lists.newArrayListWithExpectedSize(3)).add(requirement);
        return this;
    }

    @NotNull
    public Task build() {
        if (requirement.isEmpty()) throw new IllegalStateException("Task needs requirements");
        if (reward == null) throw new IllegalStateException("Task needs a reward");
        return new Task(this.variant, this.faction, new TaskRequirement(this.requirement), this.reward, this.unlocker.toArray(new TaskUnlocker[]{}), this.useDescription);
    }

    @NotNull
    public TaskBuilder enableDescription() {
        this.useDescription = true;
        return this;
    }

    @NotNull
    public TaskBuilder requireParent(@NotNull Task parentTask) {
        return this.requireParent(() -> parentTask);
    }

    @NotNull
    public TaskBuilder requireParent(@Nullable Supplier<Task> parentTask) {
        this.unlocker.add(new ParentUnlocker(parentTask));
        return this;
    }

    @NotNull
    public TaskBuilder setReward(NonnullSupplier<ItemStack> reward) {
        this.reward = new ItemReward(reward);
        return this;
    }

    @NotNull
    public TaskBuilder setReward(TaskReward reward) {
        this.reward = reward;
        return this;
    }

    @NotNull
    public TaskBuilder setUnique() {
        this.variant = Task.Variant.UNIQUE;
        return this;
    }

    @NotNull
    public TaskBuilder unlockedBy(TaskUnlocker unlocker) {
        this.unlocker.add(unlocker);
        return this;
    }

    @NotNull
    public TaskBuilder withFaction(@NotNull NullableSupplier<IPlayableFaction<?>> faction) {
        this.faction = faction;
        return this;
    }

    protected @NotNull String modId() {
        return REFERENCE.MODID;
    }
}
