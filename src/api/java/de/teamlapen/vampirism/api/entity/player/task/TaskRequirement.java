package de.teamlapen.vampirism.api.entity.player.task;

import javax.annotation.Nonnull;

public interface TaskRequirement<T> {

    @Nonnull
    default Type getType() {
        return Type.BOOLEAN;
    }

    /**
     * @throws ClassCastException if Object is not applicant for the {@link #getType()}
     */
    @Nonnull
    T getStat();

    default int getAmount() {
        return 1;
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
         * based on boolean supplier
         */
        BOOLEAN
    }

}
