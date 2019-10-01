package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class VampirismVillageEvent extends Event {


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
         * Random existing villager in totemTile
         * Used as a "seed" villager to get a valid spawn point.
         */
        private final @Nonnull
        VillagerEntity seedVillager;
        private @Nullable
        VillagerEntity newVillager;
        private boolean willBeConverted;
        private final IPlayableFaction<?> faction;

        public SpawnNewVillager(@Nonnull VillagerEntity seedVillager, boolean willBeConverted, IPlayableFaction<?> faction) {
            this.seedVillager = seedVillager;
            this.willBeConverted = willBeConverted;
            this.faction = faction;
        }

        public VillagerEntity getNewVillager() {
            return newVillager;
        }

        /**
         * The villager that should be spawned
         * The position should already be set
         *
         * @param newVillager
         */
        public void setNewVillager(VillagerEntity newVillager) {
            this.newVillager = newVillager;
        }

        /**
         * A random existing villager which can be used as a seed (e.g. for the position)
         *
         * @return
         */
        @Nonnull
        public VillagerEntity getSeedVillager() {
            return seedVillager;
        }

        /**
         * If the villager will be converted afterwards (e.g. to a vampire version)
         * Default: Is sometimes true if the village is not controlled by hunters. Can be overridden by {@link #setWillBeConverted}
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

        private final VillagerEntity oldVillager;
        private @Nullable
        IVillageCaptureEntity captureVillager;

        public MakeAggressive(@Nonnull VillagerEntity villager) {
            this.oldVillager = villager;
        }

        @Nullable
        public IVillageCaptureEntity getAggressiveVillager() {
            return captureVillager;
        }

        /**
         * Set the aggressive version of the old villager.
         * Event has to be canceled for this to take effect
         */
        public <T extends VillagerEntity & IVillageCaptureEntity> void setAggressiveVillager(@Nullable T captureVillager) {
            this.captureVillager = captureVillager;
        }

        /**
         * @return The villager which should be made aggressive
         */
        public VillagerEntity getOldVillager() {
            return oldVillager;
        }
    }

    /**
     * Fired when the Capture process is finished the the Villager should be affected by the faction change
     * if result is {@link Result#DENY} the Vanilla code is skipped
     */
    @HasResult
    public static class VillagerCaptureFinish extends VampirismVillageEvent {

        private final @Nonnull
        List<VillagerEntity> villager;
        private final @Nullable
        IPlayableFaction<?> controllingFaction;
        private final @Nonnull
        IPlayableFaction<?> capturingFaction;
        private final @Nonnull
        AxisAlignedBB affectedArea;

        public VillagerCaptureFinish(@Nonnull List<VillagerEntity> villagerIn, @Nullable IPlayableFaction<?> controllingFactionIn, @Nonnull IPlayableFaction<?> capturingFactionIn, @Nonnull AxisAlignedBB affectedAreaIn) {
            villager = villagerIn;
            controllingFaction = controllingFactionIn;
            capturingFaction = capturingFactionIn;
            affectedArea = affectedAreaIn;
        }

        /**
         * @returns all {@link VillagerEntity} that are in the village boundingBox
         */
        @Nonnull
        public List<VillagerEntity> getVillager() {
            return villager;
        }

        /**
         * @returns the faction that controls the village before the village is converted to the new faction
         */
        @Nullable
        public IPlayableFaction<?> getControllingFaction() {
            return controllingFaction;
        }

        /**
         * @returns the faction that has captured the village at this moment
         */
        @Nonnull
        public IPlayableFaction<?> getCapturingFaction() {
            return capturingFaction;
        }

        /**
         * @returns the village area
         */
        @Nonnull
        public AxisAlignedBB getAffectedArea() {
            return affectedArea;
        }
    }

    /**
     * Fired when a new Capture Entity should be spawned and the faction of the entity is not a Hunter or Vampire Entity
     */
    public static class SpawnCaptureEntity extends VampirismVillageEvent {

        private @Nonnull
        final IFaction<?> faction;
        private EntityType<? extends MobEntity> entity;

        public SpawnCaptureEntity(@Nonnull IFaction<?> f) {
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
         * @param {@link ResourceLocation}
         *               of the entity
         */
        public void setEntity(EntityType<? extends MobEntity> entity) {
            this.entity = entity;
        }

        /**
         * @returns {@link ResourceLocation} of the capture entity which should be spawned
         */
        @Nullable
        public EntityType<? extends MobEntity> getEntity() {
            return entity;
        }
    }

    /**
     * Fired when a Faction villager should be spawned, but the controlling faction in not hunter or vampire.
     * The {@link #newVillager} will replace the {@link #seed} villager
     */
    public static class SpawnFactionVillager extends VampirismVillageEvent {

        private final @Nonnull
        IPlayableFaction<?> faction;
        private final @Nonnull
        VillagerEntity seed;
        private VillagerEntity newVillager;
        private boolean poisonousBlood;

        public SpawnFactionVillager(@Nonnull VillagerEntity seed, @Nonnull IPlayableFaction<?> faction) {
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
        public VillagerEntity getSeed() {
            return seed;
        }

        /**
         * @returns the new villager
         */
        @Nullable
        public VillagerEntity getVillager() {
            return newVillager;
        }

        /**
         * sets the new villager
         */
        public void setVillager(VillagerEntity villager) {
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
     * Fired when blocks around a village should be replaced
     * Only fired if Vampirism didn't already replace a block.
     * Can be used to replace a block on your own
     */
    public static class ReplaceBlock extends VampirismVillageEvent {

        private final @Nonnull
        World world;
        private final @Nonnull
        BlockState state;
        private final @Nonnull
        IFaction<?> faction;
        private final @Nonnull
        BlockPos pos;

        public ReplaceBlock(@Nonnull World world, @Nonnull BlockState b, @Nonnull BlockPos pos, @Nonnull IFaction<?> controllingFaction) {
            this.world = world;
            this.state = b;
            this.faction = controllingFaction;
            this.pos = pos;
        }

        /**
         * @returns the world of the block
         */
        @Nonnull
        public World getWorld() {
            return world;
        }

        /**
         * @returns blockstate of the block to be replaced
         */
        @Nonnull
        public BlockState getState() {
            return state;
        }

        /**
         * @returns faction of the village
         */
        @Nonnull
        public IFaction<?> getFaction() {
            return faction;
        }

        /**
         * @returns the position of the block
         */
        @Nonnull
        public BlockPos getBlockPos() {
            return pos;
        }

    }

    /**
     * fired when the caption process is started
     * set the result to DENY to skip the vanilla code
     */
    @HasResult
    public static class InitiateCapture extends VampirismVillageEvent {

        private final @Nonnull
        World world;
        private final @Nullable
        IPlayableFaction<?> controllingFaction;
        private final @Nonnull
        IPlayableFaction<?> capturingFaction;

        public InitiateCapture(@Nonnull World world, @Nullable IPlayableFaction<?> controllingFaction, @Nonnull IPlayableFaction<?> capturingFaction) {
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
    public static class UpdateBoundingBox extends VampirismVillageEvent {

        private final @Nonnull
        MutableBoundingBox bb;

        public UpdateBoundingBox(@Nonnull MutableBoundingBox bb) {
            this.bb = bb;
        }

        /**
         * @returns bounding box of the village
         */
        @Nonnull
        public MutableBoundingBox getBoundingBox() {
            return bb;
        }

    }
}
