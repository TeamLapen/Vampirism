package de.teamlapen.faction.api.factions.level.change;

import de.teamlapen.faction.api.factions.level.ChangeKey;
import de.teamlapen.faction.api.util.FIdentifier;

public record LordLevelChange(int newLevel) implements Change<LordLevelChange> {
    public static final ChangeKey<LordLevelChange> KEY = new ChangeKey<>(FIdentifier.mod("lord"));

    @Override
    public ChangeKey<LordLevelChange> key() {
        return KEY;
    }
}
