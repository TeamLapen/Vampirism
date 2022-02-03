package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class Task {

    @Nonnull
    private final Variant variant;
    @Nonnull
    private final Supplier<IPlayableFaction<?>> faction;
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
    private Component translation;
    @Nullable
    private String descKey;
    @Nullable
    private Component desc;

    /**
     * @deprecated use {@link #Task(Variant, Supplier, TaskRequirement, TaskReward, TaskUnlocker[], boolean)}
     */
    @Deprecated
    public Task(@Nonnull Variant variant, @Nullable IPlayableFaction<?> faction, @Nonnull TaskRequirement requirements, @Nonnull TaskReward rewards, @Nonnull TaskUnlocker[] unlocker, boolean useDescription) {
        this(variant, () -> faction, requirements, rewards, unlocker, useDescription);
    }
    /**
     * translation keys used for a task are
     * <p>
     * - {@code task.<registryname>}
     * <p>
     * if needed:
     * <p>
     * - {@code task.<registryname>.req.<requirementname>}
     * <p>
     * - {@code task.<registryname>.reward}
     * <p>
     * - {@code task.<registryname>.desc}
     *
     * @param variant        the task variant
     * @param faction        the faction that can complete the task. if {@code null} all faction are able to complete the task
     * @param requirements   the requirements to acquire the task completion
     * @param rewards        the rewards upon task completion
     * @param unlocker       the unlocker to unlock the task for completion
     * @param useDescription whether the task should display a description of not
     */
    public Task(@Nonnull Variant variant, @Nonnull Supplier<IPlayableFaction<?>> faction, @Nonnull TaskRequirement requirements, @Nonnull TaskReward rewards, @Nonnull TaskUnlocker[] unlocker, boolean useDescription) {
        this.variant = variant;
        this.faction = faction;
        this.requirements = requirements;
        this.useDescription = useDescription;
        this.rewards = rewards;
        this.unlocker = unlocker;
    }

    @Nonnull
    public Component getDescription() {
        return this.desc != null ? this.desc : (this.desc = Component.translatable(this.getDescriptionKey()));
    }

    @Nonnull
    public String getDescriptionKey() {
        return this.descKey != null ? this.descKey : (this.descKey = (this.translationKey != null ? this.translationKey : getTranslationKey()) + ".desc");
    }

    @Nullable
    public IPlayableFaction<?> getFaction() {
        return this.faction.get();
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
    public Component getTranslation() {
        return this.translation != null ? this.translation : (this.translation = Component.translatable(this.getTranslationKey()));
    }

    @Nonnull
    public String getTranslationKey() {
        return this.translationKey != null ? this.translationKey : (this.translationKey = Util.makeDescriptionId("task", this.getRegistryName()));
    }

    @Nonnull
    public TaskUnlocker[] getUnlocker() {
        return unlocker;
    }

    public boolean isUnique() {
        return variant == Variant.UNIQUE;
    }

    public boolean useDescription() {
        return useDescription;
    }

    private ResourceLocation getRegistryName() {
        return VampirismRegistries.TASKS.get().getKey(this);
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
