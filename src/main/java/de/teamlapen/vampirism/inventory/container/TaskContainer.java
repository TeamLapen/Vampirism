package de.teamlapen.vampirism.inventory.container;

import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.ChatFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TaskContainer {

    /**
     * @return whether all task requirements of the specific type are completed for the {@link ITaskInstance#getTask()} at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    boolean areRequirementsCompleted(@Nonnull ITaskInstance task, @Nonnull TaskRequirement.Type type);

    /**
     * @return what action should be performed when pressing the button in the dummy task of the task giver {@link ITaskInstance#getTaskBoard()}
     */
    TaskAction buttonAction(@Nonnull ITaskInstance taskInfo);

    /**
     * @return whether the {@link ITaskInstance#getTask()} can be completed at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    boolean canCompleteTask(@Nonnull ITaskInstance taskInfo);

    /**
     * @return chat color of the faction
     */
    ChatFormatting getFactionColor();

    /**
     * @return the progress of the requirement for the {@link ITaskInstance#getTask()} at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    int getRequirementStatus(@Nonnull ITaskInstance taskInfo, @Nonnull TaskRequirement.Requirement<?> requirement);

    /**
     * @return {@code true} if the task is already completed at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    boolean isCompleted(@Nonnull ITaskInstance item);

    /**
     * @return whether task requirement of the {@link ITaskInstance#getTask()} are completed at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    boolean isRequirementCompleted(@Nonnull ITaskInstance taskInfo, @Nonnull TaskRequirement.Requirement<?> requirement);

    /**
     * @return whether the {@link ITaskInstance#getTask()} is not accepted at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    default boolean isTaskNotAccepted(@Nonnull ITaskInstance taskInfo) {
        return !taskInfo.isAccepted();
    }

    /**
     * perform the action after the button for this {@link ITaskInstance#getTask()} has been pressed for the task giver {@link ITaskInstance#getTaskBoard()}
     */
    void pressButton(@Nonnull ITaskInstance taskInfo);

    /**
     * @param listener the listener will be run if the container got updated
     */
    void setReloadListener(@Nullable Runnable listener);

    enum TaskAction {
        /**
         * The task can be completed
         */
        COMPLETE("gui.vampirism.taskmaster.complete_task"),
        /**
         * The task can be accepted
         */
        ACCEPT("gui.vampirism.taskmaster.accept_task"),
        /**
         * The task can be aborted
         */
        ABORT("gui.vampirism.taskmaster.abort_task"),
        /**
         * The task can only be removed
         */
        REMOVE("gui.vampirism.taskmaster.remove_task");

        @Nonnull
        private final String translationKey;

        TaskAction(@Nonnull String translationKey) {
            this.translationKey = translationKey;
        }

        @Nonnull
        public String getTranslationKey() {
            return translationKey;
        }
    }
}
