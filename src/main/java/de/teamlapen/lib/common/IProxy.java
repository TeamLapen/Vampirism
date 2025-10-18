package de.teamlapen.lib.common;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;


public interface IProxy {

    /**
     * @return The string describing the currently active language. "English" on server side
     */
    String getActiveLanguage();

    String getActiveLanguageCode();

    /**
     * Try to obtain the world from the given key. Null if not loaded or not accessible (on client)
     */
    @Nullable
    Level getWorldFromKey(ResourceKey<Level> dimension);
}
