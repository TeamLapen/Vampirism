package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillage;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

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
        private boolean willBeConverted;
        private final IPlayableFaction<?> faction;

        public SpawnNewVillager(@Nonnull IVampirismVillage village, @Nonnull EntityVillager seedVillager, boolean willBeConverted, IPlayableFaction<?> faction) {
            super(village);
            this.seedVillager = seedVillager;
            this.willBeConverted = willBeConverted;
            this.faction = faction;
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
         * If the villager will be converted afterwards (e.g. to a vampire version).
         * Default: Is sometimes true if the village is not controlled by hunters. Can be overridden by {@link #setWillBeVampire}
         */
        public boolean isWillBeConverted() {
            return willBeConverted;
        }

        /**
         * Overwrite the default value.
         */
        public void setWillBeConverted(boolean willBeConverted) {
            this.willBeConverted = willBeConverted;
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

    /**
     * Fired when the Capture process is finished the the Villager should be affected by the faction change
     * if result is {@link Result#DENY} the Vanilla code is skipped
     */
    @HasResult
    public static class VillagerCaptureFinish extends VampirismVillageEvent {

        private final List<EntityVillager> villager;
        private final IPlayableFaction<?> controllingFaction;
        private final IPlayableFaction<?> capturingFaction;

        public VillagerCaptureFinish(@Nullable IVampirismVillage village, @Nonnull List<EntityVillager> villagerIn, @Nullable IPlayableFaction<?> controllingFactionIn, @Nonnull IPlayableFaction<?> capturingFactionIn) {
            super(village);
            villager = villagerIn;
            controllingFaction = controllingFactionIn;
            capturingFaction = capturingFactionIn;
        }

        public List<EntityVillager> getVillager() {
            return villager;
        }

        public IPlayableFaction<?> getControllingFaction() {
            return controllingFaction;
        }

        public IPlayableFaction<?> getCapturingFaction() {
            return capturingFaction;
        }

    }

    /**
     * Fired when a new Capture Entity should be spawned and the faction of the entity is not a Hunter or Vampire Entity
     */
    public static class CaptureEntity extends VampirismVillageEvent {

        private final IFaction<?> faction;
        private ResourceLocation entity;

        public CaptureEntity(@Nonnull IFaction f) {
            super(null);
            faction = f;
        }

        public IFaction<?> getFaction() {
            return faction;
        }

        /**
         * set the Entity to spawn
         * 
         * @param entity
         */
        public void setEntity(ResourceLocation entity) {
            this.entity = entity;
        }

        /**
         * @returns capture entity which should be spawned
         */
        public ResourceLocation getEntity() {
            return entity;
        }
    }

    /**
     * Fired when a Faction villager should be spawned, but the controlling faction in not hunter or vampire
     * seed Entity must be killed
     */
    public static class SpawnFactionVillager extends VampirismVillageEvent {

        private final IPlayableFaction<?> faction;
        private final World world;
        private final EntityVillager seed;

        public SpawnFactionVillager(IVampirismVillage village, World world, EntityVillager seed, IPlayableFaction<?> faction) {
            super(village);
            this.faction = faction;
            this.world = world;
            this.seed = seed;
        }

        public IPlayableFaction<?> getFaction() {
            return faction;
        }

        /**
         * @returns entity to be replaced (must be removed)
         */
        public EntityVillager getSeed() {
            return seed;
        }

        public World getWorld() {
            return world;
        }

    }

    /**
     * fired when blocks around a village should be replaced (e.g. cursed earth for vampires)
     */
    public static class ReplaceBlock extends VampirismVillageEvent {

        private final World world;
        private final IBlockState state;
        private final IPlayableFaction<?> faction;

        public ReplaceBlock(VampirismVillage village, World world, IBlockState b, IPlayableFaction<?> controllingFaction) {
            super(village);
            this.world = world;
            this.state = b;
            this.faction = controllingFaction;
        }

        public World getWorld() {
            return world;
        }

        public IBlockState getState() {
            return state;
        }

        public IPlayableFaction<?> getFaction() {
            return faction;
        }

    }
}
