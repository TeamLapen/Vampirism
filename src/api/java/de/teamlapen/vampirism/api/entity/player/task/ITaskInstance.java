package de.teamlapen.vampirism.api.entity.player.task;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.UUID;

public interface ITaskInstance {

    /**
     * whether the task is unique or not
     */
    boolean isUnique();

    /**
     * @return the corresponding task
     */
    Task getTask();

    /**
     * sets the active status to false and removes stats
     */
    void aboardTask();

    /**
     * sets the active status and timestamp
     * @param timeStamp the gametime this task is not longer completable
     */
    void startTask(long timeStamp);

    ITaskRewardInstance getReward();

    /**
     * gets the time this instance is not longer available
     *
     * <0 when this task is not accepted
     */
    long getTaskTimeStamp();

    /**
     * whether the instance is accepted or not
     */
    boolean isAccepted();

    /**
     * @return the owning task board
     */
    UUID getTaskBoard();

    /**
     * whether the instance is completed or not
     */
    boolean isCompleted();

    /**
     * completes this instance by setting the completed status to {@code true}
     */
    void complete();

    /**
     * @return unique identifier of this instance
     */
    UUID getId();

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
     * saves this instance to nbt
     *
     * @return parameter compoundNBT
     */
    CompoundNBT writeNBT(CompoundNBT compoundNBT);

    /**
     * encodes this instance in the packet buffer
     */
    void encode(PacketBuffer buffer);
}
