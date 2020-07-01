package de.teamlapen.vampirism.api.entity.player.task;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface TaskRequirement<T> {

    @Nonnull
    default Type getType() {
        return Type.BOOLEAN;
    }

    /**
     * @throws ClassCastException if Object is not applicant for the {@link #getType()}
     * @return the stat the needs to be achieved with {@link #getAmount()} to complete the requirement
     */
    @Nonnull
    T getStat(IFactionPlayer<?> player);

    /**
     * @return the needed amount of the {@link #getStat(IFactionPlayer)} to complete this requirement
     */
    default int getAmount(IFactionPlayer<?> player) {
        return 1;
    }

    /**
     * if needed removes the requirements from the player upon task completion
     *
     * @param player the player which completed the task
     */
    default void removeRequirement(IFactionPlayer<?> player) {
    }

    @SuppressWarnings("JavadocReference")
    enum Type {
        /**
         * based on {@link net.minecraft.stats.Stats.CUSTOM} stat increase
         */
        STATS,
        /**
         * based on item in inventory
         */
        ITEMS,
        /**
         * based on {@link net.minecraft.stats.Stats.ENTITY_KILLED} stat increased
         */
        ENTITY,
        /**
         * based on {@link net.minecraft.stats.Stats.ENTITY_KILLED} stat increased, but for multiple entities.
         */
        ENTITY_TAG,
        /**
         * based on boolean supplier
         */
        BOOLEAN
    }

}
