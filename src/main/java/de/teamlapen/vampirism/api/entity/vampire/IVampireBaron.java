package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.minions.IMinionLordWithSaveable;

/**
 * Vampire that spawns in the vampire forest, has minions and drops pure blood
 */
public interface IVampireBaron extends IVampireMob, IMinionLordWithSaveable<IVampireMinion.Saveable>, IAdjustableLevel {
}
