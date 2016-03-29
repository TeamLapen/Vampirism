package de.teamlapen.vampirism.api.entity.minions;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Interface for an entity that can serve as minion for a {@link IMinionLord}
 * The implementing class has to be an {@link net.minecraft.entity.EntityLivingBase}
 */
public interface IMinion {

    /**
     * Activates the given command
     *
     * @param command
     */
    void activateMinionCommand(IMinionCommand command);

    /**
     * Returns the id of the active command. Can be -1 if none is active
     */
    @SideOnly(Side.CLIENT)
    int getActiveCommandId();

    /**
     * @return The list of available minion commands
     */
    ArrayList<IMinionCommand> getAvailableCommands();

    /**
     * @param id
     * @return The minion command represented by the given id
     */
    IMinionCommand getCommand(int id);

    /**
     * The returned EntityLiving has to implement {@link IMinionLord}
     *
     * @return The boss or null if none exist
     */
    @Nullable
    IMinionLord getLord();


    /**
     * Sets the boss
     *
     * @param b Has to implement {@link IMinionLord}
     */
    void setLord(IMinionLord b);

}