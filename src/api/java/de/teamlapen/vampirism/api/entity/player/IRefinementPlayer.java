package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.entity.player.skills.IRefinementHandler;

public interface IRefinementPlayer<T extends IFactionPlayer<T> & IRefinementPlayer<T>> extends IFactionPlayer<T> {

    IRefinementHandler<T> getRefinementHandler();
}
