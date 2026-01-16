package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.tags.TagKey;

public class FactionTags {

    public static final TagKey<IFaction<?>> ALL_FACTIONS = de.teamlapen.faction.api.tags.FactionTags.ALL_FACTIONS;
    public static final TagKey<IFaction<?>> NOT_NEUTRAL = de.teamlapen.faction.api.tags.FactionTags.NOT_NEUTRAL;
    public static final TagKey<IFaction<?>> IS_NEUTRAL = de.teamlapen.faction.api.tags.FactionTags.IS_NEUTRAL;
    /**
     * Contains factions that generally somehow harm neutral creatures
     */
    public static final TagKey<IFaction<?>> HOSTILE_TOWARDS_NEUTRAL = tag("hostile_towards_neutral");
    /**
     * Contains factions that generally do not harm neutral creatures
     */
    public static final TagKey<IFaction<?>> FRIENDLY_TOWARDS_NEUTRAL = tag("friendly_towards_neutral");

    public static final TagKey<IFaction<?>> HAS_LORD_SKILLS = tag("has_lord_skills");
    public static final TagKey<IFaction<?>> CAN_RAID = tag("can_raid");
    public static final TagKey<IFaction<?>> HAS_RANDOM_RAID = tag("has_random_raid");

    private static TagKey<IFaction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.FACTION, FIdentifier.mod(name));
    }
}
