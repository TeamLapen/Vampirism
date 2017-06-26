package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.entity.IVampirismEntity;

/**
 * Interface for all non player vampires
 */
public interface IVampireMob extends IVampire, IVampirismEntity {


    boolean wantsBlood();
}
