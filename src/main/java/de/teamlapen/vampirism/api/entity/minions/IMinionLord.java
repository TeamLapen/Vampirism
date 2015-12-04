package de.teamlapen.vampirism.api.entity.minions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.UUID;

/**
 * Interface for classes that can control minions. E.g. VampirePlayer
 */
public interface IMinionLord {

    /**
     * @return The absolute worldtime when the lord send the last comeback call
     */
    long getLastComebackCall();

    /**
     *
     * @return The maximum amount of minion the lord can control
     */
    int getMaxMinionCount();

    //SaveableMinionHandler getMinionHandler();

    /**
     * @return The target the lord's minions should attack, can be null
     */
    EntityLivingBase getMinionTarget();

    /**
     * The Entity representing this lord. Can be the same as this object (e.g. VampireLord) or something else (e.g. VampirePlayer)
     *
     * @return
     */
    EntityLivingBase getRepresentingEntity();

    /**
     * @param e
     * @return The squared distance to the given entity
     */
    double getTheDistanceSquared(Entity e);

    /**
     * Entity's uuid
     *
     * @return
     */
    UUID getThePersistentID();

    boolean isTheEntityAlive();
}
