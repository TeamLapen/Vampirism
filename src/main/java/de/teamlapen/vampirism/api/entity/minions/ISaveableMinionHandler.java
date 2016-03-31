package de.teamlapen.vampirism.api.entity.minions;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;

/**
 * Used in {@link IMinionLord} classes to manage their {@link ISaveableMinion}s
 *
 * @author Maxanier
 */
public interface ISaveableMinionHandler<T extends ISaveableMinion> {

    /**
     * Removes all minions are either dead or have found another lord
     */
    void checkMinions();

    /**
     * Returns a predicate which only accepts EntityLivingBases which are not minions of this lord or the lord itself
     *
     * @return
     */
    Predicate<EntityLivingBase> getNonMinionSelector();

    int getMinionCount();

    /**
     * @return How many minions the lord is allowed to have in addition to the current ones
     */
    int getLeftMinionSlots();


    /**
     * Kills all minions.
     *
     * @param instant If true, the entity is immediately set dead and no death animation is played
     */
    void killMinions(boolean instant);



    /**
     * Registers a minion to be saved
     *
     * @param m
     * @param force If the max minion count should be ignored
     */
    void registerMinion(T m, boolean force);

    void teleportMinionsToLord();

    void unregisterMinion(T m);

    /**
     * Spawn the loaded (from NBT) minions to the lords world
     */
    void addLoadedMinions();
}
