package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.IVampire;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Interface for the player vampire data
 */
public interface IVampirePlayer extends IVampire, IFactionPlayer, IMinionLord, IBiteableEntity {
    int getBloodLevel();


    /**
     * @return Whether automatically filling blood into bottles is enabled or not.
     */
    boolean isAutoFillEnabled();
    boolean isVampireLord();


    /**
     * Add an exhaustion modifier (used in blood usage)
     *
     * @param id  ID to remove it later
     * @param mod Exhaustion is multiplied with this
     */
    void addExhaustionModifier(String id, float mod);

    /**
     * Removes a modifier registered with {@link #addExhaustionModifier(String, float)}
     *
     * @param id
     */
    void removeExhaustionModifier(String id);


    /**
     * Copys all values from another player's IFactionPlayer
     *
     * @param old
     */
    void copyFrom(EntityPlayer old);

    boolean isDisguised();

    /**
     * Returns false for a null world
     *
     * @return if the player is in a remote world
     */
    boolean isRemote();

    /**
     * @return The players vampire skill handler
     */
    ISkillHandler getSkillHandler();
    /**
     * @return The bite type which would be applied to the give entity
     */
    BITE_TYPE determineBiteType(EntityLivingBase entity);

    enum BITE_TYPE {
        ATTACK, SUCK_BLOOD_CREATURE, SUCK_BLOOD_PLAYER, SUCK_BLOOD, NONE
    }
}
