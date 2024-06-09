package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.world.ITotem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public abstract class VampirismVillageEvent extends Event {


    protected final ITotem totem;

    public VampirismVillageEvent(ITotem totem) {
        this.totem = totem;
    }

    @Nullable
    public Holder<? extends IFaction<?>> getCapturingFaction() {
        return this.totem.getCapturingFaction();
    }

    @Nullable
    public Holder<? extends IFaction<?>> getControllingFaction() {
        return this.totem.getControllingFaction();
    }

    public ITotem getTotem() {
        return totem;
    }

    @NotNull
    public AABB getVillageArea() {
        return this.totem.getVillageArea();
    }

    @NotNull
    public AABB getVillageAreaReduced() {
        return totem.getVillageAreaReduced();
    }

    public Level getWorld() {
        return this.totem.getTileWorld();
    }

    /**
     * Fired when a new villager will be spawned.
     * You can replace the villager object with a suitable alternative instance
     * <p>
     * Your villager should not be spawned in the world.
     * <p>
     */
    public static class SpawnNewVillager extends VampirismVillageEvent {

        /**
         * Random existing villager in totemTile
         * Used as a "seed" villager to get a valid spawn point.
         */
        @Nullable
        private final Mob oldEntity;
        private final boolean replace;
        @NotNull
        private Villager newVillager;

        public SpawnNewVillager(ITotem totem, @Nullable Mob oldEntity, @NotNull Villager newVillager, boolean replace) {
            super(totem);
            this.oldEntity = oldEntity;
            this.newVillager = newVillager;
            this.replace = replace;
        }

        /**
         * Faction that owns the village
         */
        public @Nullable Holder<? extends IFaction<?>> getFaction() {
            return this.totem.getControllingFaction();
        }

        @NotNull
        public Villager getNewVillager() {
            return newVillager;
        }

        /**
         * The villager that should be spawned
         */
        public void setNewVillager(@NotNull Villager newVillager) {
            this.newVillager = newVillager;
        }

        /**
         * A random existing villager which can be used as a seed (e.g. for the position)
         */
        @Nullable
        public Mob getOldEntity() {
            return oldEntity;
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
    public static class MakeAggressive extends VampirismVillageEvent implements ICancellableEvent {

        private final @NotNull Villager oldVillager;

        public MakeAggressive(ITotem totem, @NotNull Villager villager) {
            super(totem);
            this.oldVillager = villager;
        }

        /**
         * @return The villager which should be made aggressive
         */
        public Villager getOldVillager() {
            return oldVillager;
        }
    }

    /**
     * Fired when the Capture process is finished the Villager should be affected by the faction change
     * if result is {@link Result#DENY} the Vanilla code is skipped
     */
    public static abstract class VillagerCaptureFinish extends VampirismVillageEvent {

        @NotNull
        private final List<Villager> villager;
        private final boolean forced;

        public VillagerCaptureFinish(ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
            super(totem);
            villager = villagerIn;
            this.forced = forced;
        }

        /**
         * @return all {@link Villager} that are in the village boundingBox
         */
        @NotNull
        public List<Villager> getVillager() {
            return villager;
        }

        public boolean isForced() {
            return forced;
        }

        public static class Post extends VillagerCaptureFinish {
            public Post(ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
                super(totem, villagerIn, forced);
            }
        }

        public static class Pre extends VillagerCaptureFinish {

            private boolean disableEntityConversion = false;

            public Pre(ITotem totem, @NotNull List<Villager> villagerIn, boolean forced) {
                super(totem, villagerIn, forced);
            }

            public void setDisableEntityConversion(boolean disableEntityConversion) {
                this.disableEntityConversion = disableEntityConversion;
            }

            /**
             * @return If true the entities should not be changed by vampirism.
             */
            public boolean isEntityConversionDisabled() {
                return disableEntityConversion;
            }
        }
    }

    /**
     * fired when the caption process is started
     * set the result to {@code DENY} to skip the vanilla code
     */
    public static class InitiateCapture extends VampirismVillageEvent {

        @NotNull
        private final Holder<? extends IFaction<?>> capturingFaction;
        @Nullable
        private String message;
        private boolean disallowCapture = false;

        public InitiateCapture(ITotem totem, @NotNull Holder<? extends IFaction<?>> capturingFaction) {
            super(totem);
            this.capturingFaction = capturingFaction;
        }

        /**
         * @return capturing faction
         */
        @Override
        @NotNull
        public Holder<? extends IFaction<?>> getCapturingFaction() {
            return capturingFaction;
        }

        /**
         * Get an optional message when the capture is disallowed
         */
        @Nullable
        public String getMessage() {
            return this.message;
        }

        /**
         * Set an optional message when the capture is disallowed
         * @param message the message
         */
        public void setMessage(@Nullable String message) {
            this.message = message;
        }

        /**
         * Should the capture be disallowed it is helpful to set an additional message {@link #setMessage(String)}
         */
        public void disallowCapture() {
            this.disallowCapture = true;
        }

        /**
         * Disallow the raid start
         */
        public boolean isCaptureDisallowed() {
            return this.disallowCapture;
        }
    }

    public static class DefineRaidStrength extends VampirismVillageEvent {

        /**
         * -2 if triggered by player
         * -1 if triggered by chance
         * 0<= x < 5 triggered by badomen effect with respective amplifier
         */
        private final int badOmenLevel;
        private float defendStrength;
        private float attackStrength;

        public DefineRaidStrength(ITotem totem, int badOmenLevel, float defendStrength, float attackStrength) {
            super(totem);
            this.badOmenLevel = badOmenLevel;
            this.defendStrength = defendStrength;
            this.attackStrength = attackStrength;
        }

        public float getAttackStrength() {
            return attackStrength;
        }

        public void setAttackStrength(float attackStrength) {
            this.attackStrength = attackStrength;
        }

        public int getBadOmenLevel() {
            return badOmenLevel;
        }

        public float getDefendStrength() {
            return defendStrength;
        }

        public void setDefendStrength(float defendStrength) {
            this.defendStrength = defendStrength;
        }

        public boolean isBadOmenTriggered() {
            return badOmenLevel >= 0;
        }

        public boolean isPlayerRaid() {
            return badOmenLevel == -2;
        }

        public boolean isRandomRaid() {
            return badOmenLevel == -1;
        }
    }
}
