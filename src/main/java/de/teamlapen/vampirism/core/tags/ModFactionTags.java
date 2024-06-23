package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class ModFactionTags {

    public static final TagKey<IFaction<?>> HOSTILE_TOWARDS_NEUTRAL = tag("hostile_towards_neutral");
    public static final TagKey<IFaction<?>> ALL_FACTIONS = VampirismTags.Factions.ALL_FACTIONS;
    public static final TagKey<IFaction<?>> IS_HUNTER = VampirismTags.Factions.IS_HUNTER;
    public static final TagKey<IFaction<?>> IS_VAMPIRE = VampirismTags.Factions.IS_VAMPIRE;
    public static final TagKey<IFaction<?>> HAS_LORD_SKILLS = tag("has_lord_skills");

    private static @NotNull TagKey<IFaction<?>> tag(@NotNull String name) {
        return TagKey.create(VampirismRegistries.Keys.FACTION, VResourceLocation.mod(name));
    }
}
