package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityLivingBase;

/**
 * Interface for the player vampire data
 */
public interface IVampirePlayer extends IVampire, IFactionPlayer, IMinionLord, IBiteableEntity {

    /**
     * @return The bite type which would be applied to the give entity
     */
    BITE_TYPE determineBiteType(EntityLivingBase entity);

    int getBloodLevel();

    /**
     * @return The players vampire skill handler
     */
    ISkillHandler getSkillHandler();


    /**
     * @return The amount of ticks the player has been in sun. Never higher than 100
     */
    int getTicksInSun();

    /**
     * @return Whether automatically filling blood into bottles is enabled or not.
     */
    boolean isAutoFillEnabled();

    boolean isVampireLord();


    enum BITE_TYPE {
        ATTACK, SUCK_BLOOD_CREATURE, SUCK_BLOOD_PLAYER, SUCK_BLOOD, NONE
    }
}
