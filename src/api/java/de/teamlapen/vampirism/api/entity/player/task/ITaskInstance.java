package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public interface ITaskInstance {

    /**
     * sets the active status to false and removes stats
     */
    void aboardTask();

    /**
     * completes this instance by setting the completed status to {@code true}
     */
    void complete();

    /**
     * encodes this instance in the packet buffer
     */
    void encode(FriendlyByteBuf buffer);

    /**
     * @return unique identifier of this instance
     */
    UUID getId();

    ITaskRewardInstance getReward();

    /**
     * @return stats for this instance
     */
    Map<ResourceLocation, Integer> getStats();

    /**
     * @return the corresponding task
     */
    ResourceKey<Task> getTask();

    /**
     * @return the owning task board
     */
    UUID getTaskBoard();

    long getTaskDuration();

    /**
     * gets the time this instance is no longer available
     * <p>
     * <0 when this task is not accepted
     */
    long getTaskTimeStamp();

    /**
     * whether the instance is accepted or not
     */
    boolean isAccepted();

    /**
     * whether the instance is completed or not
     */
    boolean isCompleted();

    /**
     * whether the task is unique or not
     */
    boolean isUnique(Registry<Task> registry);

    /**
     * sets the active status and timestamp
     *
     * @param timeStamp the game time this task is no longer completable
     */
    void startTask(long timeStamp);

}
