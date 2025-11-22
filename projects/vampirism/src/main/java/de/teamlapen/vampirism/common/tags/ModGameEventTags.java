package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.GameEvent;

public class ModGameEventTags {
    public static final TagKey<GameEvent> DARK_STALKER_IGNORE = tag("dark_stalker_ignore");

    private static TagKey<GameEvent> tag(String name) {
        return TagKey.create(Registries.GAME_EVENT, VResourceLocation.mod(name));
    }
}
