package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Basic implementation of IAction<IHunterPlayer>. It is recommended to extend this
 */
public abstract class DefaultHunterAction extends DefaultAction<IHunterPlayer> {
    @Nonnull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return Optional.of(VReference.HUNTER_FACTION);
    }
}
