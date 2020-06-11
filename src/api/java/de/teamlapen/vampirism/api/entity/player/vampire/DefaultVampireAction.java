package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;

/**
 * Basic implementation of IAction<IVampirePlayer>. It is recommend to extend this
 */
public abstract class DefaultVampireAction extends DefaultAction<IVampirePlayer> {
    public DefaultVampireAction() {
        super(VReference.VAMPIRE_FACTION);
    }
}
