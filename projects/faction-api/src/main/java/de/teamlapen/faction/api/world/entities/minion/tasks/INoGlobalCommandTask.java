package de.teamlapen.faction.api.world.entities.minion.tasks;

import de.teamlapen.faction.api.world.entities.minion.IMinionData;

/**
 * This identifies a task that cannot be used as a global task and can only be selected as a task for a specific minion
 */
public interface INoGlobalCommandTask<T extends IMinionTask.IMinionTaskState<Q>, Q extends IMinionData> extends IMinionTask<T, Q> {
}
