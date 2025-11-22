package de.teamlapen.factions.api.entities.minion;

/**
 * This identifies a task that can not be used as a global task, and can only be selected as task for a specific minion
 */
public interface INoGlobalCommandTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends IMinionTask<T, Q> {
}
