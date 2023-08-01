package de.teamlapen.vampirism.api.settings;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Provides access to the settings api at <a href="https://api.vampirism.dev/api/v1/">api.vampirism.dev</a>
 */
public interface ISettingsProvider {

    /**
     * @param key settings key
     * @return true if the settings value exists and the value is {@code true}
     */
    @Contract(pure = true)
    boolean isSettingTrue(@NotNull String key);

    /**
     * @param key settings key
     * @return settings value for the given key
     */
    @NotNull
    @Contract(pure = true)
    Optional<String> getSettingsValue(@NotNull String key);

    @NotNull
    CompletableFuture<Optional<Collection<Supporter>>> getSupportersAsync();

    /**
     * updates the cache of the settings
     */
    void syncSettingsCache();

}
