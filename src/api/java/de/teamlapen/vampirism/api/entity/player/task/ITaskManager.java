package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface ITaskManager {

    /**
     * opens a TaskBoardScreen based on the taskBoardId
     *
     * @param taskBoardId the unique id of the task board
     */
    void openTaskMasterScreen(UUID taskBoardId);

    /**
     * updated an open TaskBoardScreen on the client
     *
     * @param taskBoardId the unique id of the task board
     */
    void updateTaskMasterScreen(UUID taskBoardId);

    /**
     * checks if the task is unlocked and can be completed
     * cleans the task board from the task
     * <p>
     * handles rewards and requirements
     *
     * @param taskBoardId the id of the task board
     * @param task        the task to complete
     * @implNote syncs changes to the client
     */
    void completeTask(UUID taskBoardId, @Nonnull Task task);

    /**
     * accepts the task, so that the TaskManger knows that the player is working on the Task at the task board
     *
     * @param taskBoardId the id of the task board
     * @param task        the accepted task
     * @implNote does not sync changes to the client
     */
    void acceptTask(UUID taskBoardId, @Nonnull Task task);

    /**
     * removes a accepted task from the task board and cleans the the task board from already completed requirements
     *
     * @param taskBoardId the id of the task board
     * @param task        the aborted task
     * @implNote does not sync changes to the client
     */
    void abortTask(UUID taskBoardId, @Nonnull Task task);

    /**
     * checks if the task board has available tasks
     *
     * @param taskBoardId the id of the task board
     * @return weather the task board would show tasks
     */
    boolean hasAvailableTasks(UUID taskBoardId);

    /**
     * cleans the TaskManager from every trace of completed/active tasks
     */
    void reset();

    /**
     * checks if the task was completed by the player
     *
     * @param task the task to check
     * @return whether the task has been completed or not
     */
    boolean isTaskCompleted(@Nonnull Task task);

    /**
     * removes chosen, but not accepted tasks for every task board to have space for new ones
     */
    void updateTaskLists();

    /**
     * cleans all task boards from all tasks except unique tasks
     */
    void resetTaskLists();
}
