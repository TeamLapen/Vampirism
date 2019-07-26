package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.player.actions.DefaultAction;

/**
 * Basic implementation of IAction<IHunterPlayer>. It is recommend to extend this
 */
public abstract class DefaultHunterAction extends DefaultAction<IHunterPlayer> {
    public DefaultHunterAction() {
        super(VReference.HUNTER_FACTION);
    }


}
