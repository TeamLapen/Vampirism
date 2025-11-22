package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.village.IFactionVillage;
import de.teamlapen.factions.api.tags.FactionTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public interface IFaction<T extends IFactionEntity> {

    @SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
    static boolean is(@Nullable Holder<? extends IFaction<?>> first, @Nullable Holder<? extends IFaction<?>> second) {
        if (first == null) {
            return second == null;
        }
        return second != null && first.is((Holder) second);
    }

    static boolean isNeutral(@Nullable Holder<? extends IFaction<?>> holder) {
        return holder == null || is(holder, FactionTags.IS_NEUTRAL);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T extends IFaction<?>> boolean is(@Nullable Holder<? extends IFaction<?>> first, @Nullable TagKey<T> second) {
        if (first == null) {
            return second == null;
        }
        return second != null && first.is((TagKey) second);
    }

    static <T extends IFaction<?>, Z extends IFaction<?>> boolean is(TagKey<Z> first, TagKey<T> second) {
        return first.location().equals(second.location());
    }

    @SuppressWarnings("unchecked")
    static <T extends IFaction<?>> boolean contains(HolderSet<T> first, @Nullable Holder<? extends IFaction<?>> second) {
        return second != null && first.contains((Holder<T>) second);
    }

    static <T extends IFaction<?>> boolean contains(HolderSet<T> first, HolderSet<T> second) {
        return second.stream().allMatch(s -> contains(first, s));
    }

    @SuppressWarnings("unchecked")
    static <T extends IFaction<?>> boolean contains(HolderSet<T> first, TagKey<IFaction<?>> second) {
        return FactionRegistries.FACTION.get().get(second).map(set -> contains(first, (HolderSet<T>) set)).orElse(false);
    }

    /**
     * If not set returns white
     */
    TextColor getChatColor();

    /**
     * Used for some rendering, e.g. for displaying the level
     */
    int getColor();

    /**
     * @return The name of the faction
     */
    default MutableComponent getName() {
        return Component.translatable(getDescriptionId());
    }

    String getDescriptionId();

    /**
     * @return The plural name of the faction
     */
    default MutableComponent getNamePlural() {
        return Component.translatable(getDescriptionIdPlural());
    }

    String getDescriptionIdPlural();

    /**
     * Gets Village Totem related utility class
     *
     * @return the village data class
     */
    IFactionVillage getVillageData();

    /**
     * @return a tag key for the given registry associated with this faction if any is registered
     */
    <Z> Optional<TagKey<Z>> getTag(ResourceKey<? extends Registry<Z>> registryKey);
}
