package de.teamlapen.vampirism.api.entity.player.task;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Task extends ForgeRegistryEntry<Task> {

    private final @Nullable IPlayableFaction<?> faction;
    private final @Nullable Task parentTask;
    private final @Nonnull ImmutableList<TaskRequirement> requirements;
    private @Nullable String translationKey;
    private @Nullable String descKey;

    public Task(@Nullable IPlayableFaction<?> faction, @Nonnull ImmutableList<TaskRequirement> requirements, @Nullable Task parentTask) {
        this.faction = faction;
        this.requirements = requirements;
        this.parentTask = parentTask;
    }

    @Nullable
    public IPlayableFaction<?> getFaction() {
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

    @Nonnull
    public String getTranslationKey() {
        return translationKey != null ? translationKey : (this.translationKey = Util.makeTranslationKey("task", this.getRegistryName()));
    }

    @Nonnull
    public String getDescriptionKey() {
        return descKey != null ? descKey : (descKey = (translationKey != null ? translationKey : getTranslationKey()) + ".desc");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private @Nullable IPlayableFaction<?> faction;
        private final ImmutableList.Builder<TaskRequirement> requirements = ImmutableList.builder();
        private @Nullable Task parentTask;

        public Builder withFaction(@Nullable IPlayableFaction<?> faction) {
            this.faction = faction;
            return this;
        }

        public Builder requireParent(@Nullable Task parentTask) {
            this.parentTask = parentTask;
            return this;
        }

        public Builder addEntityRequirement(EntityType<?> entityType, int amount) {
            this.requirements.add(new KillRequirement(entityType, amount));
            return this;
        }

        public Builder addItemRequirement(ItemStack itemStack) {
            this.requirements.add(new ItemRequirement(itemStack));
            return this;
        }

        public Task build() {
            ImmutableList<TaskRequirement> requirements = this.requirements.build();
            if (requirements.isEmpty()) throw new IllegalStateException("The Task needs Requirements");
            return new Task(faction, requirements, parentTask);
        }
    }

}
