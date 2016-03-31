package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.ISaveableMinion;

/**
 * A {@link IMinion} of the vampire faction
 */
public interface IVampireMinion extends IMinion, IVampireMob {
    /**
     * A {@link IVampireMinion} which is saveable ({@link ISaveableMinion}
     */
    interface Saveable extends IVampireMinion, ISaveableMinion {

    }
}
