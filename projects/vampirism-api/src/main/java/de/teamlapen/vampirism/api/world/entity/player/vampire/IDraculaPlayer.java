package de.teamlapen.vampirism.api.world.entity.player.vampire;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface IDraculaPlayer extends IPlayer, IWingsEntity {

    static Optional<IDraculaPlayer> getDracula(Player player) {
        if (FactionsApi.factionPlayerHandler(player).factionPlayer() instanceof IDraculaPlayer draculaPlayer) {
            return Optional.of(draculaPlayer).filter(IDraculaPlayer::isDracula);
        }
        return Optional.empty();
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
