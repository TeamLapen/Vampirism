package de.teamlapen.vampirism.player.tasks.unlock;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;

import java.util.function.Supplier;

public class ParentUnlocker implements TaskUnlocker {

    private final Supplier<Task> parent;

    public ParentUnlocker(Supplier<Task> parent) {
        this.parent = parent;
    }

    @Override
    public boolean isUnlocked(IFactionPlayer<?> playerEntity) {
        return playerEntity.getTaskManager().isTaskCompleted(parent.get());
    }

    public Supplier<Task> getParent() {
        return parent;
    }
}
