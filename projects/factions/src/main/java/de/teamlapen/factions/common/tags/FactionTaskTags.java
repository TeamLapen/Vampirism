package de.teamlapen.factions.common.tags;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.tasks.Task;
import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.tags.TagKey;

public class FactionTaskTags {

    public static final TagKey<Task> HAS_FACTION = tag("has_faction");
    public static final TagKey<Task> IS_UNIQUE = tag("is_unique");
    public static final TagKey<Task> AWARDS_LORD_LEVEL = tag("awards_lord_level");

    private static TagKey<Task> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.TASK, FResourceLocation.mod(name));
    }
}
