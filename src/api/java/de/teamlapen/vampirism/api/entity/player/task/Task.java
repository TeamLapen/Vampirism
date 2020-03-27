package de.teamlapen.vampirism.api.entity.player.task;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Task extends ForgeRegistryEntry<Task> {

    private final @Nullable IPlayableFaction<?> faction;
    private final @Nullable Task parentTask;
    private final @Nonnull ImmutableList<TaskRequirement> requirements;
    private @Nullable String translationKey;
    private @Nullable String descKey;
    private @Nullable ITextComponent translation;
    private @Nullable ITextComponent desc;

    public Task(@Nullable IPlayableFaction<?> faction, @Nonnull ImmutableList<TaskRequirement> requirements, @Nullable Task parentTask) {
        this.faction = faction;
        this.requirements = requirements;
        this.parentTask = parentTask;
    }

    public static Builder builder() {
        return new Builder();
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
        return this.translationKey != null ? this.translationKey : (this.translationKey = Util.makeTranslationKey("task", this.getRegistryName()));
    }

    @Nonnull
    public String getDescriptionKey() {
        return this.descKey != null ? this.descKey : (this.descKey = (this.translationKey != null ? this.translationKey : getTranslationKey()) + ".desc");
    }

    @Nonnull
    public ITextComponent getTranslation() {
        return (this.translation != null ? this.translation : (this.translation = new TranslationTextComponent(this.getTranslationKey()))).shallowCopy();
    }

    @Nonnull
    public ITextComponent getDescription() {
        return (this.desc != null ? this.desc : (this.desc = new TranslationTextComponent(this.getDescriptionKey()))).shallowCopy();
    }

    public static class Builder {
        private final ImmutableList.Builder<TaskRequirement> requirements = ImmutableList.builder();
        private @Nullable IPlayableFaction<?> faction;
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
            return this.addStatRequirement(Stats.ENTITY_KILLED, entityType, amount);
        }

        public <T extends IForgeRegistryEntry<?>> Builder addStatRequirement(StatType<T> statType, T stat, int amount) {
            this.requirements.add(new StatRequirement<>(statType, stat, amount));
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
