package de.teamlapen.vampirism.api.entity.player.task;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class Task extends ForgeRegistryEntry<Task> {

    private final @Nullable IPlayableFaction<?> faction;
    private final @Nullable Supplier<Task> parentTask;
    private final @Nonnull ImmutableList<TaskRequirement<?>> requirements;
    private final @Nonnull ImmutableList<TaskReward> rewards;
    private @Nullable String translationKey;
    private @Nullable String descKey;
    private final boolean useDescription;
    private @Nullable ITextComponent translation;
    private @Nullable ITextComponent desc;

    public Task(@Nullable IPlayableFaction<?> faction, @Nonnull ImmutableList<TaskRequirement<?>> requirements, @Nonnull ImmutableList<TaskReward> rewards, @Nullable Supplier<Task> parentTask, boolean useDescription) {
        this.faction = faction;
        this.requirements = requirements;
        this.parentTask = parentTask;
        this.useDescription = useDescription;
        this.rewards = rewards;
    }

    @Nullable
    public IPlayableFaction<?> getFaction() {
        return faction;
    }

    @Nonnull
    public ImmutableList<TaskRequirement<?>> getRequirements() {
        return this.requirements;
    }

    @Nonnull
    public ImmutableList<TaskReward> getRewards() {
        return this.rewards;
    }

    @Nullable
    public Task getParentTask() {
        return parentTask == null ? null : parentTask.get();
    }

    public boolean requireParent() {
        return parentTask != null;
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

    public boolean useDescription() {
        return useDescription;
    }

}
