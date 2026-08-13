package de.teamlapen.vampirism.api.settings;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Provides access to the settings api at <a href="https://api.vampirism.dev/api/v1/">api.vampirism.dev</a>
 */
public interface ISettingsProvider {


    @NotNull
    CompletableFuture<Optional<Collection<Supporter>>> getSupportersAsync();

}
