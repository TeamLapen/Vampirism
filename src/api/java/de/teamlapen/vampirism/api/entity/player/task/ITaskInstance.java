package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
     * temporary here for compatibility with old worlds
     */
    @Deprecated
    void setStats(Map<ResourceLocation, Integer> requirements); //TODO 1.17 remove

    /**
     * @return the corresponding task
     */
    Task getTask();

    /**
     * @return the owning task board
     */
    UUID getTaskBoard();

    long getTaskDuration();

    /**
     * gets the time this instance is not longer available
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
    boolean isUnique();

    /**
     * sets the active status and timestamp
     *
     * @param timeStamp the gametime this task is not longer completable
     */
    void startTask(long timeStamp);

    /**
     * saves this instance to nbt
     *
     * @return parameter compoundNBT
     */
    CompoundTag writeNBT(CompoundTag compoundNBT);
}
