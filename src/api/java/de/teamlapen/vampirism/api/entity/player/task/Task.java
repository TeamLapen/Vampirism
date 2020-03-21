package de.teamlapen.vampirism.api.entity.player.task;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Task extends ForgeRegistryEntry<Task> {

    private final @Nullable IPlayableFaction<? extends IFaction<?>> faction;
    private final @Nullable Task parentTask;
    private final @Nonnull ImmutableList<TaskRequirement> requirements;

    public Task(@Nullable IPlayableFaction<? extends IFaction<?>> faction, @Nonnull ImmutableList<TaskRequirement> requirements, @Nullable Task parentTask) {
        this.faction = faction;
        this.requirements = requirements;
        this.parentTask = parentTask;
    }

    @Nullable
    public IPlayableFaction<? extends IFaction<?>> getFaction() {
        return faction;
    }

    @Nonnull
    public ImmutableList<TaskRequirement> getRequirements() {
        return requirements;
    }

    @Nullable
    public Task getParentTask() {
        return parentTask;
    }
}
