package de.teamlapen.vampirism.inventory;

import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TaskMenu {

    /**
     * @return whether all task requirements of the specific type are completed for the {@link ITaskInstance#getTask()} at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    boolean areRequirementsCompleted(@NotNull ITaskInstance task, @NotNull TaskRequirement.Type type);

    /**
     * @return what action should be performed when pressing the button in the dummy task of the task giver {@link ITaskInstance#getTaskBoard()}
     */
    TaskAction buttonAction(@NotNull ITaskInstance taskInfo);

    /**
     * @return whether the {@link ITaskInstance#getTask()} can be completed at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    boolean canCompleteTask(@NotNull ITaskInstance taskInfo);

    /**
     * @return chat color of the faction
     */
    TextColor getFactionColor();

    /**
     * @return the progress of the requirement for the {@link ITaskInstance#getTask()} at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    int getRequirementStatus(@NotNull ITaskInstance taskInfo, @NotNull TaskRequirement.Requirement<?> requirement);

    /**
     * @return {@code true} if the task is already completed at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    boolean isCompleted(@NotNull ITaskInstance item);

    /**
     * @return whether task requirement of the {@link ITaskInstance#getTask()} are completed at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    boolean isRequirementCompleted(@NotNull ITaskInstance taskInfo, @NotNull TaskRequirement.Requirement<?> requirement);

    /**
     * @return whether the {@link ITaskInstance#getTask()} is not accepted at the task giver {@link ITaskInstance#getTaskBoard()}
     */
    default boolean isTaskNotAccepted(@NotNull ITaskInstance taskInfo) {
        return !taskInfo.isAccepted();
    }

    /**
     * perform the action after the button for this {@link ITaskInstance#getTask()} has been pressed for the task giver {@link ITaskInstance#getTaskBoard()}
     */
    void pressButton(@NotNull ITaskInstance taskInfo);

    /**
     * @param listener the listener will be run if the container got updated
     */
    void setReloadListener(@Nullable Runnable listener);

    Registry<Task> getRegistry();

    default Task getTask(ResourceKey<Task> key) {
        return getRegistry().getOrThrow(key);
    }

    enum TaskAction implements StringRepresentable {
        /**
         * The task can be completed
         */
        COMPLETE("complete", "gui.vampirism.taskmaster.complete_task"),
        /**
         * The task can be accepted
         */
        ACCEPT("accept", "gui.vampirism.taskmaster.accept_task"),
        /**
         * The task can be aborted
         */
        ABORT("abort", "gui.vampirism.taskmaster.abort_task"),
        /**
         * The task can only be removed
         */
        REMOVE("remove", "gui.vampirism.taskmaster.remove_task");

        private final String name;
        @NotNull
        private final String translationKey;

        TaskAction(@NotNull String name, @NotNull String translationKey) {
            this.name = name;
            this.translationKey = translationKey;
        }

        @NotNull
        public String getTranslationKey() {
            return translationKey;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
