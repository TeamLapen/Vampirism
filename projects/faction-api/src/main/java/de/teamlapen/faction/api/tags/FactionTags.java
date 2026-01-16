package de.teamlapen.faction.api.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.tags.TagKey;

public class FactionTags {

    public static final TagKey<IFaction<?>> ALL_FACTIONS = tag("all");
    /**
     * Contains all registered faction but the default "NEUTRAL" faction
     * Matches any creature that is not in the "NEUTRAL" faction
     */
    public static final TagKey<IFaction<?>> NOT_NEUTRAL = tag("not_neutral");
    /**
     * Contains only the NEUTRAL faction ->
     * Matches only creatures that do not have a faction or rather have the "NEUTRAL" faction
     */
    public static final TagKey<IFaction<?>> IS_NEUTRAL = tag("is_neutral");

    private static TagKey<IFaction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.FACTION, FIdentifier.mod(name));
    }
}
