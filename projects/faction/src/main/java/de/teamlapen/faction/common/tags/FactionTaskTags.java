package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.tags.TagKey;

public class FactionTaskTags {

    public static final TagKey<Task> HAS_FACTION = tag("has_faction");
    public static final TagKey<Task> IS_UNIQUE = tag("is_unique");
    public static final TagKey<Task> AWARDS_LORD_LEVEL = tag("awards_lord_level");

    private static TagKey<Task> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.TASK, FIdentifier.mod(name));
    }
}
