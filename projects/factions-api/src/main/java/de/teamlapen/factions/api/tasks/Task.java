package de.teamlapen.factions.api.tasks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.FactionRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryFileCodec;

import java.util.List;
import java.util.Optional;

public record Task(TaskRequirement requirements, TaskReward reward, List<TaskUnlocker> unlocker,
                   Optional<Component> description, Component title) {

    public static final Codec<Task> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            TaskRequirement.CODEC.fieldOf("requirements").forGetter(i -> i.requirements),
            TaskReward.CODEC.fieldOf("reward").forGetter(i -> i.reward),
            TaskUnlocker.CODEC.listOf().optionalFieldOf("unlocker").forGetter(i -> Optional.of(i.unlocker)),
            ComponentSerialization.CODEC.optionalFieldOf("description").forGetter(i -> i.description),
            ComponentSerialization.CODEC.fieldOf("title").forGetter(i -> i.title)
    ).apply(inst, Task::new));
    public static final Codec<Holder<Task>> HOLDER_CODEC = RegistryFileCodec.create(FactionRegistries.Keys.TASK, CODEC);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Task(TaskRequirement requirements, TaskReward rewards, Optional<List<TaskUnlocker>> unlocker, Optional<Component> description, Component title) {
        this(requirements, rewards, unlocker.orElse(List.of()), description, title);
    }
}
