package de.teamlapen.faction.api.event;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.world.ITotem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public abstract class FactionVillageEvent extends Event {


    protected final ITotem totem;

    public FactionVillageEvent(ITotem totem) {
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

    public AABB getVillageArea() {
        return this.totem.getVillageArea();
    }

    public AABB getVillageAreaReduced() {
        return totem.getVillageAreaReduced();
    }

    @Nullable
    public Level getLevel() {
        return this.totem.getTileLevel();
    }

    /**
     * Fired when a new villager will be spawned.
     * You can replace the villager object with a suitable alternative instance
     * <p>
     * Your villager should not be spawned in the world.
     * <p>
     */
    public static class SpawnNewVillager extends FactionVillageEvent {

        /**
         * If an existing entity is to be replaced, it is available here
         */
        @Nullable
        private final LivingEntity replacedEntity;

        @Nullable
        private Villager newVillager;
        private final EntityType<? extends Villager> originalNewVillagerType;

        public SpawnNewVillager(ITotem totem, EntityType<? extends Villager> newVillagerType, @Nullable LivingEntity replacedEntity) {
            super(totem);
            this.replacedEntity = replacedEntity;
            this.originalNewVillagerType = newVillagerType;

        }

        /**
         * Faction that owns the village
         */
        public Holder<? extends IFaction<?>> getFaction() {
            return this.totem.getControllingFaction();
        }

        /**
         * @return The type the ne
         */
        public EntityType<? extends Villager> getOriginalNewVillagerType(){
            return originalNewVillagerType;
        }

        /**
         * @return May be null, if entity creation fails
         */
        @Nullable
        public Villager getOrCreateNewVillager(){
            if(newVillager==null && getLevel() != null){
                newVillager= getOriginalNewVillagerType().create(getLevel(), isReplace() ? EntitySpawnReason.CONVERSION : EntitySpawnReason.EVENT );
            }
            return newVillager;
        }

        /**
         * The villager that should be spawned
         */
        public Villager setNewVillager(Villager newVillager) {
            this.newVillager = newVillager;
            return newVillager;
        }

        /**
         * @return if the {@link #replacedEntity} will be replaced by {@link #newVillager}
         */
        public boolean isReplace() {
            return replacedEntity !=null;
        }

        /**
         * @return Villager to be replaced if present
         */
        public Optional<LivingEntity> getEntityToReplace(){
            return Optional.<LivingEntity>ofNullable(replacedEntity);
        }

    }

    /**
     * Fired when a normal villager should be converted to angry villager.
     * You can cancel this event to prevent vampirism behavior and replace the villager by yourself.
     */
    public static class MakeAggressive extends FactionVillageEvent implements ICancellableEvent {

        private final Villager villager;

        public MakeAggressive(ITotem totem, Villager villager) {
            super(totem);
            this.villager = villager;
        }

        /**
         * @return The villager which should be made aggressive
         */
        public Villager getVillager() {
            return villager;
        }
    }

    /**
     * Fired when the Capture process is finished the Villager should be affected by the faction change
     */
    public static abstract class VillagerCaptureFinish extends FactionVillageEvent {

        private final List<Villager> villager;
        private final boolean forced;

        public VillagerCaptureFinish(ITotem totem, List<Villager> villagerIn, boolean forced) {
            super(totem);
            villager = villagerIn;
            this.forced = forced;
        }

        /**
         * @return all {@link Villager} that are in the village boundingBox
         */
        public List<Villager> getVillager() {
            return villager;
        }

        public boolean isForced() {
            return forced;
        }

        public static class Pre extends VillagerCaptureFinish {

            private boolean disableEntityConversion = false;

            public Pre(ITotem totem, List<Villager> villagerIn, boolean forced) {
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
    public static class InitiateCapture extends FactionVillageEvent {

        private final Holder<? extends IFaction<?>> capturingFaction;
        @Nullable
        private String message;
        private boolean disallowCapture = false;

        public InitiateCapture(ITotem totem, Holder<? extends IFaction<?>> capturingFaction) {
            super(totem);
            this.capturingFaction = capturingFaction;
        }

        /**
         * @return capturing faction
         */
        @Override
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
         *
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

    public static class DefineRaidStrength extends FactionVillageEvent {

        /**
         * -2 if triggered by player
         * -1 if triggered by chance
         * 0<= x < 5 triggered by bad omen effect with respective amplifier
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

    public static class AreaChangedEvent extends FactionVillageEvent {

        private final @Nullable AABB area;

        public AreaChangedEvent(ITotem totem, @Nullable AABB area) {
            super(totem);
            this.area = area;
        }

        public @Nullable AABB getArea() {
            return area;
        }
    }

    public static class RemovedEvent extends FactionVillageEvent {

        public RemovedEvent(ITotem totem) {
            super(totem);
        }
    }

    public static class SpawnVillagerEvent extends FactionVillageEvent {

        public SpawnVillagerEvent(ITotem totem) {
            super(totem);
        }
    }

    public static class BreakCaptureEvent extends FactionVillageEvent {
        public BreakCaptureEvent(ITotem totem) {
            super(totem);
        }
    }

    public static class UpdateCreaturesOnCaptureFinishEvent extends FactionVillageEvent {

        private final boolean forced;
        private final Map<LivingEntity, Action> entitiesScheduledForReplacement = new HashMap<>();

        public UpdateCreaturesOnCaptureFinishEvent(ITotem totem, boolean forced) {
            super(totem);
            this.forced = forced;
        }

        public boolean isForced() {
            return this.forced;
        }

        public void requestReplacement(LivingEntity oldVillager) {
            if (this.entitiesScheduledForReplacement.get(oldVillager) != Action.KILL) {
                this.entitiesScheduledForReplacement.put(oldVillager, Action.REPLACE);
            }
        }

        public void requestKill(LivingEntity oldVillager) {
            this.entitiesScheduledForReplacement.put(oldVillager, Action.KILL);
        }

        public Map<LivingEntity, Action> getEntitiesScheduledForReplacement() {
            return Collections.unmodifiableMap(this.entitiesScheduledForReplacement);
        }


        public enum Action {
            /**
             * Replace with a fresh villager
             */
            REPLACE,
            KILL
        }
    }

    public static class SpawnCaptureEntityEvent extends FactionVillageEvent {

        private final Holder<? extends IFaction<?>> faction;
        @Nullable
        private LivingEntity entity;

        public SpawnCaptureEntityEvent(ITotem totem, Holder<? extends IFaction<?>> faction) {
            super(totem);
            this.faction = faction;
        }

        public @Nullable LivingEntity getEntity() {
            return entity;
        }

        public <T extends LivingEntity> T setEntity(T entity) {
            this.entity = entity;
            return entity;
        }

        public Holder<? extends IFaction<?>> getFaction() {
            return faction;
        }
    }
}
