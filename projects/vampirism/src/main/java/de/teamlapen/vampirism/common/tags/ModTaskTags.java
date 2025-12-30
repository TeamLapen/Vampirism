package de.teamlapen.vampirism.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.common.tags.FactionTaskTags;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.tags.TagKey;

public class ModTaskTags {
    public static final TagKey<Task> HAS_FACTION = FactionTaskTags.HAS_FACTION;
    public static final TagKey<Task> IS_VAMPIRE = tag("has_faction/vampire");
    public static final TagKey<Task> IS_HUNTER = tag("has_faction/hunter");
    public static final TagKey<Task> IS_UNIQUE = FactionTaskTags.IS_UNIQUE;
    public static final TagKey<Task> AWARDS_LORD_LEVEL = FactionTaskTags.AWARDS_LORD_LEVEL;

    private static TagKey<Task> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.TASK, VIdentifier.mod(name));
    }
}
