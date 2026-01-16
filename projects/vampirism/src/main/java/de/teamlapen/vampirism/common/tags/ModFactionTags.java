package de.teamlapen.vampirism.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.common.tags.FactionTags;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.tags.TagKey;

public class ModFactionTags {
    public static final TagKey<IFaction<?>> HOSTILE_TOWARDS_NEUTRAL = FactionTags.HOSTILE_TOWARDS_NEUTRAL;
    public static final TagKey<IFaction<?>> FRIENDLY_TOWARDS_NEUTRAL = FactionTags.FRIENDLY_TOWARDS_NEUTRAL;
    public static final TagKey<IFaction<?>> ALL_FACTIONS = FactionTags.ALL_FACTIONS;
    public static final TagKey<IFaction<?>> IS_HUNTER = VampirismTags.Factions.IS_HUNTER;
    public static final TagKey<IFaction<?>> IS_VAMPIRE = VampirismTags.Factions.IS_VAMPIRE;
    public static final TagKey<IFaction<?>> IS_NEUTRAL = FactionTags.IS_NEUTRAL;
    public static final TagKey<IFaction<?>> HAS_LORD_SKILLS = FactionTags.HAS_LORD_SKILLS;
    public static final TagKey<IFaction<?>> CAN_RAID = FactionTags.CAN_RAID;
    public static final TagKey<IFaction<?>> HAS_RANDOM_RAID = FactionTags.HAS_RANDOM_RAID;

    public static final TagKey<IFaction<?>> USE_GARLIC_BREAD = tag("can_use/garlic_bread");

    public static final TagKey<IFaction<?>> HUNTER_MINION_TARGETS = tag("target/hunter_minion");
    public static final TagKey<IFaction<?>> VAMPIRE_MINION_TARGETS = tag("target/vampire_minion");

    private static TagKey<IFaction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.FACTION, VIdentifier.mod(name));
    }
}
