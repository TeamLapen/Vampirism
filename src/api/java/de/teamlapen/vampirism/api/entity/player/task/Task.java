package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Task {

    @NotNull
    private final Variant variant;
    @NotNull
    private final Supplier<IPlayableFaction<?>> faction;
    @NotNull
    private final TaskRequirement requirements;
    @NotNull
    private final TaskReward rewards;
    @NotNull
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
    public Task(@NotNull Variant variant, @Nullable IPlayableFaction<?> faction, @NotNull TaskRequirement requirements, @NotNull TaskReward rewards, @NotNull TaskUnlocker[] unlocker, boolean useDescription) {
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
    public Task(@NotNull Variant variant, @NotNull Supplier<IPlayableFaction<?>> faction, @NotNull TaskRequirement requirements, @NotNull TaskReward rewards, @NotNull TaskUnlocker[] unlocker, boolean useDescription) {
        this.variant = variant;
        this.faction = faction;
        this.requirements = requirements;
        this.useDescription = useDescription;
        this.rewards = rewards;
        this.unlocker = unlocker;
    }

    @NotNull
    public Component getDescription() {
        return this.desc != null ? this.desc : (this.desc = Component.translatable(this.getDescriptionKey()));
    }

    @NotNull
    public String getDescriptionKey() {
        return this.descKey != null ? this.descKey : (this.descKey = (this.translationKey != null ? this.translationKey : getTranslationKey()) + ".desc");
    }

    @Nullable
    public IPlayableFaction<?> getFaction() {
        return this.faction.get();
    }

    @NotNull
    public TaskRequirement getRequirement() {
        return this.requirements;
    }

    @NotNull
    public TaskReward getReward() {
        return this.rewards;
    }

    @NotNull
    public Component getTranslation() {
        return this.translation != null ? this.translation : (this.translation = Component.translatable(this.getTranslationKey()));
    }

    @NotNull
    public String getTranslationKey() {
        return this.translationKey != null ? this.translationKey : (this.translationKey = Util.makeDescriptionId("task", this.getRegistryName()));
    }

    @NotNull
    public TaskUnlocker[] getUnlocker() {
        return unlocker;
    }

    public boolean isUnique() {
        return variant == Variant.UNIQUE;
    }

    public boolean useDescription() {
        return useDescription;
    }

    private @Nullable ResourceLocation getRegistryName() {
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
