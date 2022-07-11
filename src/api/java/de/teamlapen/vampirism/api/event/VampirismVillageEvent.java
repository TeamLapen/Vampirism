package de.teamlapen.vampirism.api.event;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.world.ITotem;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraftforge.eventbus.api.Event.HasResult;

@SuppressWarnings("unused")
public abstract class VampirismVillageEvent extends Event {


    protected final ITotem totem;

    public VampirismVillageEvent(ITotem totem) {
        this.totem = totem;
    }

    @Nullable
    public IFaction<?> getCapturingFaction() {
        return this.totem.getCapturingFaction();
    }

    @Nullable
    public IFaction<?> getControllingFaction() {
        return this.totem.getControllingFaction();
    }

    public ITotem getTotem() {
        return totem;
    }

    @Nonnull
    public AABB getVillageArea() {
        return this.totem.getVillageArea();
    }

    @Nonnull
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
        @Nonnull
        private Villager newVillager;

        public SpawnNewVillager(ITotem totem, @Nullable Mob oldEntity, @Nonnull Villager newVillager, boolean replace) {
            super(totem);
            this.oldEntity = oldEntity;
            this.newVillager = newVillager;
            this.replace = replace;
        }

        /**
         * Faction that owns the village
         */
        public IFaction<?> getFaction() {
            return this.totem.getControllingFaction();
        }

        @Nonnull
        public Villager getNewVillager() {
            return newVillager;
        }

        /**
         * The villager that should be spawned
         */
        public void setNewVillager(@Nonnull Villager newVillager) {
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
    @Cancelable
    public static class MakeAggressive extends VampirismVillageEvent {

        private final Villager oldVillager;

        public MakeAggressive(ITotem totem, @Nonnull Villager villager) {
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
    @HasResult
    public static abstract class VillagerCaptureFinish extends VampirismVillageEvent {

        @Nonnull
        private final List<Villager> villager;
        private final boolean forced;

        public VillagerCaptureFinish(ITotem totem, @Nonnull List<Villager> villagerIn, boolean forced) {
            super(totem);
            villager = villagerIn;
            this.forced = forced;
        }

        /**
         * @return all {@link Villager} that are in the village boundingBox
         */
        @Nonnull
        public List<Villager> getVillager() {
            return villager;
        }

        public boolean isForced() {
            return forced;
        }

        public static class Post extends VillagerCaptureFinish {
            public Post(ITotem totem, @Nonnull List<Villager> villagerIn, boolean forced) {
                super(totem, villagerIn, forced);
            }
        }

        public static class Pre extends VillagerCaptureFinish {
            public Pre(ITotem totem, @Nonnull List<Villager> villagerIn, boolean forced) {
                super(totem, villagerIn, forced);
            }
        }
    }

    /**
     * fired when the caption process is started
     * set the result to {@code DENY} to skip the vanilla code
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
