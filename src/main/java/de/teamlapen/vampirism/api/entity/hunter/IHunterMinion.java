package de.teamlapen.vampirism.api.entity.hunter;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinion;

/**
 * A {@link IMinion} of the hunter faction
 */
public interface IHunterMinion extends IMinion, IHunterMob {

    /**
     * A {@link IHunterMinion} which is saveable ({@link ISaveableMinion}
     */
    interface Saveable extends IHunterMinion, ISaveableMinion {

    }
}
