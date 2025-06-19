package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;

public class ModFactionTags {
    public static final TagKey<IFaction<?>> HOSTILE_TOWARDS_NEUTRAL = tag("hostile_towards_neutral");
    public static final TagKey<IFaction<?>> ALL_FACTIONS = VampirismTags.Factions.ALL_FACTIONS;
    public static final TagKey<IFaction<?>> IS_HUNTER = VampirismTags.Factions.IS_HUNTER;
    public static final TagKey<IFaction<?>> IS_VAMPIRE = VampirismTags.Factions.IS_VAMPIRE;
    public static final TagKey<IFaction<?>> IS_NEUTRAL = VampirismTags.Factions.IS_NEUTRAL;
    public static final TagKey<IFaction<?>> HAS_LORD_SKILLS = tag("has_lord_skills");
    public static final TagKey<IFaction<?>> CAN_RAID = tag("can_raid");
    public static final TagKey<IFaction<?>> HAS_RANDOM_RAID = tag("has_random_raid");

    public static final TagKey<IFaction<?>> USE_GARLIC_BREAD = tag("can_use/garlic_bread");

    private static TagKey<IFaction<?>> tag(String name) {
        return TagKey.create(VampirismRegistries.Keys.FACTION, VResourceLocation.mod(name));
    }
}
