package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.world.IVampirismVillage;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
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
     * The {@linkplain #willBeConverted} field tells if the villager will be converted to an aggressive version. You can change this.
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

        public SpawnNewVillager(@Nonnull IVampirismVillage village, @Nonnull EntityVillager seedVillager, boolean willBeConverted, @Nonnull IPlayableFaction<?> faction) {
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

        /**
         * Faction that owns the village
         */
        public IPlayableFaction<?> getFaction() {
            return faction;
        }

    }

    /**
     * Fired when a normal villager should be converted to angry villager.
     * You can set a custom replacement and cancel this event to make it take effect.
     * The {@link #oldVillager} is probably not added to a world
     */
    @Cancelable
    public static class MakeAggressive extends VampirismVillageEvent {

        private final EntityVillager oldVillager;
        private @Nullable IVillageCaptureEntity captureVillager;

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

        private final @Nonnull List<EntityVillager> villager;
        private final @Nullable IPlayableFaction<?> controllingFaction;
        private final @Nonnull IPlayableFaction<?> capturingFaction;

        public VillagerCaptureFinish(@Nullable IVampirismVillage village, @Nonnull List<EntityVillager> villagerIn, @Nullable IPlayableFaction<?> controllingFactionIn, @Nonnull IPlayableFaction<?> capturingFactionIn) {
            super(village);
            villager = villagerIn;
            controllingFaction = controllingFactionIn;
            capturingFaction = capturingFactionIn;
        }

        @Nonnull
        public List<EntityVillager> getVillager() {
            return villager;
        }

        @Nullable
        public IPlayableFaction<?> getControllingFaction() {
            return controllingFaction;
        }

        @Nonnull
        public IPlayableFaction<?> getCapturingFaction() {
            return capturingFaction;
        }

    }

    /**
     * Fired when a new Capture Entity should be spawned and the faction of the entity is not a Hunter or Vampire Entity
     */
    public static class CaptureEntity extends VampirismVillageEvent {

        private @Nonnull final IFaction<?> faction;
        private ResourceLocation entity;

        public CaptureEntity(@Nullable IVampirismVillage village, @Nonnull IFaction<?> f) {
            super(village);
            faction = f;
        }

        /**
         * the faction of the spawning entity
         */
        @Nonnull
        public IFaction<?> getFaction() {
            return faction;
        }

        /**
         * set the Entity to spawn
         * 
         * @param resourcelocation
         *            of the entity
         */
        public void setEntity(ResourceLocation entity) {
            this.entity = entity;
        }

        /**
         * @returns resourcelocation of the capture entity which should be spawned
         */
        @Nullable
        public ResourceLocation getEntity() {
            return entity;
        }
    }

    /**
     * Fired when a Faction villager should be spawned, but the controlling faction in not hunter or vampire.
     * The {@link #newVillager} will replace the {@link #seed} villager
     */
    public static class SpawnFactionVillager extends VampirismVillageEvent {

        private final @Nonnull IPlayableFaction<?> faction;
        private final @Nonnull EntityVillager seed;
        private EntityVillager newVillager;
        private boolean poisonousBlood;

        public SpawnFactionVillager(@Nullable IVampirismVillage village, @Nonnull EntityVillager seed, @Nonnull IPlayableFaction<?> faction) {
            super(village);
            this.faction = faction;
            this.seed = seed;
        }

        /**
         * faction of the village
         */
        @Nonnull
        public IPlayableFaction<?> getFaction() {
            return faction;
        }

        /**
         * @returns entity to be replaced
         */
        @Nonnull
        public EntityVillager getSeed() {
            return seed;
        }

        /**
         * @returns the new villager
         */
        @Nullable
        public EntityVillager getVillager() {
            return newVillager;
        }

        /**
         * sets the new villager
         */
        public void setVillager(EntityVillager villager) {
            newVillager = villager;
        }

        /**
         * if the villager should be protected against vampire
         */
        public boolean hasPoisonousBlood() {
            return poisonousBlood;
        }

        /**
         * sets the protection against vampire
         */
        public void setPoisonousBlood(boolean poisonous) {
            poisonousBlood = poisonous;
        }

    }

    /**
     * fired when blocks around a village should be replaced (e.g. cursed earth for vampires)
     */
    public static class ReplaceBlock extends VampirismVillageEvent {

        private final @Nonnull World world;
        private final @Nonnull IBlockState state;
        private final @Nonnull IPlayableFaction<?> faction;

        public ReplaceBlock(@Nullable IVampirismVillage village, @Nonnull World world, @Nonnull IBlockState b, @Nonnull IPlayableFaction<?> controllingFaction) {
            super(village);
            this.world = world;
            this.state = b;
            this.faction = controllingFaction;
        }

        @Nonnull
        public World getWorld() {
            return world;
        }

        /**
         * @returns blockstate of the block to be replaced
         */
        @Nonnull
        public IBlockState getState() {
            return state;
        }

        /**
         * @returns faction of the village
         */
        @Nonnull
        public IPlayableFaction<?> getFaction() {
            return faction;
        }

    }

    /**
     * fired when the caption process is started
     */
    @HasResult
    public static class InitiateCapture extends VampirismVillageEvent {

        private final @Nonnull World world;
        private final @Nullable IPlayableFaction<?> controllingFaction;
        private final @Nonnull IPlayableFaction<?> capturingFaction;

        public InitiateCapture(@Nullable IVampirismVillage village, @Nonnull World world, @Nullable IPlayableFaction<?> controllingFaction, @Nonnull IPlayableFaction<?> capturingFaction) {
            super(village);
            this.world = world;
            this.controllingFaction = controllingFaction;
            this.capturingFaction = capturingFaction;
        }

        @Nonnull
        public World getWorld() {
            return world;
        }

        /**
         * @returns controlling faction
         */
        @Nullable
        public IPlayableFaction<?> getControllingFaction() {
            return controllingFaction;
        }

        /**
         * @returns capturing faction
         */
        @Nonnull
        public IPlayableFaction<?> getCapturingFaction() {
            return capturingFaction;
        }

    }

    /**
     * fired when the village area is updated (used for vampire fog rendering & sundamage)
     */
    public static class RegisterVillageBoundingBoxEvent extends VampirismVillageEvent {

        private final @Nonnull StructureBoundingBox bb;

        public RegisterVillageBoundingBoxEvent(@Nullable IVampirismVillage village, @Nonnull StructureBoundingBox bb) {
            super(village);
            this.bb = bb;
        }
        
        /**
         * @returns bounding box of the village
         */
        @Nonnull
        public StructureBoundingBox getBoundingBox() {
            return bb;
        }

    }
}
