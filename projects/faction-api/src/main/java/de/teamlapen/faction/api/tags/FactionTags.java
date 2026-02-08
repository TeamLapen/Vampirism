package de.teamlapen.faction.api.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.tags.TagKey;

public class FactionTags {

    /**
     * Contains all registered factions
     * <p>
     * Factions in this tag must be in either {@link #NOT_NEUTRAL} or {@link #IS_NEUTRAL}
     * <p>
     * Factions in this tag should be in either {@link #HOSTILE_TOWARDS_NEUTRAL} or {@link #FRIENDLY_TOWARDS_NEUTRAL}
     */
    public static final TagKey<IFaction<?>> ALL_FACTIONS = tag("all");
    /**
     * Contains all registered faction but the default "NEUTRAL" faction
     * Matches any creature not in the "NEUTRAL" faction
     */
    public static final TagKey<IFaction<?>> NOT_NEUTRAL = tag("not_neutral");
    /**
     * Contains only the NEUTRAL faction ->
     * Matches only creatures that do not have a faction or rather have the "NEUTRAL" faction
     */
    public static final TagKey<IFaction<?>> IS_NEUTRAL = tag("is_neutral");
    /**
     * Contains factions that generally somehow harm neutral creatures
     */
    public static final TagKey<IFaction<?>> HOSTILE_TOWARDS_NEUTRAL = tag("hostile_towards_neutral");
    /**
     * Contains factions that generally do not harm neutral creatures
     */
    public static final TagKey<IFaction<?>> FRIENDLY_TOWARDS_NEUTRAL = tag("friendly_towards_neutral");

    /**
     * Contains factions that have lord skills.
     */
    public static final TagKey<IFaction<?>> HAS_LORD_SKILLS = tag("has_lord_skills");
    /**
     * Contains factions that can trigger a raid on a village
     */
    public static final TagKey<IFaction<?>> CAN_RAID = tag("can_raid");
    /**
     * Contains factions where when holding a village, a random raid can start.
     */
    public static final TagKey<IFaction<?>> HAS_RANDOM_RAID = tag("has_random_raid");

    private static TagKey<IFaction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.FACTION, FIdentifier.mod(name));
    }
}
