package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;

import javax.annotation.Nonnull;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommend to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {
    @Nonnull
    @Override
    public IPlayableFaction getFaction() {
        return VReference.VAMPIRE_FACTION;
    }
}
