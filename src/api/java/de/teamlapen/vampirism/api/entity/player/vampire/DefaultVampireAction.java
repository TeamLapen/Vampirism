package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommended to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {
    @NotNull
    @Override
    public Optional<IPlayableFaction<?>> getFaction() {
        return Optional.of(VReference.VAMPIRE_FACTION);
    }
}
