package de.teamlapen.factions.common.tags;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.tags.TagKey;

public class FactionTags {

    public static final TagKey<IFaction<?>> HOSTILE_TOWARDS_NEUTRAL = tag("hostile_towards_neutral");
    public static final TagKey<IFaction<?>> ALL_FACTIONS = de.teamlapen.factions.api.tags.FactionTags.ALL_FACTIONS;
    public static final TagKey<IFaction<?>> IS_NEUTRAL = de.teamlapen.factions.api.tags.FactionTags.IS_NEUTRAL;

    public static final TagKey<IFaction<?>> HAS_LORD_SKILLS = tag("has_lord_skills");
    public static final TagKey<IFaction<?>> CAN_RAID = tag("can_raid");
    public static final TagKey<IFaction<?>> HAS_RANDOM_RAID = tag("has_random_raid");

    private static TagKey<IFaction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.FACTION, FResourceLocation.mod(name));
    }
}
