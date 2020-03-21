package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class Task extends ForgeRegistryEntry<Task> {

    private final @Nonnull Variant variant;
    private final @Nullable IPlayableFaction<?> faction;
    private final @Nonnull TaskRequirement<?> requirements;
    private final @Nonnull TaskReward rewards;
    private final @Nonnull TaskUnlocker[] unlocker;
    private @Nullable String translationKey;
    private @Nullable String descKey;
    private final boolean useDescription;
    private @Nullable ITextComponent translation;
    private @Nullable ITextComponent desc;

    public Task(@Nonnull Variant variant, @Nullable IPlayableFaction<?> faction, @Nonnull TaskRequirement<?> requirements, @Nonnull TaskReward rewards, @Nonnull TaskUnlocker[] unlocker, boolean useDescription) {
        this.variant = variant;
        this.faction = faction;
        this.requirements = requirements;
        this.useDescription = useDescription;
        this.rewards = rewards;
        this.unlocker = unlocker;
    }

    @Nonnull
    public Variant getVariant() {
        return variant;
    }

    @Nullable
    public IPlayableFaction<?> getFaction() {
        return faction;
    }

    @Nonnull
    public TaskRequirement<?> getRequirement() {
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
        REPEATABLE, UNIQUE
    }
}
