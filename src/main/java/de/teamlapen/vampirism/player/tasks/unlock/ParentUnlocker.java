package de.teamlapen.vampirism.player.tasks.unlock;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class ParentUnlocker implements TaskUnlocker {

    private final Supplier<Task> parent;

    public ParentUnlocker(Supplier<Task> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("text.vampirism.task.require_parent", parent.get().getTranslation());
    }

    public Supplier<Task> getParent() {
        return parent;
    }

    @Override
    public boolean isUnlocked(@NotNull IFactionPlayer<?> playerEntity) {
        return playerEntity.getTaskManager().wasTaskCompleted(parent.get());
    }
}
