package de.teamlapen.vampirism.player.tasks;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.player.tasks.req.EntityRequirement;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.player.tasks.req.StatRequirement;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TaskBuilder {
    private @Nullable TaskRequirement<?> requirement;
    private @Nullable TaskReward reward;
    private @Nullable IPlayableFaction<?> faction;
    private @Nullable Supplier<Task> parentTask;
    private boolean useDescription = false;

    private TaskBuilder() {
    }

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    public TaskBuilder withFaction(@Nullable IPlayableFaction<?> faction) {
        this.faction = faction;
        return this;
    }

    public TaskBuilder requireParent(@Nonnull Task parentTask) {
        this.parentTask = () -> parentTask;
        return this;
    }

    public TaskBuilder requireParent(@Nullable Supplier<Task> parentTask) {
        this.parentTask = parentTask;
        return this;
    }

    public TaskBuilder addRequirement(@Nonnull EntityType<?> entityType, int amount) {
        this.requirement = new EntityRequirement(entityType, amount);
        return this;
    }

    public <T extends IForgeRegistryEntry<?>> TaskBuilder addRequirement(@Nonnull ResourceLocation stat, int amount) {
        this.requirement = new StatRequirement(stat, amount);
        return this;
    }

    public TaskBuilder addRequirement(@Nonnull ItemStack itemStack) {
        this.requirement = new ItemRequirement(itemStack);
        return this;
    }

    public TaskBuilder addRequirement(@Nonnull Supplier<Boolean> supplier) {
        this.requirement = new TaskRequirement<Boolean>() {
            @Nonnull
            @Override
            public Boolean getStat() {
                return supplier.get();
            }
        };
        return this;
    }

    public TaskBuilder addRequirement(@Nonnull TaskRequirement<?> requirement) {
        this.requirement = requirement;
        return this;
    }

    public TaskBuilder addReward(@Nonnull ItemStack reward) {
        this.reward = new ItemReward(reward);
        return this;
    }

    public TaskBuilder addReward(@Nonnull Consumer<PlayerEntity> onFinished) {
        this.reward = onFinished::accept;
        return this;
    }

    public TaskBuilder addReward(@Nonnull TaskReward reward) {
        this.reward = reward;
        return this;
    }

    public TaskBuilder enableDescription() {
        this.useDescription = true;
        return this;
    }

    public Task build(ResourceLocation registryName) {
        if (requirement == null) throw new IllegalStateException("The task " + registryName + " needs a requirement");
        return new Task(this.faction, this.requirement, this.reward == null ? player -> {
        } : this.reward, this.parentTask, this.useDescription).setRegistryName(registryName);
    }

    public Task build(String modId, String name) {
        return this.build(new ResourceLocation(modId, name));
    }

    public Task build(String name) {
        return this.build(new ResourceLocation(REFERENCE.MODID, name));
    }
}
