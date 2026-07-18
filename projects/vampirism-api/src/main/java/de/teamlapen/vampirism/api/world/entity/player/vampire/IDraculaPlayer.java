package de.teamlapen.vampirism.api.world.entity.player.vampire;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface IDraculaPlayer extends IPlayer, IWingsEntity {

    static Optional<IDraculaPlayer> getDracula(Player player) {
        return FactionsApi.factionPlayerHandler(player).getCurrentFactionPlayer().map(x -> x instanceof IDraculaPlayer p ? p : null).filter(IDraculaPlayer::isDracula);
    }

    boolean isDracula();

    void makeDracula();

    int getDraculaSkillPoints();

    record DraculaChange() implements LevelingChange.Change<DraculaChange> {
        public static final LevelingChange.Key<DraculaChange> KEY = new LevelingChange.Key<>(VIdentifier.mod("dracula"));

        @Override
        public LevelingChange.Key<DraculaChange> key() {
            return KEY;
        }
    }
}
