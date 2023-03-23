package de.teamlapen.vampirism.api.entity.player.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Task {

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                TaskRequirement.CODEC.fieldOf("requirements").forGetter(i -> i.requirements),
                TaskReward.CODEC.fieldOf("rewards").forGetter(i -> i.rewards),
                TaskUnlocker.CODEC.listOf().optionalFieldOf("unlocker").forGetter(i -> Optional.of(List.of(i.unlocker))),
                ExtraCodecs.COMPONENT.optionalFieldOf("description").forGetter(i -> Optional.ofNullable(i.description)),
                ExtraCodecs.COMPONENT.fieldOf("title").forGetter(i -> i.title)
        ).apply(inst, Task::new);
    });
    public static final Codec<Holder<Task>> HOLDER_CODEC = RegistryFileCodec.create(VampirismRegistries.TASK_ID, CODEC);

    @NotNull
    private final TaskRequirement requirements;
    @NotNull
    private final TaskReward rewards;
    @NotNull
    private final TaskUnlocker[] unlocker;
    @NotNull
    private final Component title;
    @Nullable
    private final Component description;

    public Task(@NotNull TaskRequirement requirements, @NotNull TaskReward rewards, @NotNull TaskUnlocker[] unlocker, @Nullable Component description, @NotNull Component title) {
        this.requirements = requirements;
        this.description = description;
        this.rewards = rewards;
        this.unlocker = unlocker;
        this.title = title;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Task(@NotNull TaskRequirement requirements, @NotNull TaskReward rewards, @NotNull Optional<List<TaskUnlocker>> unlocker, Optional<Component> description, Component title) {
        this(requirements, rewards, unlocker.map(list -> list.toArray(new TaskUnlocker[0])).orElseGet(() -> new TaskUnlocker[0]), description.orElse(null), title);
    }

    @NotNull
    public TaskRequirement getRequirement() {
        return this.requirements;
    }

    @NotNull
    public TaskReward getReward() {
        return this.rewards;
    }

    @NotNull
    public TaskUnlocker[] getUnlocker() {
        return unlocker;
    }

    public Optional<Component> getDescription() {
        return Optional.ofNullable(description);
    }

    public @NotNull Component getTitle() {
        return this.title;
    }
}
