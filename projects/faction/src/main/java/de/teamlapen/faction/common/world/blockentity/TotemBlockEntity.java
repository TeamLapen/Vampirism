package de.teamlapen.faction.common.world.blockentity;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.teamlapen.faction.api.event.FactionVillageEvent;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IFactionHelper;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.world.ICaptureAttributes;
import de.teamlapen.faction.api.world.ITotem;
import de.teamlapen.faction.api.world.entities.ICaptureIgnore;
import de.teamlapen.faction.api.world.entities.ICaptureStrengthProvider;
import de.teamlapen.faction.api.world.entities.ITaskMasterEntity;
import de.teamlapen.faction.api.world.entities.IVillageCaptureEntity;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.core.*;
import de.teamlapen.faction.common.event.FactionEventFactory;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.network.packets.client.ClientboundPlaySoundEventPacket;
import de.teamlapen.faction.common.tags.FactionProfessionTags;
import de.teamlapen.faction.common.util.*;
import de.teamlapen.faction.common.world.ServerMultiBossEvent;
import de.teamlapen.faction.common.world.blocks.TotemBaseBlock;
import de.teamlapen.faction.common.world.blocks.TotemTopBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public class TotemBlockEntity extends NetworkedBlockEntity implements ITotem {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final RandomSource RNG = RandomSource.create();
    private final ServerMultiBossEvent captureInfo = new ServerMultiBossEvent(Component.translatable("message.factionapi.village_raid.neutral"), BossEvent.BossBarOverlay.NOTCHED_10);
    public long timeSinceLastRaid = 0;
    //block attributes
    private boolean isComplete;
    private boolean isInsideVillage;
    private boolean isDisabled;
    //tile attributes
    @NotNull
    private Set<PoiRecord> village = Sets.newHashSet();
    /**
     * use {@link #setControllingFaction(net.minecraft.core.Holder)}
     */
    @NotNull
    private Holder<? extends IFaction<?>> controllingFaction = DefaultFactions.NEUTRAL;
    /**
     * use {@link #setCapturingFaction(net.minecraft.core.Holder)}
     */
    @Nullable
    private Holder<? extends IFaction<?>> capturingFaction;
    /**
     * use {@link #getVillageArea()}
     */
    @Nullable
    private AABB villageArea;
    /**
     * use {@link #getVillageAreaReduced()}
     */
    @Nullable
    private AABB villageAreaReduced;
    //forced attributes
    @Nullable
    private Holder<? extends IFaction<?>> forcedFaction;
    private int forcedFactionTimer;
    private boolean forceVillageUpdate;
    //capturing attributes
    private CAPTURE_PHASE phase;
    private int captureTimer;
    private int captureAbortTimer;
    private int captureDuration;
    private int captureForceTargetTimer;
    private float strengthRatio;
    private int badOmenLevel;
    //client attributes
    private long beamRenderCounter;
    private float beamRenderScale;
    private int baseColor = DyeColor.WHITE.getTextureDiffuseColor();
    private int progressColor = DyeColor.WHITE.getTextureDiffuseColor();

    public TotemBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(FactionBlockEntities.TOTEM.get(), pos, state);
    }

    public void abortCapture() {
        this.applyVictoryBonus(false);
        notifyNearbyPlayers(Component.translatable("message.factionapi.village_raid.defended"));
        breakCapture();
    }

    public void breakCapture() {
        this.setCapturingFaction(null);
        this.forceVillageUpdate = true;
        this.informEntitiesAboutCaptureStop();
        this.updateBossinfoPlayers(null);
        this.captureInfo.clear();
        FactionEventFactory.fireVillageCaptureBreakEvent(this);
        this.setChanged();
    }

    /**
     * checks if the player can remove the totem
     *
     * @param player player to check
     * @return weather he can or not
     */
    public boolean canPlayerRemoveBlock(@NotNull Player player) {
        if (player.getAbilities().instabuild) return true;
        if (!player.isAlive()) return false;
        @NotNull Holder<? extends IPlayableFaction<?>> faction = IFactionHelper.get().getFaction(player);
        if (IFaction.is(faction, this.controllingFaction)) {
            if (this.capturingFaction == null) {
                return true;
            } else {
                player.displayClientMessage(Component.translatable("message.factionapi.village_totem.fail_other_capturing"), true);
                return false;
            }
        } else if (IFaction.is(this.capturingFaction, faction)) {
            if (IFaction.is(this.controllingFaction, de.teamlapen.faction.api.tags.FactionTags.IS_NEUTRAL)) {
                return true;
            } else {
                player.displayClientMessage(Component.translatable("message.factionapi.village_totem.fail_other_faction"), true);
                return false;
            }
        } else {
            if (!(this.capturingFaction == null && IFaction.is(this.controllingFaction, de.teamlapen.faction.api.tags.FactionTags.IS_NEUTRAL))) {
                player.displayClientMessage(Component.translatable("message.factionapi.village_totem.fail_other_faction"), true);
                return false;
            }
            return true;
        }
    }

    public int getBaseColor() {
        return this.baseColor;
    }

    /**
     * @return 0-100. 80 if in stage 2
     */
    public int getCaptureProgress() {
        return this.capturingFaction == null ? 0 : this.phase == CAPTURE_PHASE.PHASE_2 ? 80 : (int) (this.captureTimer / (float) FactionConfig.server().raidPhaseOneDuration.get() * 80f);
    }

    public int getCapturingColors() {
        return this.progressColor;
    }

    @Nullable
    @Override
    public Holder<? extends IFaction<?>> getCapturingFaction() {
        return capturingFaction;
    }

    @Override
    public @NotNull Holder<? extends IFaction<?>> getControllingFaction() {
        return controllingFaction;
    }

    /**
     * gets the size of the village
     *
     * @return amount of {@link PoiRecord} related to this village totem
     */
    public int getSize() {
        return this.village.size();
    }

    public void initiateCapture(Player player) {
        if (!player.isAlive()) return;
        var faction = FactionPlayerHandler.get(player).getFaction();
        if (IFaction.is(faction, de.teamlapen.faction.api.tags.FactionTags.CAN_RAID)) {
            initiateCapture(faction, player::displayClientMessage, -1, -1f);
        } else if (!IFaction.isNeutral(faction))  {
            player.displayClientMessage(Component.translatable("message.factionapi.village_totem.non_village_faction"),true);
        }
    }

    @Override
    public AABB getVillageArea() {
        if (this.villageArea == null) {
            updateVillageArea();
        }
        return this.villageArea;
    }

    @Override
    public AABB getVillageAreaReduced() {
        if (this.villageAreaReduced == null) {
            updateVillageArea();
        }
        return this.villageAreaReduced;
    }

    /**
     * @param faction          attacking faction
     * @param feedback         interaction feedback supplier if capture cannot be started {@link #capturePreconditions(net.minecraft.core.Holder, java.util.function.BiConsumer)}
     * @param badOmenLevel     level of the badomen effect that triggered the raid (effect amplifier + 1). -1 if not triggered by bad omen.
     * @param strengthModifier modifier of the faction strength ration. See {@link #calculateAttackStrength(int, float)}
     */
    public void initiateCapture(@NotNull Holder<? extends IFaction<?>> faction, @Nullable BiConsumer<Component, Boolean> feedback, int badOmenLevel, float strengthModifier) {
        this.updateTileStatus();
        if (!this.capturePreconditions(faction, feedback == null ? (a, b) -> {
        } : feedback)) {
            return;
        }
        this.forceVillageUpdate = true;
        this.captureAbortTimer = 0;
        this.captureDuration = 24000;
        this.captureTimer = 0;
        this.captureForceTargetTimer = 0;
        this.setCapturingFaction(faction);
        this.calculateAttackStrength(badOmenLevel, strengthModifier);
        this.timeSinceLastRaid = 0;

        this.phase = IFaction.isNeutral(this.controllingFaction) ? CAPTURE_PHASE.PHASE_1_NEUTRAL : CAPTURE_PHASE.PHASE_1_OPPOSITE;

        Identifier currentFactionId = this.controllingFaction.unwrapKey().orElseThrow().identifier();
        this.notifyNearbyPlayers(Component.translatable("message." + currentFactionId.getNamespace() + ".village_raid.attacked." + currentFactionId.getPath(), faction.value().getNamePlural()));

        this.setChanged();

        this.makeAgressive();
        LOGGER.debug("Initiated capture with strength {} by {} at {} with badomen level {}", this.strengthRatio, faction.getRegisteredName(), this.getBlockPos(), badOmenLevel);
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.badOmenLevel = input.getIntOr("badOmenTriggered", 0);
        this.isDisabled = input.getBooleanOr("isDisabled", false);
        this.isComplete = input.getBooleanOr("isComplete", false);
        this.isInsideVillage = input.getBooleanOr("isInsideVillage", false);
        this.setControllingFaction(input.read("controllingFaction", ModCodecs.faction()).orElse(DefaultFactions.NEUTRAL));
        input.read("capturingFaction", ModCodecs.faction()).ifPresentOrElse(faction -> {
            this.setCapturingFaction(faction);
            this.captureTimer = input.getIntOr("captureTimer", 0);
            this.captureDuration = input.getIntOr("captureDuration", 0);
            this.phase = CAPTURE_PHASE.valueOf(input.getStringOr("phase", CAPTURE_PHASE.PHASE_1_NEUTRAL.name()).toUpperCase(Locale.ROOT));
            this.strengthRatio = input.getFloatOr("strengthRatio", 0f);
            this.captureAbortTimer = input.getIntOr("captureAbortTimer", 0);
            if (this.phase == CAPTURE_PHASE.PHASE_2) {
                this.setupPhase2();
            }
        }, () -> setCapturingFaction(null));
        if (this.level != null) {
            input.read("villageArea", ModCodecs.AABB).ifPresentOrElse(aabb -> this.villageArea = aabb, () -> this.villageArea = null);
            FactionEventFactory.fireVillageAreaChangedEvent(this, this.villageArea);
        }
        this.forceVillageUpdate = true;
        this.captureInfo.deserialize(input.childOrEmpty("captureInfo"));
        this.timeSinceLastRaid = input.getLongOr("timeSinceLastRaid", 0L);
    }

    @Override
    public BlockPos position() {
        return this.worldPosition;
    }

    public void notifyNearbyPlayers(@NotNull Component textComponent) {
        //noinspection ConstantConditions
        for (Player player : this.level.players()) {
            if (player.distanceToSqr(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()) > FactionConfig.server().raidNotifyDistance.get()) {
                continue;
            }
            player.displayClientMessage(textComponent, true);
        }
    }

    /**
     * initiates a new capture or increases the badomen level of a running capture
     *
     * @param faction          attacking faction
     * @param feedback         interaction feedback supplier if capture cannot be started {@link #capturePreconditions(net.minecraft.core.Holder, java.util.function.BiConsumer)}
     * @param badOmenLevel     level of the badomen effect that triggered the raid (effect amplifier + 1). -1 if not triggered by bad omen.
     * @param strengthModifier modifier of the faction strength ration. See {@link #calculateAttackStrength(int, float)}
     * @return true if the badomen effect should be consumed
     */
    public boolean initiateCaptureOrIncreaseBadOmenLevel(@NotNull Holder<? extends IFaction<?>> faction, @Nullable BiConsumer<Component, Boolean> feedback, int badOmenLevel, float strengthModifier) {
        if (!IFaction.is(faction, de.teamlapen.faction.api.tags.FactionTags.CAN_RAID)) {
            return false;
        }
        if (this.capturingFaction == null) {
            this.initiateCapture(faction, feedback, badOmenLevel, strengthModifier);
            return true;
        }
        if (IFaction.is(this.capturingFaction, faction)) {
            if (this.phase == CAPTURE_PHASE.PHASE_1_OPPOSITE) {
                int tmpBadOmen = this.badOmenLevel;
                float tmpStrength = this.strengthRatio;
                this.calculateAttackStrength(this.badOmenLevel + badOmenLevel, strengthModifier);
                this.captureTimer = this.captureTimer / 2;
                LOGGER.debug("Increase capture from strength {} and badomen level {} to strength {} and badomen level {}", tmpStrength, tmpBadOmen, this.strengthRatio, this.badOmenLevel);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isRaidTriggeredByBadOmen() {
        return this.badOmenLevel >= 0;
    }

    public void ringBell(@NotNull Player playerEntity) {
        if (this.capturingFaction != null) {
            Holder<? extends IPlayableFaction<?>> faction = IFactionHelper.get().getFaction(playerEntity);
            boolean defender = faction == this.controllingFaction;
            boolean attacker = faction == this.capturingFaction;
            List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, getVillageArea());
            for (LivingEntity entity : entities) {
                Holder<? extends IFaction<?>> f = IFactionHelper.get().getFaction(entity);
                if (IFaction.isNeutral(f)) continue;
                if (entity instanceof ICaptureIgnore) {
                    continue;
                } else if (attacker && IFaction.is(this.capturingFaction, f)) {
                    continue;
                } else if (defender && IFaction.is(this.controllingFaction, f)) continue;
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120));
            }
        }
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("isDisabled", this.isDisabled);
        output.putBoolean("isComplete", this.isComplete);
        output.putBoolean("isInsideVillage", this.isInsideVillage);
        output.store("controllingFaction", ModCodecs.faction(), this.controllingFaction);
        if (this.capturingFaction != null) {
            output.store("capturingFaction", ModCodecs.faction(), this.capturingFaction);
            output.putInt("captureTimer", this.captureTimer);
            output.putFloat("strengthRatio", this.strengthRatio);
            output.putInt("captureDuration", this.captureDuration);
            output.putInt("captureAbortTimer", this.captureAbortTimer);
            output.putString("phase", this.phase.name());
        }
        if (!village.isEmpty()) {
            output.store("villageArea", ModCodecs.AABB, this.getVillageArea());
        }
        this.captureInfo.serialize(output.child("captureInfo"));
        output.putInt("badOmenTriggered", this.badOmenLevel);
        output.putLong("timeSinceLastRaid", this.timeSinceLastRaid);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null) {
            if (!this.village.isEmpty()) {
                FactionEventFactory.fireVillageAreaChangedEvent(this, this.villageArea);
            }
        }
    }

    public void setForcedFaction(@Nullable Holder<? extends IFaction<?>> faction) {
        this.forcedFaction = faction;
        this.forcedFactionTimer = 5;
        this.setChanged();
    }

    private boolean unloaded;

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unloaded = true;
    }

    public void onRemovedNotDueTuChunkUnload() {
        //Probably destroyed
        if (this.capturingFaction != null) {
            this.breakCapture();
        } else {
            this.updateBossinfoPlayers(null);
        }
    }

    @Override
    public void setRemoved() {
        FactionEventFactory.fireVillageTotemRemovedEvent(this);
        TotemHelper.removeTotem(this.level.dimension(), this.village, this.worldPosition, true);
        if (!unloaded) {
            // @Volatile: MC calls setRemoved when a chunk unloads now as well (see ServerLevel#unload -> LevelChunk#clearAllBlockEntities).
            // Since we don't want to remove network node data in that case, we need to know if it was removed due to unloading.
            // We can use "unloaded" for that, it's set in #onChunkUnloaded.
            // Since MC first calls #onChunkUnloaded and then #setRemoved, this check keeps working.
            // @Credit raoulvdberge
            // https://github.com/MinecraftForge/MinecraftForge/issues/8302
            onRemovedNotDueTuChunkUnload();
        }
        super.setRemoved();

    }

    public float shouldRenderBeam() {
        if (!this.isComplete || isDisabled || !isInsideVillage) return 0f;
        if (this.capturingFaction == null) return 0f;
        //noinspection ConstantConditions
        int i = (int) (this.level.getGameTime() - this.beamRenderCounter);
        this.beamRenderCounter = this.level.getGameTime();
        if (i > 1) {
            this.beamRenderScale -= (float) i / 40.0f;
            if (this.beamRenderScale < 0f) {
                this.beamRenderScale = 0f;
            }
        }
        this.beamRenderScale += 0.025f;
        if (this.beamRenderScale > 1f) {
            this.beamRenderScale = 1.0f;
        }
        return this.beamRenderScale;
    }

    public static void clientTick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull TotemBlockEntity blockEntity) {
        if (level.getGameTime() % 10 == 7 && !IFaction.isNeutral(blockEntity.controllingFaction)) {
            ParticleUtil.spawnParticlesClient(level, ColorParticleOption.create(FactionParticles.FACTION_SURROUNDING.get(), blockEntity.controllingFaction.value().getColor()), pos.getX(), pos.getY(), pos.getZ(), 3, 30, level.random);
        }
    }

    /**
     * Ticked every second when no capture is active
     * Handle normal village live
     */
    private void serverTickSecondNonCapture(int timeInSeconds) {
        //Update/spawn entities
        if (!IFaction.isNeutral(this.controllingFaction) && timeInSeconds % 16 == 0) {
            int beds = (int) ((ServerLevel) level).getPoiManager().getInRange(pointOfInterestType -> pointOfInterestType.is(PoiTypes.HOME), this.worldPosition, ((int) Math.sqrt(Math.pow(this.getVillageArea().getXsize(), 2) + Math.pow(this.getVillageArea().getZsize(), 2))) / 2, PoiManager.Occupancy.ANY).count();
            boolean spawnTaskMaster = RNG.nextInt(6) == 0;
            int villagerCount = this.level.getEntitiesOfClass(Villager.class, this.getVillageArea().inflate(20)).size();
            int max = Math.min(beds, FactionConfig.server().villageMaxSpawnableVillagers.get());
            if (villagerCount < max) {
                var villager = FactionEventFactory.fireSpawnNewVillagerEvent(this, EntityType.VILLAGER, null);
                if(villager!=null){
                    spawnEntity(villager);
                }
            } else {
                spawnTaskMaster = true;
            }
            if (spawnTaskMaster && this.level.getEntitiesOfClass(LivingEntity.class, this.getVillageArea(), ITaskMasterEntity.class::isInstance).isEmpty()) {
                this.spawnTaskMaster();
            }
            int defenderNumMax = Math.min(6, this.village.size() / 5);
            TagKey<EntityType<?>> villageGuardTypes = this.controllingFaction.value().getVillageData().getVillageGuardTypes();
            if (villageGuardTypes != null) {
                List<Entity> guards = this.level.getEntities((Entity) null, this.getVillageArea(), x -> x.getType().is(villageGuardTypes));
                if (defenderNumMax > guards.size()) {
                    LivingEntity livingEntity = FactionEventFactory.fireCreateCaptureEntityEvent(this, this.controllingFaction);
                    if (livingEntity != null) {
                        this.spawnEntity(livingEntity);
                    }
                }
            }
        }

        //Random raids
        if (timeSinceLastRaid > 12000 && this.level.getDifficulty() != Difficulty.PEACEFUL && this.level.random.nextFloat() < FactionConfig.server().raidRandomChance.get() * 20) {
            List<Holder<IFaction<?>>> factions = ModRegistries.FACTIONS.get(de.teamlapen.faction.api.tags.FactionTags.HAS_RANDOM_RAID).stream().flatMap(HolderSet.ListBacked::stream).collect(Collectors.toList());
            factions.remove(this.controllingFaction);
            this.initiateCapture(factions.get(this.level.random.nextInt(factions.size())), null, 0, -1f);
        }
    }

    /**
     * Ticked every second during raid.
     * Handle raid activity
     */
    private void serverTickSecondCapture(int timeInSeconds) {
        List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, this.getVillageArea());
        this.updateBossinfoPlayers(entities);
        int currentAttacker = 0; //include player
        int attackerPlayer = 0;
        int currentDefender = 0; //include player
        int defenderPlayer = 0;
        int neutral = 0;
        float attackerStrength = 0f;
        float defenderStrength = 0f;
        float attackerHealth = 0;
        float attackerMaxHealth = 0;
        float defenderHealth = 0;
        float defenderMaxHealth = 0;

        //count entities
        CaptureInfo captureInfo = new CaptureInfo(this);
        for (LivingEntity entity : entities) {
            Holder<? extends IFaction<?>> faction = IFactionHelper.get().getFaction(entity);
            if (IFaction.isNeutral(faction)) continue;
            if (entity instanceof ICaptureIgnore) continue;
            if (!entity.isAlive()) continue;
            if (IFaction.is(this.capturingFaction, faction)) {
                currentAttacker++;
                attackerStrength += this.getStrength(entity);
                attackerMaxHealth += entity.getMaxHealth();
                attackerHealth += entity.getHealth();
                if (entity instanceof Player) attackerPlayer++;
                if (entity instanceof IVillageCaptureEntity captureEntity) {
                    captureEntity.attackVillage(captureInfo);
                }
            } else if (IFaction.is(faction, this.controllingFaction)) {
                currentDefender++;
                defenderStrength += this.getStrength(entity);
                defenderMaxHealth += entity.getMaxHealth();
                defenderHealth += entity.getHealth();
                if (entity instanceof Player) defenderPlayer++;
                if (entity instanceof IVillageCaptureEntity captureEntity) {
                    captureEntity.defendVillage(captureInfo);
                }
            } else {
                neutral++;
            }
        }

        if (currentAttacker == 0) {
            this.captureAbortTimer++;
        } else {
            this.captureAbortTimer = 0;
        }

        ++this.captureTimer;
        --this.captureDuration;
        if (this.phase == CAPTURE_PHASE.PHASE_2) {
            this.captureForceTargetTimer++;
        }

        if (this.captureDuration == 0 || this.captureAbortTimer > 10) {
            this.abortCapture();
        } else {
            switch (this.phase) {
                case PHASE_1_NEUTRAL -> {
                    if (this.captureTimer >= FactionConfig.server().raidPhaseOneDuration.get()) {
                        this.captureTimer = 1;
                        this.setupPhase2();
                        this.setChanged();
                    } else {
                        if (this.captureTimer % 2 == 0) {
                            if (attackerStrength < 5) {
                                this.spawnCaptureEntity(this.capturingFaction);
                            }
                        }
                    }
                }
                case PHASE_1_OPPOSITE -> {
                    if (captureTimer >= FactionConfig.server().raidPhaseOneDuration.get()) {
                        captureTimer = 1;
                        this.setupPhase2();
                        this.setChanged();
                        this.notifyNearbyPlayers(Component.translatable("message.factionapi.village_raid.almost_captured", currentDefender));
                    } else {
                        if (captureTimer % 2 == 0) {
                            float max = attackerStrength + defenderStrength;
                            if (attackerStrength / max <= this.strengthRatio) {
                                this.spawnCaptureEntity(this.capturingFaction);
                            } else if (defenderStrength / max <= 1 - this.strengthRatio) {
                                this.spawnCaptureEntity(this.controllingFaction);
                            }
                        }
                    }
                }
                case PHASE_2 -> {
                    if (currentDefender == 0) {
                        captureTimer++;
                        if (captureTimer > 4) {
                            this.completeCapture(true, false);
                        }
                    } else if (currentAttacker == 0) {
                        captureTimer++;
                        if (captureTimer > 4) {
                            this.abortCapture();
                        }
                    } else {
                        captureTimer = 1;
                    }
                }
            }
            this.handleBossBar(defenderMaxHealth, defenderHealth, attackerMaxHealth, attackerHealth);
        }
    }

    public static void serverTick(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull TotemBlockEntity blockEntity) {
        if (blockEntity.isDisabled) {
            level.destroyBlock(pos, true);
            if (level.getBlockState(pos.below()).getBlock() instanceof TotemBaseBlock) {
                level.destroyBlock(pos.below(), true);
            }
        }

        //Update tile status
        long time = level.getGameTime();
        if (time % 20 == 0) {
            blockEntity.updateTileStatus();
        }


        //Update capture progress/states
        if (!blockEntity.checkTileStatus()) {
            if (!blockEntity.isInsideVillage && blockEntity.capturingFaction != null) {
                blockEntity.breakCapture();
            }
            return;
        }
        if (blockEntity.forcedFaction != null) {
            if (blockEntity.forcedFactionTimer > 0) {
                if (blockEntity.forcedFactionTimer == 1) {
                    blockEntity.breakCapture();
                }
                blockEntity.forcedFactionTimer--;
            } else {
                blockEntity.setCapturingFaction(blockEntity.forcedFaction);
                blockEntity.completeCapture(false, true);
                blockEntity.forcedFaction = null;
            }
        }
        if (blockEntity.forceVillageUpdate) {
            blockEntity.updateTileStatus();
            blockEntity.forceVillageUpdate = false;
        }
        if (time % 12000 == 0) {
            blockEntity.updateVillageArea();
        }

        //Capture
        if (blockEntity.capturingFaction != null) {
            if (time % 20 == 0) {
                assert blockEntity.level == level;
                blockEntity.serverTickSecondCapture((int) (time / 20));
            }
        }
        //Normal village life
        else {
            blockEntity.timeSinceLastRaid++;
            if (time % 20 == 7) {
                blockEntity.serverTickSecondNonCapture((int) (time / 20));
            }

        }
    }

    /**
     * updates the tile status and determines the related {@link PoiRecord}s
     * <p>
     * this includes checking if the totem is placed in a village, the totem is complete, if there is another totem and forces a faction
     */
    public void updateTileStatus() {
        if (this.level instanceof ServerLevel serverLevel) {

            Block b = this.level.getBlockState(this.worldPosition).getBlock();
            if (!(this.isComplete = b instanceof TotemTopBlock && serverLevel.getBlockState(this.worldPosition.below()).getBlock().equals(FactionBlocks.TOTEM_BASE.get()))) {
                return;
            }
            Holder<? extends IFaction<?>> blockFaction = ((TotemTopBlock) b).faction;
            if (blockFaction == null) {
                if (!IFaction.isNeutral(this.controllingFaction)) {
                    this.setControllingFaction(DefaultFactions.NEUTRAL);
                }
            } else if (blockFaction != this.controllingFaction) {
                this.setControllingFaction(blockFaction);
            }
            Set<PoiRecord> points = TotemHelper.getVillagePointsOfInterest(serverLevel, this.worldPosition);
            if (!(this.isInsideVillage = !points.isEmpty())) {
                this.village = Collections.emptySet();
                if (!IFaction.isNeutral(this.controllingFaction)) {
                    this.setControllingFaction(DefaultFactions.NEUTRAL);
                }
            } else if (!(7 == TotemHelper.isVillage(points, serverLevel, this.worldPosition, !IFaction.isNeutral(this.controllingFaction) || this.capturingFaction != null))) {
                this.isInsideVillage = false;
                this.village = Collections.emptySet();
                if (!IFaction.isNeutral(this.controllingFaction)) {
                    this.setControllingFaction(DefaultFactions.NEUTRAL);
                }
            } else if (!(this.isDisabled = !TotemHelper.addTotem(serverLevel, points, this.worldPosition))) {
                this.village.removeIf(points::contains);
                TotemHelper.removeTotem(serverLevel.dimension(), this.village, this.worldPosition, false);
                this.village = points;
            } else {
                this.village = Collections.emptySet();
            }
            this.setChanged();
        }
    }

    @SuppressWarnings("ConstantConditions")


    private void applyVictoryBonus(boolean attackWin) {
        for (Player player : level.players()) {
            if (player instanceof ServerPlayer serverPlayer && !serverPlayer.isSpectator() && this.getVillageArea().contains(serverPlayer.position())) {
                Holder<? extends IFaction<?>> playerFaction = IFactionHelper.get().getFaction(serverPlayer);
                if (!serverPlayer.isSpectator() && IFaction.is(playerFaction, (attackWin ? this.capturingFaction : this.controllingFaction))) {
                    if (!attackWin) {
                        serverPlayer.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, Math.max(this.badOmenLevel - 1, 0), false, false, true));
                    }

                    serverPlayer.connection.send(new ClientboundPlaySoundEventPacket(FactionSounds.RAID_WON));

                    serverPlayer.awardStat(FactionStats.WIN_VILLAGE_CAPTURE.get());
                    if (attackWin) {
                        serverPlayer.awardStat(FactionStats.CAPTURE_VILLAGE.get());
                    } else {
                        serverPlayer.awardStat(FactionStats.DEFEND_VILLAGE.get());
                    }
                } else if (IFaction.is(playerFaction, (!attackWin ? this.capturingFaction : this.controllingFaction))) {
                    serverPlayer.connection.send(new ClientboundPlaySoundEventPacket(FactionSounds.RAID_FAILED));
                }
            }
        }
    }

    private boolean capturePreconditions(@Nullable Holder<? extends IFaction<?>> faction, @NotNull BiConsumer<Component, Boolean> feedback) {
        if (faction == null) {
            feedback.accept(Component.translatable("message.factionapi.village_totem.no_faction"), true);
            return false;
        }
        if (capturingFaction != null) {
            feedback.accept(Component.translatable("message.factionapi.village_totem.already_capturing"), true);
            return false;
        }
        if (faction.equals(controllingFaction)) {
            feedback.accept(Component.translatable("message.factionapi.village_totem.same_faction"), true);
            return false;
        }
        if (!isInsideVillage) {
            if (!IFaction.isNeutral(this.controllingFaction)) {
                this.setControllingFaction(DefaultFactions.NEUTRAL);  //Reset the controlling faction only on interaction, not in tick. Maybe village is just temporarily unavailable #417
                this.setChanged();
            }
            //noinspection ConstantConditions
            Map<Integer, Integer> stats = TotemHelper.getVillageStats(TotemHelper.getVillagePointsOfInterest((ServerLevel) level, this.worldPosition), this.level);
            int status = TotemHelper.isVillage(stats, !IFaction.isNeutral(this.controllingFaction) || this.capturingFaction != null);
            MutableComponent text = Component.translatable("message.factionapi.village_totem.no_village");
            if ((status & 1) == 0) {
                text.append("\n").append(Component.translatable("message.factionapi.village_totem.no_village.homes", stats.get(1), TotemHelper.MIN_HOMES));
            }
            if ((status & 2) == 0) {
                text.append("\n").append(Component.translatable("message.factionapi.village_totem.no_village.workstations", stats.get(2), TotemHelper.MIN_WORKSTATIONS));
            }
            if ((status & 4) == 0) {
                text.append("\n").append(Component.translatable("message.factionapi.village_totem.no_village.villagers", stats.get(4), TotemHelper.MIN_VILLAGER));
            }
            feedback.accept(text, false);


            return false;
        }
        if (isDisabled) {
            feedback.accept(Component.translatable("message.factionapi.village_totem.disabled"), true);
            return false;
        }
        FactionVillageEvent.InitiateCapture event = new FactionVillageEvent.InitiateCapture(this, faction);
        NeoForge.EVENT_BUS.post(event);
        if (event.isCaptureDisallowed()) {
            if (event.getMessage() != null) {
                feedback.accept(Component.translatable(event.getMessage()), true);
            }
            return false;
        }
        return true;
    }

    private void completeCapture(boolean notifyPlayer, boolean fullConvert) {
        Preconditions.checkArgument(this.capturingFaction != null);
        this.informEntitiesAboutCaptureStop();

        @Nullable
        Map<LivingEntity, FactionVillageEvent.UpdateCreaturesOnCaptureFinishEvent.Action> replaceEntities = null;
        //noinspection ConstantConditions
        if (!this.level.isClientSide()) {
            replaceEntities = this.updateCreaturesOnCapture(fullConvert);
        }

        this.applyVictoryBonus(true);
        this.setControllingFaction(this.capturingFaction);
        this.setCapturingFaction(null);

        if (replaceEntities != null) {
            replaceEntities.forEach(((livingEntity, action) -> {
                if (action == FactionVillageEvent.UpdateCreaturesOnCaptureFinishEvent.Action.KILL) {
                    livingEntity.discard();
                } else if (action == FactionVillageEvent.UpdateCreaturesOnCaptureFinishEvent.Action.REPLACE) {
                    Villager villager = FactionEventFactory.fireSpawnNewVillagerEvent(this, EntityType.VILLAGER, livingEntity);
                    if (villager == null) return;
                    spawnEntity(villager, livingEntity);
                }
            }));
        }

        if (notifyPlayer) {
            this.notifyNearbyPlayers(Component.translatable("message.factionapi.village_raid.captured_by", controllingFaction.value().getNamePlural()));
        }
        this.updateBossinfoPlayers(null);
        this.setChanged();
    }

    /**
     * sets the strength ratio of attacking and defending factions
     *
     * @param strengthModifier a positive value increases the attacker strength while the defender strength is increased by negative values
     */
    private void calculateAttackStrength(int badOmenLevel, float strengthModifier) {
        this.badOmenLevel = Mth.clamp(badOmenLevel, -1, 5);
        int level = this.badOmenLevel - 1;
        float defenderStrength = 1f;
        float attackerStrength = 1f;
        if (level >= 0) {
            attackerStrength += 0.25f + 0.4375f * level;
        }
        if (strengthModifier > 0) {
            attackerStrength += strengthModifier;
        } else {
            defenderStrength -= strengthModifier;
        }
        Pair<Float, Float> strength = FactionEventFactory.fireDefineRaidStrengthEvent(this, level, defenderStrength, attackerStrength);
        this.strengthRatio = strength.getRight() / (strength.getLeft() + strength.getRight());
    }

    private void informEntitiesAboutCaptureStop() {
        //noinspection ConstantConditions
        if (this.level.isClientSide()) return;
        List<PathfinderMob> list = this.level.getEntitiesOfClass(PathfinderMob.class, this.getVillageArea());
        for (PathfinderMob e : list) {
            if (e instanceof IVillageCaptureEntity captureEntity) {
                captureEntity.stopVillageAttackDefense();
            }
        }
    }

    private boolean checkTileStatus() {
        return this.isComplete && this.isInsideVillage && !this.isDisabled && !this.village.isEmpty();
    }

    private void makeAgressive() {
        if (FactionConfig.server().raidDisableVillageGuards.get()) return;
        //noinspection ConstantConditions
        if (!this.level.isClientSide()) {
            List<Villager> villagerEntities = this.level.getEntitiesOfClass(Villager.class, this.getVillageArea());
            for (Villager villager : villagerEntities) {
                if (villager instanceof IFactionEntity) continue;
                FactionEventFactory.fireMakeAggressive(this, villager);
            }
        }
    }

    private float getStrength(@NotNull LivingEntity entity) {
        return switch (entity) {
            case Player player -> FactionPlayerHandler.get(player).getCurrentLevelRelative();
            case Villager ignored -> 0.4f;
            case ICaptureStrengthProvider provider -> provider.getCaptureStrength();
            default -> 1f;
        };
    }

    private void handleBossBar(float defenderMaxHealth, float defenderHealth, float attackerMaxHealth, float attackerHealth) {
        float neutralPerc;
        switch (this.phase) {
            case PHASE_1_NEUTRAL, PHASE_1_OPPOSITE -> neutralPerc = this.captureTimer / (float) FactionConfig.server().raidPhaseOneDuration.get();
            case PHASE_2 -> {
                neutralPerc = 1f;
                this.captureInfo.setName(Component.translatable("message.factionapi.village_raid.participants_remaining"));
            }
            default -> neutralPerc = 0;
        }
        float max = defenderHealth + attackerHealth;
        this.captureInfo.setPercentage(neutralPerc * attackerHealth / max, 1 - neutralPerc, neutralPerc * defenderHealth / max);
    }

    private void setCapturingFaction(@Nullable Holder<? extends IFaction<?>> faction) {
        this.capturingFaction = faction;
        this.progressColor = faction != null ? new Color(faction.value().getColor()).getRGB() : DyeColor.WHITE.getTextureDiffuseColor();
        if (faction != null) {
            this.captureInfo.setColors(new Color(faction.value().getColor()), Color.WHITE, new Color(this.controllingFaction.value().getColor()));
            Identifier capturingFactionId = faction.unwrapKey().orElseThrow().identifier();
            this.captureInfo.setName(Component.translatable("message." + capturingFactionId.getNamespace() + ".village_raid." + capturingFactionId.getPath()).withStyle(style -> style.withColor(faction.value().getChatColor())));
        }
    }

    private void setControllingFaction(@NotNull Holder<? extends IFaction<?>> faction) {
        this.controllingFaction = faction;
        this.baseColor = new Color(faction.value().getColor()).getRGB();
        if (this.level != null) {
            BlockState oldBlockState = this.getBlockState();
            Block b = oldBlockState.getBlock();
            boolean crafted = b instanceof TotemTopBlock totem && totem.isCrafted();
            BlockState newBlockState = (IFaction.isNeutral(faction) ? crafted ? FactionBlocks.TOTEM_TOP_CRAFTED.get() : FactionBlocks.TOTEM_TOP.get() : faction.value().getVillageData().getTotemTopBlock(crafted)).defaultBlockState();
            try { //https://github.com/TeamLapen/Vampirism/issues/793 no idea what might cause this
                this.level.setBlock(this.worldPosition, newBlockState, 55);
            } catch (IllegalStateException e) {
                LOGGER.error("Setting blockstate from {} to {}", oldBlockState, newBlockState);
                LOGGER.error("Failed to set totem blockstate", e);
            }
        }
    }

    private void setupPhase2() {
        if (this.phase != CAPTURE_PHASE.PHASE_2) {
            this.phase = CAPTURE_PHASE.PHASE_2;
            this.captureInfo.setName(Component.translatable("message.factionapi.village_raid.participants_remaining"));
        }
    }

    private void spawnCaptureEntity(@Nullable Holder<? extends IFaction<?>> faction) {
        if (faction == null) return;

        LivingEntity entity = FactionEventFactory.fireCreateCaptureEntityEvent(this, faction);
        List<? extends Player> players = this.level.players();
        players.removeIf(Player::isSpectator);
        if (entity != null && !SpawnUtil.spawnEntityInWorld((ServerLevel) this.level, this.getVillageAreaReduced(), entity, 50, players, EntitySpawnReason.EVENT)) {
            entity.discard();
            entity = null;
        }
        if (entity instanceof IVillageCaptureEntity captureEntity) {
            if (faction == this.controllingFaction) {
                captureEntity.defendVillage(new CaptureInfo(this));
            } else {
                captureEntity.attackVillage(new CaptureInfo(this));
            }
        } else if (entity != null) {
            LOGGER.warn("Creature registered for village capture does not implement IVillageCaptureEntity ({})", RegUtil.id(entity.getType()));
        } else {
            LOGGER.info("Failed to spawn capture creature");
        }
    }

    private void spawnEntity(@NotNull LivingEntity newEntity) {
        if (level instanceof ServerLevel serverLevel) {
            SpawnUtil.spawnEntityInWorld(serverLevel, this.getVillageAreaReduced(), newEntity, 50, Lists.newArrayList(), EntitySpawnReason.EVENT);
        }
    }

    //accessors for other classes --------------------------------------------------------------------------------------

    private void spawnEntity(@NotNull LivingEntity newEntity, @NotNull LivingEntity oldEntity) {
        newEntity.restoreFrom(oldEntity);
        newEntity.setUUID(Mth.createInsecureUUID());
        SpawnUtil.replaceEntity(oldEntity, newEntity);
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnTaskMaster() {
        assert level instanceof ServerLevel;
        assert this.controllingFaction != null;
        EntityType<? extends ITaskMasterEntity> entity = this.controllingFaction.value().getVillageData().getTaskMasterEntity();
        if (entity != null) {
            ITaskMasterEntity newEntity = entity.create(this.level, EntitySpawnReason.EVENT);
            newEntity.setHome(this.getVillageAreaReduced());
            SpawnUtil.spawnEntityInWorld((ServerLevel) this.level, this.getVillageAreaReduced(), (Entity) newEntity, 25, Lists.newArrayList(), EntitySpawnReason.EVENT);
        }
    }

    //client------------------------------------------------------------------------------------------------------------

    /**
     * Update the bossbar to only include players that are in the given list.
     * If list is null, remove all players
     *
     * @param includedPlayerEntities List of player entities to be included. May contain other non player entities
     */
    private void updateBossinfoPlayers(@Nullable List<LivingEntity> includedPlayerEntities) {
        Set<ServerPlayer> oldList = new HashSet<>(captureInfo.getPlayers());
        if (includedPlayerEntities != null) {
            for (LivingEntity entity : includedPlayerEntities) {
                if (entity instanceof ServerPlayer serverPlayer) {
                    if (!oldList.remove(entity)) {
                        captureInfo.addPlayer(serverPlayer);
                    }
                }
            }
        }
        for (ServerPlayer player : oldList) {
            captureInfo.removePlayer(player);
        }
    }

    private Map<LivingEntity, FactionVillageEvent.UpdateCreaturesOnCaptureFinishEvent.Action> updateCreaturesOnCapture(boolean fullConvert) {
        List<Villager> villagerEntities = this.level.getEntitiesOfClass(Villager.class, getVillageArea());
        if (FactionEventFactory.fireVillagerCaptureEventPre(this, villagerEntities, fullConvert)) {
            return Collections.emptyMap();
        }

        for (Villager villager : villagerEntities) {
            if (villager.getVillagerData().profession().is(FactionProfessionTags.HAS_FACTION)) {
                villager.setVillagerData(villager.getVillagerData().withProfession(level.registryAccess(), VillagerProfession.NONE));
            }
        }

        return FactionEventFactory.fireReplaceEntitiesOnCaptureEvent(this, fullConvert);
    }

    private void updateVillageArea() {
        if (this.villageArea != null && this.villageAreaReduced != null) {
            if (this.village.stream().allMatch(point -> this.villageAreaReduced.contains(Vec3.atLowerCornerOf(point.getPos())))) {
                return;
            }
        }
        AABB totem = TotemHelper.getAABBAroundPOIs(this.village);
        if (totem == null) {
            totem = new AABB(this.worldPosition);
        }
        Optional<StructureStart> start = StructureUtil.getStructureStartAt(this.level, this.worldPosition, StructureTags.VILLAGE);
        if (start.isPresent()) {
            totem = totem.minmax(MBtoAABB(start.get().getBoundingBox()));
        }
        this.villageArea = totem;
        this.villageAreaReduced = totem.inflate(-30, -10, -30);
    }

    @NotNull
    public static AABB MBtoAABB(@NotNull BoundingBox bb) {
        return new AABB(bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ());
    }

    private enum CAPTURE_PHASE {
        PHASE_1_NEUTRAL, PHASE_1_OPPOSITE, PHASE_2
    }

    public static class CaptureInfo implements ICaptureAttributes {
        @Nullable
        private final Holder<? extends IFaction<?>> defendingFaction;
        @Nullable
        private final Holder<? extends IFaction<?>> attackingFaction;
        private final @NotNull AABB villageArea;
        private final @NotNull BlockPos pos;
        private final boolean shouldForceTargets;

        private CaptureInfo(@NotNull TotemBlockEntity totem) {
            this.defendingFaction = totem.controllingFaction;
            this.attackingFaction = totem.capturingFaction;
            this.villageArea = totem.getVillageAreaReduced();
            this.pos = totem.worldPosition;
            this.shouldForceTargets = totem.captureForceTargetTimer > FactionConfig.server().raidPhaseTwoMembersForceTargetTime.get();
        }

        @Nullable
        @Override
        public Holder<? extends IFaction<?>> getAttackingFaction() {
            return this.attackingFaction;
        }

        @Nullable
        @Override
        public Holder<? extends IFaction<?>> getDefendingFaction() {
            return this.defendingFaction;
        }

        @Override
        public BlockPos getPosition() {
            return this.pos;
        }

        @Override
        public @NotNull AABB getVillageArea() {
            return this.villageArea;
        }

        public boolean shouldForceTargets() {
            return this.shouldForceTargets;
        }

    }
}
