package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.IAggressiveVillager;
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
        private boolean willBeAggressive;

        public SpawnNewVillager(@Nonnull IVampirismVillage village, @Nonnull EntityVillager seedVillager, boolean willBeAggressive) {
            super(village);
            this.seedVillager = seedVillager;
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
         * If the villager will be converted to a aggressive version afterwards
         *
         * @return
         */
        public boolean isWillBeAggressive() {
            return willBeAggressive;
        }

        public void setWillBeAggressive(boolean willBeAggressive) {
            this.willBeAggressive = willBeAggressive;
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
        IAggressiveVillager aggressiveVillager;

        public MakeAggressive(@Nullable IVampirismVillage village, @Nonnull EntityVillager villager) {
            super(village);
            this.oldVillager = villager;
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
        public void setAgressiveVillager(@Nullable IAggressiveVillager aggressiveVillager) {
            if (!(aggressiveVillager instanceof EntityVillager)) {
                throw new IllegalArgumentException("Aggressive villager must be a instanceof EntityVillager");
            }
            this.aggressiveVillager = aggressiveVillager;
        }

        @Nullable
        public IAggressiveVillager getAggressiveVillager() {
            return aggressiveVillager;
        }
    }
}
