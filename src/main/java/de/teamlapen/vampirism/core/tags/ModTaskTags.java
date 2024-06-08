package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

public class ModTaskTags {

    public static final TagKey<Task> HAS_FACTION = tag("has_faction");
    public static final TagKey<Task> IS_VAMPIRE = tag("has_faction/vampire");
    public static final TagKey<Task> IS_HUNTER = tag("has_faction/hunter");
    public static final TagKey<Task> IS_UNIQUE = tag("is_unique");
    public static final TagKey<Task> AWARDS_LORD_LEVEL = tag("awards_lord_level");

    private static @NotNull TagKey<Task> tag(@NotNull String name) {
        return TagKey.create(VampirismRegistries.Keys.TASK, new ResourceLocation(REFERENCE.MODID, name));
    }
}
