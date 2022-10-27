package de.teamlapen.vampirism.api.entity;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface VampireBookLootProvider {

    @NotNull Optional<String> getBookLootId();
}
