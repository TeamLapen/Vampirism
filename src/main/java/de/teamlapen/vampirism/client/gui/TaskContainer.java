package de.teamlapen.vampirism.client.gui;

import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.client.gui.widget.TaskItem;
import net.minecraft.util.text.TextFormatting;

public interface TaskContainer {

    boolean isTaskNotAccepted(TaskItem.TaskInfo taskInfo);

    boolean canCompleteTask(TaskItem.TaskInfo taskInfo);

    boolean pressButton(TaskItem.TaskInfo taskInfo);

    TaskAction buttonAction(TaskItem.TaskInfo taskInfo);

    boolean isCompleted(TaskItem.TaskInfo item);

    TextFormatting getFactionColor();

    boolean areRequirementsCompleted(TaskItem.TaskInfo task, TaskRequirement.Type type);

    int getRequirementStatus(TaskItem.TaskInfo taskInfo, TaskRequirement.Requirement<?> requirement);

    boolean isRequirementCompleted(TaskItem.TaskInfo taskInfo, TaskRequirement.Requirement<?> requirement);

    enum TaskAction {
        COMPLETE, ACCEPT, ABORT
    }
}
