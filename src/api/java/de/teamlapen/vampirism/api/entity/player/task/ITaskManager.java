package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;

public interface ITaskManager {

    /**
     * opens a TaskBoardScreen based on the taskBoardId
     *
     * @param taskBoardId the unique id of the task board
     */
    void openTaskMasterScreen(int taskBoardId);

    /**
     * updated an open TaskBoardScreen on the client
     *
     * @param taskBoardId the unique id of the task board
     */
    void updateTaskMasterScreen(int taskBoardId);

    /**
     * checks if the task is unlocked and can be completed
     * cleans the task board from the task
     *
     * handles rewards and requirements
     *
     * @implNote syncs changes to the client
     * @param taskBoardId the id of the task board
     * @param task the task to complete
     */
    void completeTask(int taskBoardId, @Nonnull Task task);

    /**
     * accepts the task, so that the TaskManger knows that the player is working on the Task at the task board
     *
     * @implNote does not sync changes to the client
     * @param taskBoardId the id of the task board
     * @param task the accepted task
     */
    void acceptTask(int taskBoardId, @Nonnull Task task);

    /**
     * removes a accepted task from the task board and cleans the the task board from already completed requirements
     *
     * @implNote does not sync changes to the client
     * @param taskBoardId the id of the task board
     * @param task the aborted task
     */
    void abortTask(int taskBoardId, @Nonnull Task task);

    /**
     * checks if the task board has available tasks
     *
     * @param taskBoardId the id of the task board
     * @return weather the task board would show tasks
     */
    boolean hasAvailableTasks(int taskBoardId);

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
