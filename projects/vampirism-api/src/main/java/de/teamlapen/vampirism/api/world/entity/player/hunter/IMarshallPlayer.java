package de.teamlapen.vampirism.api.world.entity.player.hunter;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.factions.level.ChangeKey;
import de.teamlapen.faction.api.factions.level.change.Change;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public interface IMarshallPlayer {

    static Optional<IMarshallPlayer> get(Player player) {
        return FactionsApi.factionPlayerHandler(player).getExtension(IMarshallPlayer.class);
    }

    static Optional<IMarshallPlayer> getPresent(Player player) {
        return get(player).filter(IMarshallPlayer::isMarshall);
    }

    boolean isMarshall();

    int getSkillPoints();

    record MarshallChange() implements Change<IMarshallPlayer.MarshallChange> {
        public static final ChangeKey<IMarshallPlayer.MarshallChange> KEY = new ChangeKey<>(VIdentifier.mod("marshall"));

        @Override
        public ChangeKey<IMarshallPlayer.MarshallChange> key() {
            return KEY;
        }
    }
}
