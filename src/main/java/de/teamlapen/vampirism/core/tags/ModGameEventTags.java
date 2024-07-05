package de.teamlapen.vampirism.core.tags;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class ModGameEventTags {

    public static final TagKey<GameEvent> DARK_STALKER_IGNORE = tag("dark_stalker_ignore");

    private static @NotNull TagKey<GameEvent> tag(@NotNull String name) {
        return TagKey.create(Registries.GAME_EVENT, VResourceLocation.mod(name));
    }
}
