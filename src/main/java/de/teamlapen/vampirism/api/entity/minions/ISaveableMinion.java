package de.teamlapen.vampirism.api.entity.minions;

import java.util.List;

/**
 * {@link IMinion} that is saved in the lord's nbt tag, and is always related to the minion
 */
public interface ISaveableMinion extends IMinion {

    /**
     * Return a list that contains all commands that can be triggered remotely
     * Can be the same as {@link #getAvailableCommands(IMinionLord)}
     *
     * @param lord
     * @return
     */
    List<IMinionCommand> getAvailableRemoteCommands(IMinionLord lord);
}
