package de.teamlapen.factions.common.tasks;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.tasks.ITaskInstance;
import de.teamlapen.factions.api.tasks.ITaskRewardInstance;
import de.teamlapen.factions.api.tasks.Task;
import de.teamlapen.factions.common.tags.FactionTaskTags;
import net.minecraft.core.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskInstance implements ITaskInstance {

    public static final Codec<TaskInstance> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                UUIDUtil.STRING_CODEC.fieldOf("taskGiver").forGetter(t -> t.taskGiver),
                ResourceKey.codec(FactionRegistries.Keys.TASK).fieldOf("task").forGetter(t -> t.task),
                UUIDUtil.STRING_CODEC.fieldOf("instanceId").forGetter(t -> t.instanceId),
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("stats").forGetter(t -> t.stats),
                ITaskRewardInstance.CODEC.fieldOf("reward").forGetter(t -> t.reward),
                Codec.LONG.fieldOf("taskDuration").forGetter(t -> t.taskDuration),
                Codec.BOOL.fieldOf("accepted").forGetter(t -> t.accepted),
                Codec.LONG.fieldOf("taskTimer").forGetter(t -> t.taskTimeStamp)
        ).apply(inst, TaskInstance::new);
    });

    private final UUID taskGiver;
    private final ResourceKey<Task> task;
    private final UUID instanceId;
    private final Map<ResourceLocation, Integer> stats;
    private final ITaskRewardInstance reward;
    private final long taskDuration;
    private boolean accepted;
    private long taskTimeStamp;
    private boolean completed;

    public TaskInstance(Holder<Task> task, UUID taskGiver, IFactionPlayer<?> player, long taskDuration) {
        this.task = task.getKey();
        this.taskGiver = taskGiver;
        this.instanceId = UUID.randomUUID();
        this.stats = new HashMap<>();
        this.taskTimeStamp = -1;
        this.taskDuration = taskDuration;
        this.reward = task.value().reward().createInstance(player);
    }

    private TaskInstance(UUID taskGiver, ResourceKey<Task> task, UUID instanceId, Map<ResourceLocation, Integer> stats, ITaskRewardInstance taskRewardInstance, long taskDuration, boolean accepted, long taskTimeStamp) {
        this.taskGiver = taskGiver;
        this.task = task;
        this.stats = new HashMap<>(stats);
        this.accepted = accepted;
        this.taskTimeStamp = taskTimeStamp;
        this.instanceId = instanceId;
        this.reward = taskRewardInstance;
        this.taskDuration = taskDuration;
    }

    public void aboardTask() {
        this.accepted = false;
        this.stats.clear();
        this.taskTimeStamp = -1;
    }

    public void complete() {
        this.completed = true;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskInstance instance = (TaskInstance) o;
        return accepted == instance.accepted && taskTimeStamp == instance.taskTimeStamp && Objects.equal(taskGiver, instance.taskGiver) && Objects.equal(instanceId, instance.instanceId) && Objects.equal(task, instance.task) && Objects.equal(stats, instance.stats);
    }

    public UUID getId() {
        return instanceId;
    }

    @Override
    public ITaskRewardInstance getReward() {
        return this.reward;
    }

    public Map<ResourceLocation, Integer> getStats() {
        return stats;
    }

    public ResourceKey<Task> getTask() {
        return this.task;
    }

    @Override
    public UUID getTaskBoard() {
        return this.taskGiver;
    }

    public long getTaskDuration() {
        return taskDuration;
    }

    public long getTaskTimeStamp() {
        return taskTimeStamp;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskGiver, task, instanceId);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isUnique(Registry<Task> registry) {
        return registry.get(this.task).map(s -> s.is(FactionTaskTags.IS_UNIQUE)).orElse(false);
    }

    public void startTask(long timestamp) {
        this.taskTimeStamp = timestamp;
        this.accepted = true;
    }
}
