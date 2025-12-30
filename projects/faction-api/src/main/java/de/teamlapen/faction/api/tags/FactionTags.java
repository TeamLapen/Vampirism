package de.teamlapen.faction.api.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.tags.TagKey;

public class FactionTags {

    public static final TagKey<IFaction<?>> ALL_FACTIONS = tag("all");
    public static final TagKey<IFaction<?>> NOT_NEUTRAL = tag("not_neutral");
    public static final TagKey<IFaction<?>> IS_NEUTRAL = tag("is_neutral");

    private static TagKey<IFaction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.FACTION, FIdentifier.mod(name));
    }
}
