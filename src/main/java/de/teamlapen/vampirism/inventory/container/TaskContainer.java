package de.teamlapen.vampirism.inventory.container;

import com.google.common.base.Objects;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public interface TaskContainer {

    /**
     * @return whether the {@link TaskInfo#task} is not accepted at the task giver {@link TaskInfo#taskBoard}
     */
    boolean isTaskNotAccepted(TaskInfo taskInfo);

    /**
     * @return whether the {@link TaskInfo#task} can be completed at the task giver {@link TaskInfo#taskBoard}
     */
    boolean canCompleteTask(TaskInfo taskInfo);

    /**
     * perform the action after the button for this {@link TaskInfo#task} has been pressed for the task giver {@link TaskInfo#taskBoard}
     */
    boolean pressButton(TaskInfo taskInfo);

    /**
     * @return what action should be performed when pressing the button in the dummy task of the task giver {@link TaskInfo#taskBoard}
     */
    TaskAction buttonAction(TaskInfo taskInfo);

    /**
     * @return {@code true} if the task is already completed at the task giver {@link TaskInfo#taskBoard}
     */
    boolean isCompleted(TaskInfo item);

    /**
     * @return chat color of the faction
     */
    TextFormatting getFactionColor();

    /**
     * @return whether all task requirements of the specific type are completed for the {@link TaskInfo#task} at the task giver {@link TaskInfo#taskBoard}
     */
    boolean areRequirementsCompleted(TaskInfo task, TaskRequirement.Type type);

    /**
     * @return the progress of the requirement for the {@link TaskInfo#task} at the task giver {@link TaskInfo#taskBoard}
     */
    int getRequirementStatus(TaskInfo taskInfo, TaskRequirement.Requirement<?> requirement);

    /**
     * @return whether task requirement of the {@link TaskInfo#task} are completed at the task giver {@link TaskInfo#taskBoard}
     */
    boolean isRequirementCompleted(TaskInfo taskInfo, TaskRequirement.Requirement<?> requirement);

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

        private final String translationKey;

        TaskAction(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }

    class TaskInfo {

        /**
         * the task
         */
        @Nonnull
        public final Task task;
        /**
         * the id og the task giver
         */
        @Nonnull
        public final UUID taskBoard;

        @Nonnull
        public Supplier<Long> remainingTime;

        public TaskInfo(@Nonnull Task task, @Nonnull UUID taskBoard, @Nonnull Supplier<Long> remainingTime) {
            this.task = task;
            this.taskBoard = taskBoard;
            this.remainingTime = remainingTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TaskInfo taskInfo = (TaskInfo) o;
            return Objects.equal(task, taskInfo.task) && Objects.equal(taskBoard, taskInfo.taskBoard);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(task, taskBoard);
        }
    }
}
