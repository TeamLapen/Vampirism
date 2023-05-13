package de.teamlapen.vampirism.api.entity.player.runnable;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;

public interface IPlayerRunnable<T extends IFactionPlayer<T>> {

    /**
     * is initially called when the runnable is dispatched
     */
    void setUp(T factionPlayer);

    /**
     * Is called every update tick
     *
     * @return true if the task is finished
     */
    boolean run(T factionPlayer);

    /**
     * is called when the runnable is finished
     */
    void shutdown(T factionPlayer);
}
