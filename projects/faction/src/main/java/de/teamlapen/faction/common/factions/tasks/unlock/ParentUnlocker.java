package de.teamlapen.faction.common.factions.tasks.unlock;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.tasks.ITaskPlayer;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.factions.tasks.TaskUnlocker;
import de.teamlapen.faction.common.core.FactionTasks;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

public record ParentUnlocker(Holder<Task> parent) implements TaskUnlocker {

    public static final MapCodec<ParentUnlocker> CODEC = RecordCodecBuilder.mapCodec(inst -> {
        return inst.group(
                Task.HOLDER_CODEC.fieldOf("parent").forGetter(i -> i.parent)
        ).apply(inst, ParentUnlocker::new);
    });

    @Override
    public Component getDescription() {
        return Component.translatable("text.factionapi.task.require_parent", this.parent.value().title());
    }

    @Override
    public <T extends ITaskPlayer<T>> boolean isUnlocked(T playerEntity) {
        return this.parent.unwrapKey().map(key -> playerEntity.getTaskManager().wasTaskCompleted(key)).orElse(false);
    }

    @Override
    public MapCodec<? extends TaskUnlocker> codec() {
        return FactionTasks.PARENT_UNLOCKER.get();
    }
}
