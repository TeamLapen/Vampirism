package de.teamlapen.vampirism.api;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.tags.TagKey;

public class VampirismTags {

    public static class Factions {
        public static final TagKey<IFaction<?>> IS_HUNTER = tag("is_hunter");
        public static final TagKey<IFaction<?>> IS_VAMPIRE = tag("is_vampire");

        private static TagKey<IFaction<?>> tag(String name) {
            return TagKey.create(FactionRegistries.Keys.FACTION, VResourceLocation.mod(name));
        }

    }
}
