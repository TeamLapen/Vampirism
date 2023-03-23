package de.teamlapen.vampirism.entity.player.tasks.unlock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.core.ModTasks;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public record ParentUnlocker(Holder<Task> parent) implements TaskUnlocker {

    public static final Codec<ParentUnlocker> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                Task.HOLDER_CODEC.fieldOf("parent").forGetter(i -> i.parent)
        ).apply(inst, ParentUnlocker::new);
    });

    @Override
    public @NotNull Component getDescription() {
        return Component.translatable("text.vampirism.task.require_parent", this.parent.get().getTitle());
    }

    @Override
    public boolean isUnlocked(@NotNull IFactionPlayer<?> playerEntity) {
        return this.parent.unwrapKey().map(key -> playerEntity.getTaskManager().wasTaskCompleted(key)).orElse(false);
    }

    @Override
    public Codec<? extends TaskUnlocker> codec() {
        return ModTasks.PARENT_UNLOCKER.get();
    }
}
