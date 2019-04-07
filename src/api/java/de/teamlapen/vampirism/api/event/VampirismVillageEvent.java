package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class VampirismVillageEvent extends Event {

    @Nullable
    private final IVampirismVillage village;

    public VampirismVillageEvent(@Nullable IVampirismVillage village) {
        this.village = village;
    }

    @Nullable
    public IVampirismVillage getVillage() {
        return village;
    }


    /**
     * Fired when a new villager will be spawned.
     * Deny if none should spawn, allow and set villager if you own villager should spawn.
     * Default spawns a standard villager.
     * <p>
     * Your villager should have the position set, but should not be spawned in the world.
     * <p>
     * The willBeAggressive field tells if the villager will be converted to an aggressive version. You can change this.
     * DON'T set an aggressive villager even if this field is true
     */
    @HasResult
    public static class SpawnNewVillager extends VampirismVillageEvent {

        /**
         * Random existing villager in village
         * Used as a "seed" villager to get a valid spawn point.
         */
        private final @Nonnull
        EntityVillager seedVillager;
        private @Nullable
        EntityVillager newVillager;
        private boolean willBeVampire;

        public SpawnNewVillager(@Nonnull IVampirismVillage village, @Nonnull EntityVillager seedVillager, boolean willBeVampire) {
            super(village);
            this.seedVillager = seedVillager;
            this.willBeVampire = willBeVampire;
        }

        public EntityVillager getNewVillager() {
            return newVillager;
        }

        /**
         * The villager that should be spawned
         * The position should already be set
         *
         * @param newVillager
         */
        public void setNewVillager(EntityVillager newVillager) {
            this.newVillager = newVillager;
        }

        /**
         * A random existing villager which can be used as a seed (e.g. for the position)
         *
         * @return
         */
        @Nonnull
        public EntityVillager getSeedVillager() {
            return seedVillager;
        }

        /**
         * If the villager will be converted to a vampire version afterwards.
         * Default: Is sometimes true if the village is controlled by vampires. Can be overridden by {@link #setWillBeVampire}
         */
        public boolean isWillBeVampire() {
            return willBeVampire;
        }

        /**
         * Overwrite the default value.
         */
        public void setWillBeVampire(boolean willBeVampire) {
            this.willBeVampire = willBeVampire;
        }
    }

    /**
     * Fired when a normal villager should be converted to angry villager.
     * You can set a custom replacement and cancel this event to make it take effect.
     * The oldVillager is probably not added to a world
     */
    @Cancelable
    public static class MakeAggressive extends VampirismVillageEvent {

        private final EntityVillager oldVillager;
        private @Nullable
        IVillageCaptureEntity captureVillager;

        public MakeAggressive(@Nullable IVampirismVillage village, @Nonnull EntityVillager villager) {
            super(village);
            this.oldVillager = villager;
        }

        @Nullable
        public IVillageCaptureEntity getAggressiveVillager() {
            return captureVillager;
        }

        /**
         * @return The villager which should be made agressive
         */
        public EntityVillager getOldVillager() {
            return oldVillager;
        }

        /**
         * Set the aggressive version of the old villager.
         * Event has to be canceled for this to take effect
         *
         * @param aggressiveVillager Must extend EntityVillager
         */
        public void setAgressiveVillager(@Nullable IVillageCaptureEntity captureVillager) {
            if (captureVillager != null && !(captureVillager instanceof EntityVillager)) {
                throw new IllegalArgumentException("Aggressive villager must be a instanceof EntityVillager");
            }
            this.captureVillager = captureVillager;
        }
    }
}
