package de.teamlapen.vampirism.api.entity.vampire;

import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.api.entity.IEntityLeader;
import net.minecraft.world.entity.monster.Enemy;

/**
 * Vampire that spawns in the vampire forest, has minions and drops pure blood
 */
public interface IVampireBaron extends IVampireMob, IAdjustableLevel, Enemy, IEntityLeader {
}
