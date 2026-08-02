package de.teamlapen.faction.api.factions.level.change;

import de.teamlapen.faction.api.factions.level.ChangeKey;
import de.teamlapen.faction.api.util.FIdentifier;

public record LevelChange(int newLevel) implements Change<LevelChange> {
    public static final ChangeKey<LevelChange> KEY = new ChangeKey<>(FIdentifier.mod("level"));

    public ChangeKey<LevelChange> key() {
        return KEY;
    }
}
