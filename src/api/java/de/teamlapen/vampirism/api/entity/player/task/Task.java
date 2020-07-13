package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Task extends ForgeRegistryEntry<Task> {

    @Nonnull
    private final Variant variant;
    @Nullable
    private final IPlayableFaction<?> faction;
    @Nonnull
    private final TaskRequirement requirements;
    @Nonnull
    private final TaskReward rewards;
    @Nonnull
    private final TaskUnlocker[] unlocker;
    private final boolean useDescription;
    @Nullable
    private String translationKey;
    @Nullable
    private ITextComponent translation;
    @Nullable
    private String descKey;
    @Nullable
    private ITextComponent desc;

    /**
     * translation keys used for a task are
     * <p>
     *  - {@code task.<registryname>}
     *  <p>
     *  if needed:
     *  <p>
     *  - {@code task.<registryname>.req.<requirementname>}
     *  <p>
     *  - {@code task.<registryname>.reward}
     *  <p>
     *  - {@code task.<registryname>.desc}
     *
     * @param variant        the task variant
     * @param faction        the faction that can complete the task. if {@code null} all faction are able to complete the task
     * @param requirements   the requirements to acquire the task completion
     * @param rewards        the rewards upon task completion
     * @param unlocker       the unlocker to unlock the task for completion
     * @param useDescription whether the task should display a description of not
     */
    public Task(@Nonnull Variant variant, @Nullable IPlayableFaction<?> faction, @Nonnull TaskRequirement requirements, @Nonnull TaskReward rewards, @Nonnull TaskUnlocker[] unlocker, boolean useDescription) {
        this.variant = variant;
        this.faction = faction;
        this.requirements = requirements;
        this.useDescription = useDescription;
        this.rewards = rewards;
        this.unlocker = unlocker;
    }

    public boolean isUnique() {
        return variant == Variant.UNIQUE;
    }

    @Nullable
    public IPlayableFaction<?> getFaction() {
        return faction;
    }

    @Nonnull
    public TaskRequirement getRequirement() {
        return this.requirements;
    }

    @Nonnull
    public TaskReward getReward() {
        return this.rewards;
    }

    @Nonnull
    public TaskUnlocker[] getUnlocker() {
        return unlocker;
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

    public enum Variant {
        /**
         * tasks that can be completed multiple times
         */
        REPEATABLE,
        /**
         * task that can only be completed once
         */
        UNIQUE
    }
}
