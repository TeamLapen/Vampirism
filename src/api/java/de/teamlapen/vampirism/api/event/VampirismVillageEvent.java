package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.world.ITotem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public abstract class VampirismVillageEvent extends Event {


    protected final ITotem totem;

    public VampirismVillageEvent(ITotem totem) {
        this.totem = totem;
    }

    @Nullable
    public IFaction getCapturingFaction() {
        return this.totem.getCapturingFaction();
    }

    @Nullable
    public IFaction getControllingFaction() {
        return this.totem.getControllingFaction();
    }

    public ITotem getTotem() {
        return totem;
    }

    @Nonnull
    public AxisAlignedBB getVillageArea() {
        return this.totem.getVillageArea();
    }

    @Nonnull
    public AxisAlignedBB getVillageAreaReduced() {
        return totem.getVillageAreaReduced();
    }

    public World getWorld() {
        return this.totem.getTileWorld();
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
         * Random existing villager in totemTile
         * Used as a "seed" villager to get a valid spawn point.
         */
        private final @Nullable
        MobEntity oldEntity;
        private @Nullable
        VillagerEntity newVillager;
        private final boolean replace;
        private boolean willBeConverted;

        public SpawnNewVillager(ITotem totem, @Nullable MobEntity oldEntity, @Nonnull VillagerEntity newVillager, boolean replace, boolean willBeConverted) {
            super(totem);
            this.oldEntity = oldEntity;
            this.newVillager = newVillager;
            this.replace = replace;
            this.willBeConverted = willBeConverted;
        }

        /**
         * Faction that owns the village
         */
        public IFaction<?> getFaction() {
            return this.totem.getControllingFaction();
        }

        @Nullable
        public VillagerEntity getNewVillager() {
            return newVillager;
        }

        /**
         * The villager that should be spawned
         * The position should already be set
         *
         * @param newVillager
         */
        public void setNewVillager(@Nullable VillagerEntity newVillager) {
            this.newVillager = newVillager;
        }

        /**
         * If the villager will be converted afterwards (e.g. to a vampire version)
         * Default: Is sometimes true if the village is not controlled by hunters. Can be overridden by {@link #setWillBeConverted}
         */
        public boolean isWillBeConverted() {
            return willBeConverted;
        }

        /**
         * A random existing villager which can be used as a seed (e.g. for the position)
         *
         * @return
         */
        @Nullable
        public MobEntity getOldEntity() {
            return oldEntity;
        }

        /**
         * Overwrite the default value.
         */
        public void setWillBeConverted(boolean willBeConverted) {
            this.willBeConverted = willBeConverted;
        }

        /**
         * @return if the {@link #oldEntity} will be replaced by {@link #newVillager}
         */
        public boolean isReplace() {
            return replace;
        }
    }

    /**
     * Fired when a normal villager should be converted to angry villager.
     * You can cancel this event to prevent vampirism behavior and replace the villager by yourself.
     */
    @Cancelable
    public static class MakeAggressive extends VampirismVillageEvent {

        private final VillagerEntity oldVillager;

        public MakeAggressive(ITotem totem, @Nonnull VillagerEntity villager) {
            super(totem);
            this.oldVillager = villager;
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
    public static abstract class VillagerCaptureFinish extends VampirismVillageEvent {

        @Nonnull
        private final List<VillagerEntity> villager;
        private final boolean forced;

        public VillagerCaptureFinish(ITotem totem, @Nonnull List<VillagerEntity> villagerIn, boolean forced) {
            super(totem);
            villager = villagerIn;
            this.forced = forced;
        }

        /**
         * @return all {@link VillagerEntity} that are in the village boundingBox
         */
        @Nonnull
        public List<VillagerEntity> getVillager() {
            return villager;
        }

        public boolean isForced() {
            return forced;
        }

        public static class Post extends VillagerCaptureFinish {
            public Post(ITotem totem, @Nonnull List<VillagerEntity> villagerIn, boolean forced) {
                super(totem, villagerIn, forced);
            }
        }

        public static class Pre extends VillagerCaptureFinish {
            public Pre(ITotem totem, @Nonnull List<VillagerEntity> villagerIn, boolean forced) {
                super(totem, villagerIn, forced);
            }
        }
    }

    /**
     * Fired when blocks around a village should be replaced
     * Only fired if Vampirism didn't already replace a block.
     * Can be used to replace a block on your own
     */
    public static class ReplaceBlock extends VampirismVillageEvent {

        @Nonnull
        private final BlockState state;
        @Nonnull
        private final BlockPos pos;

        public ReplaceBlock(ITotem totem, @Nonnull BlockState b, @Nonnull BlockPos pos) {
            super(totem);
            this.state = b;
            this.pos = pos;
        }

        /**
         * @return the position of the block
         */
        @Nonnull
        public BlockPos getBlockPos() {
            return pos;
        }

        /**
         * @return blockstate of the block to be replaced
         */
        @Nonnull
        public BlockState getState() {
            return state;
        }

    }

    /**
     * fired when the caption process is started
     * set the result to DENY to skip the vanilla code
     */
    @HasResult
    public static class InitiateCapture extends VampirismVillageEvent {

        @Nonnull
        private final IFaction<?> capturingFaction;
        private String message;

        public InitiateCapture(ITotem totem, @Nonnull IFaction<?> capturingFaction) {
            super(totem);
            this.capturingFaction = capturingFaction;
        }

        /**
         * @return capturing faction
         */
        @Override
        @Nonnull
        public IFaction<?> getCapturingFaction() {
            return capturingFaction;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
