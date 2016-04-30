package de.teamlapen.vampirism.api.entity.player.vampire;

import de.teamlapen.vampirism.api.entity.IBiteableEntity;
import de.teamlapen.vampirism.api.entity.minions.IMinionLord;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for the player vampire data.
 * Attached to all players as capability
 */
public interface IVampirePlayer extends IVampire, IFactionPlayer<IVampirePlayer>, IMinionLord, IBiteableEntity {

    /**
     * Force enables the vision
     * Does NOT unlock the vision
     * @param vision Null to disable all
     */
    void activateVision(@Nullable IVampireVision vision);

    /**
     * @return The bite type which would be applied to the give entity
     */
    BITE_TYPE determineBiteType(EntityLivingBase entity);

    /**
     * @return The players vampire skill handler
     */
    IActionHandler<IVampirePlayer> getActionHandler();

    /**
     * @return The currently active vision. May be null
     */
    @Nullable
    IVampireVision getActiveVision();

    int getBloodLevel();

    /**
     * @return The amount of ticks the player has been in sun. Never higher than 100
     */
    int getTicksInSun();

    /**
     * @return Whether automatically filling blood into bottles is enabled or not.
     */
    boolean isAutoFillEnabled();

    boolean isVampireLord();

    /**
     * Try to sleep at the given location during daytime
     *
     * @param pos
     * @return
     */
    EntityPlayer.EnumStatus trySleep(BlockPos pos);

    /**
     * Locks the vision again, preventing the player from using it
     * @param vision
     */
    void unUnlockVision(@Nonnull IVampireVision vision);

    /**
     * Unlocks the given vision, so the player can activate it.
     * Is not saved to nbt
     *
     * @param vision
     */
    void unlockVision(@Nonnull IVampireVision vision);

    /**
     * Wake up the player if he is sleeping in a coffin
     *
     * @param immediately
     * @param updateWorldFlag
     * @param setSpawn
     */
    void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn);
    enum BITE_TYPE {
        ATTACK, SUCK_BLOOD_CREATURE, SUCK_BLOOD_PLAYER, SUCK_BLOOD_PLAYER_POISONOUS, SUCK_BLOOD, NONE
    }
}
