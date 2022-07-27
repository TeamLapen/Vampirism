package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommended to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {
    @Nonnull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return Optional.of(VReference.VAMPIRE_FACTION);
    }
}
