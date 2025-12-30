package de.teamlapen.vampirism.api;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.tags.TagKey;

public class VampirismTags {

    public static class Factions {
        public static final TagKey<IFaction<?>> IS_HUNTER = tag("is_hunter");
        public static final TagKey<IFaction<?>> IS_VAMPIRE = tag("is_vampire");

        private static TagKey<IFaction<?>> tag(String name) {
            return TagKey.create(FactionRegistries.Keys.FACTION, VIdentifier.mod(name));
        }

    }
}
