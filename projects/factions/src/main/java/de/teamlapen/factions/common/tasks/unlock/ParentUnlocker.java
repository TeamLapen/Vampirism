package de.teamlapen.factions.common.tasks.unlock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.api.tasks.ITaskPlayer;
import de.teamlapen.factions.api.tasks.Task;
import de.teamlapen.factions.api.tasks.TaskUnlocker;
import de.teamlapen.factions.common.core.FactionTasks;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public record ParentUnlocker(Holder<Task> parent) implements TaskUnlocker {

    public static final MapCodec<ParentUnlocker> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                Task.HOLDER_CODEC.fieldOf("parent").forGetter(i -> i.parent)
        ).apply(inst, ParentUnlocker::new);
    });

    @Override
    public Component getDescription() {
        return Component.translatable("text.vampirism.task.require_parent", this.parent.value().title());
    }

    @Override
    public <T extends ITaskPlayer<T>> boolean isUnlocked(@NotNull T playerEntity) {
        return this.parent.unwrapKey().map(key -> playerEntity.getTaskManager().wasTaskCompleted(key)).orElse(false);
    }

    @Override
    public MapCodec<? extends TaskUnlocker> codec() {
        return FactionTasks.PARENT_UNLOCKER.get();
    }
}
