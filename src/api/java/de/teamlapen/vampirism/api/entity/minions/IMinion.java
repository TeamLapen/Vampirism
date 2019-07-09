package de.teamlapen.vampirism.api.entity.minions;

import net.minecraft.entity.CreatureEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Interface for an entity that can serve as minion for a {@link IMinionLord}
 * The implementing class has to be an {@link CreatureEntity}
 */
public interface IMinion {

    /**
     * Activates the given command
     *
     * @param command
     */
    void activateMinionCommand(IMinionCommand command);


    /**
     * All commands should have unique ids
     *
     * @return The list of available minion commands
     */
    ArrayList<IMinionCommand> getAvailableCommands(IMinionLord lord);

    /**
     * @param id
     * @return The minion command represented by the given id
     */
    IMinionCommand getCommand(int id);

    /**
     * @return The boss or null if none exist
     */
    @Nullable
    IMinionLord getLord();


    /**
     * Sets the lord
     *
     * @param lord Has to implement {@link IMinionLord}
     */
    void setLord(IMinionLord lord);

}