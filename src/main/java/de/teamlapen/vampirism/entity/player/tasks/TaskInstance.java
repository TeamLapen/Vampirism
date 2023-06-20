package de.teamlapen.vampirism.entity.player.tasks;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.entity.player.tasks.reward.LordLevelReward;
import de.teamlapen.vampirism.util.CodecUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskInstance implements ITaskInstance {

    public static final Codec<TaskInstance> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                CodecUtil.UUID.fieldOf("taskGiver").forGetter(t -> t.taskGiver),
                ResourceKey.codec(VampirismRegistries.TASK_ID).fieldOf("task").forGetter(t -> t.task),
                CodecUtil.UUID.fieldOf("instanceId").forGetter(t -> t.instanceId),
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("stats").forGetter(t -> t.stats),
                ITaskRewardInstance.CODEC.fieldOf("reward").forGetter(t -> t.reward),
                Codec.LONG.fieldOf("taskDuration").forGetter(t -> t.taskDuration),
                Codec.BOOL.fieldOf("accepted").forGetter(t -> t.accepted),
                Codec.LONG.fieldOf("taskTimer").forGetter(t -> t.taskTimeStamp)
        ).apply(inst, TaskInstance::new);
    });

    @Deprecated
    @NotNull
    public static TaskInstance readLegacy(@NotNull CompoundTag nbt) {
        ResourceKey<Task> task = ResourceKey.create(VampirismRegistries.TASK_ID, new ResourceLocation(nbt.getString("task")));
        UUID id = nbt.getUUID("id");
        UUID insId = nbt.getUUID("insId");
        boolean accepted = nbt.getBoolean("accepted");
        long taskTimer = nbt.getLong("taskTimer");
        CompoundTag statsNBT = nbt.getCompound("stats");
        Map<ResourceLocation, Integer> stats = new HashMap<>();
        statsNBT.getAllKeys().forEach(name -> stats.put(new ResourceLocation(name), statsNBT.getInt(name)));
        ResourceLocation rewardId = new ResourceLocation(nbt.getString("rewardId"));
        ITaskRewardInstance reward = createReward(rewardId, nbt);
        long taskDuration = nbt.getLong("taskDuration");
        return new TaskInstance(id, task, insId, stats, reward, taskDuration, accepted, taskTimer);
    }

    private static ITaskRewardInstance createReward(ResourceLocation id, CompoundTag tag) {
        return switch (id.toString()) {
            case "vampirism:item" -> new ItemReward.Instance(ItemStack.of(tag.getCompound("reward")));
            case "vampirism:lord_level_reward" -> new LordLevelReward(tag.getInt("targetLevel"));
            default -> throw new IllegalArgumentException("Unknown reward id " + id);
        };
    }


    @NotNull
    private final UUID taskGiver;
    @NotNull
    private final ResourceKey<Task> task;
    @NotNull
    private final UUID instanceId;
    @NotNull
    private final Map<ResourceLocation, Integer> stats;
    @NotNull
    private final ITaskRewardInstance reward;
    private final long taskDuration;
    private boolean accepted;
    private long taskTimeStamp;
    private boolean completed;

    public TaskInstance(@NotNull Holder.Reference<Task> task, @NotNull UUID taskGiver, @NotNull IFactionPlayer<?> player, long taskDuration) {
        this.task = task.key();
        this.taskGiver = taskGiver;
        this.instanceId = UUID.randomUUID();
        this.stats = new HashMap<>();
        this.taskTimeStamp = -1;
        this.taskDuration = taskDuration;
        this.reward = task.value().getReward().createInstance(player);
    }

    private TaskInstance(@NotNull UUID taskGiver, @NotNull ResourceKey<Task> task, @NotNull UUID instanceId, @NotNull Map<ResourceLocation, Integer> stats, @NotNull ITaskRewardInstance taskRewardInstance, long taskDuration, boolean accepted, long taskTimeStamp) {
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

    public void encode(@NotNull FriendlyByteBuf buffer) {
        buffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskInstance instance = (TaskInstance) o;
        return accepted == instance.accepted && taskTimeStamp == instance.taskTimeStamp && Objects.equal(taskGiver, instance.taskGiver) && Objects.equal(instanceId, instance.instanceId) && Objects.equal(task, instance.task) && Objects.equal(stats, instance.stats);
    }

    @NotNull
    public UUID getId() {
        return instanceId;
    }

    @NotNull
    @Override
    public ITaskRewardInstance getReward() {
        return this.reward;
    }

    @NotNull
    public Map<ResourceLocation, Integer> getStats() {
        return stats;
    }

    @NotNull
    public ResourceKey<Task> getTask() {
        return task;
    }

    @Override
    public @NotNull UUID getTaskBoard() {
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
        return registry.getHolder(this.task).map(s -> s.is(ModTags.Tasks.IS_UNIQUE)).orElse(false);
    }

    public void startTask(long timestamp) {
        this.taskTimeStamp = timestamp;
        this.accepted = true;
    }

    public @NotNull CompoundTag writeNBT(@NotNull CompoundTag nbt) {
        DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE, this);
        nbt.put("instance", result.getOrThrow(false, (message) -> LogManager.getLogger().error(message)));
        return nbt;
    }
}
