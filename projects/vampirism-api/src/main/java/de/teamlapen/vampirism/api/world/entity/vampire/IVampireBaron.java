package de.teamlapen.vampirism.api.world.entity.vampire;

import de.teamlapen.factions.api.world.entities.IEntityLeader;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import net.minecraft.world.entity.monster.Enemy;

/**
 * Vampire that spawns in the vampire forest, has minions and drops pure blood
 */
public interface IVampireBaron extends IVampireMob, IAdjustableLevel, Enemy, IEntityLeader {
}
