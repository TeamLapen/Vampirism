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

    public IVampirismVillage getVillage() {
        return village;
    }

    /**
     * Fired when a normal villager should be converted to angry villager.
     * You can set a custom replacement and cancel this event to make it take effect.
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
