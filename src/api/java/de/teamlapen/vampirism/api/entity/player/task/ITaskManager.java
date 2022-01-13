package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;
import java.util.UUID;

public interface ITaskManager {

    /**
     * removes a accepted taskInstance from the taskInstance board and cleans the the taskInstance board from already completed requirements
     *
     * @param taskBoardId  the id of the taskInstance board
     * @param taskInstance the aborted taskInstance
     * @param remove       whether the taskInstance should be removed from the taskInstance list
     * @implNote does not sync changes to the client
     */
    void abortTask(UUID taskBoardId, @Nonnull UUID taskInstance, boolean remove);

    @Deprecated
    void abortTask(UUID taskBoardId, @Nonnull ITaskInstance taskInstance, boolean remove); //TODO 1.17 remove

    @Deprecated
    void abortTask(UUID taskBoardId, @Nonnull Task task); //TODO 1.17 remove

    /**
     * accepts the taskInstance, so that the TaskManger knows that the player is working on the Task at the taskInstance board
     *
     * @param taskBoardId  the id of the taskInstance board
     * @param taskInstance the accepted taskInstance
     * @implNote does not sync changes to the client
     */
    void acceptTask(UUID taskBoardId, @Nonnull UUID taskInstance);

    /**
     * checks if the taskInstance is unlocked and can be completed
     * cleans the taskInstance board from the taskInstance
     * <p>
     * handles rewards and requirements
     *
     * @param taskBoardId  the id of the taskInstance board
     * @param taskInstance the taskInstance to complete
     * @implNote syncs changes to the client
     */
    void completeTask(UUID taskBoardId, @Nonnull UUID taskInstance);

    /**
     * checks if the task board has available tasks
     *
     * @param taskBoardId the id of the task board
     * @return weather the task board would show tasks
     */
    boolean hasAvailableTasks(UUID taskBoardId);

    /**
     * opens a TaskBoardScreen based on the taskBoardId
     *
     * @param taskBoardId the unique id of the task board
     */
    void openTaskMasterScreen(UUID taskBoardId);

    /**
     * Open menu container and send task information to the vampirism container
     */
    void openVampirismMenu();

    /**
     * cleans the TaskManager from every trace of completed/active tasks
     */
    void reset();

    /**
     * cleans all task boards from all tasks except unique tasks
     */
    void resetTaskLists();

    /**
     * Completely reset the given unique task.
     */
    void resetUniqueTask(Task task);

    /**
     * removes chosen, but not accepted tasks for every task board to have space for new ones
     */
    void updateTaskLists();

    /**
     * checks if the task was completed by the player
     *
     * @param task the task to check
     * @return whether the task has been completed or not
     */
    boolean wasTaskCompleted(@Nonnull Task task);
}
