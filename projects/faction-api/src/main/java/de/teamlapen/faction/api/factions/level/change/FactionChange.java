package de.teamlapen.faction.api.factions.level.change;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.level.ChangeKey;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.Holder;

public record FactionChange(Holder<? extends IPlayableFaction<?>> newFaction) implements Change<FactionChange> {

    public static final ChangeKey<FactionChange> KEY = new ChangeKey<>(FIdentifier.mod("faction"));

    @Override
    public ChangeKey<FactionChange> key() {
        return KEY;
    }

}
