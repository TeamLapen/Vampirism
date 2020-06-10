package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;

import javax.annotation.Nonnull;

/**
 * Basic implementation of IAction<IHunterPlayer>. It is recommend to extend this
 */
public abstract class DefaultHunterAction extends DefaultAction<IHunterPlayer> {
    @Nonnull
    @Override
    public IPlayableFaction getFaction() {
        return VReference.HUNTER_FACTION;
    }
}
