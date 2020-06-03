package de.teamlapen.vampirism.player.tasks;

import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;
import java.util.function.Supplier;

public class ParentUnlocker implements TaskUnlocker {

    private Supplier<Task> parent;

    public ParentUnlocker(Supplier<Task> parent) {
        this.parent = parent;
    }

    @Override
    public boolean isUnlocked(PlayerEntity playerEntity) {
        return FactionPlayerHandler.getOpt(playerEntity).map(FactionPlayerHandler::getCurrentFactionPlayer).filter(Optional::isPresent).map(Optional::get).map(d -> d.getTaskManager().isTaskCompleted(parent.get())).orElse(false);
    }

    public Supplier<Task> getParent() {
        return parent;
    }
}
