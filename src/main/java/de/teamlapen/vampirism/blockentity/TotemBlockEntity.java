package de.teamlapen.vampirism.blockentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.*;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.event.VampirismVillageEvent;
import de.teamlapen.vampirism.api.world.ICaptureAttributes;
import de.teamlapen.vampirism.api.world.ITotem;
import de.teamlapen.vampirism.blocks.TotemBaseBlock;
import de.teamlapen.vampirism.blocks.TotemTopBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.effects.SanguinareEffect;
import de.teamlapen.vampirism.effects.SanguinareEffectInstance;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.FactionVillagerProfession;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.hunter.AggressiveVillagerEntity;
import de.teamlapen.vampirism.entity.hunter.DummyHunterTrainerEntity;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.entity.hunter.HunterTrainerEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.VampirismEventFactory;
import de.teamlapen.vampirism.world.ServerMultiBossEvent;
import de.teamlapen.vampirism.world.VampirismWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiConsumer;

import static de.teamlapen.vampirism.blockentity.TotemHelper.*;

@ParametersAreNonnullByDefault
public class TotemBlockEntity extends BlockEntity implements ITotem {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RNG = new Random();
    private static final ResourceLocation nonFactionTotem = new ResourceLocation("none");

    public static void makeAgressive(Villager villager) {
        AggressiveVillagerEntity hunter = AggressiveVillagerEntity.makeHunter(villager);
        UtilLib.replaceEntity(villager, hunter);
    }

    private final ServerMultiBossEvent captureInfo = new ServerMultiBossEvent(new TranslatableComponent("text.vampirism.village.bossinfo.raid"), BossEvent.BossBarOverlay.NOTCHED_10);
    public long timeSinceLastRaid = 0;
    //block attributes
    private boolean isComplete;
    private boolean isInsideVillage;
    private boolean isDisabled;
    //tile attributes
    @Nonnull
    private Set<PoiRecord> village = Sets.newHashSet();
    /**
     * use {@link #setControllingFaction(IFaction)}
     */
    @Nullable
    private IFaction<?> controllingFaction;
    /**
     * use {@link #setCapturingFaction(IFaction)}
     */
    @Nullable
    private IFaction<?> capturingFaction;
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
    private IFaction<?> forcedFaction;
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
    @OnlyIn(Dist.CLIENT)
    private long beamRenderCounter;
    @OnlyIn(Dist.CLIENT)
    private float beamRenderScale;
    private float[] baseColors = DyeColor.WHITE.getTextureDiffuseColors();
    private float[] progressColor = DyeColor.WHITE.getTextureDiffuseColors();

    public TotemBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.totem, pos, state);
    }

    public void abortCapture() {
        this.applyVictoryBonus(false);
        notifyNearbyPlayers(new TranslatableComponent("text.vampirism.village.defended"));
        breakCapture();
    }

    public void breakCapture() {
        this.setCapturingFaction(null);
        this.forceVillageUpdate = true;
        this.informEntitiesAboutCaptureStop();
        this.updateBossinfoPlayers(null);
        this.captureInfo.clear();
        VampirismWorld.getOpt(this.level).ifPresent(vw -> vw.updateTemporaryArtificialFog(this.worldPosition, null));
        this.setChanged();
    }

    /**
     * checks if the player can remove the totem
     *
     * @param player player to check
     * @return weather he can or not
     */
    public boolean canPlayerRemoveBlock(Player player) {
        if (player.getAbilities().instabuild) return true;
        if (!player.isAlive()) return false;
        @Nullable IFaction<?> faction = VampirismPlayerAttributes.get(player).faction;
        if (faction == this.controllingFaction) {
            if (this.capturingFaction == null) {
                return true;
            } else {
                player.displayClientMessage(new TranslatableComponent("text.vampirism.village.totem_destroy.fail_other_capturing"), true);
                return false;
            }
        } else if (faction == this.capturingFaction) {
            if (this.controllingFaction == null) {
                return true;
            } else {
                player.displayClientMessage(new TranslatableComponent("text.vampirism.village.totem_destroy.fail_other_faction"), true);
                return false;
            }
        } else {
            if (!(this.capturingFaction == null && this.controllingFaction == null)) {
                player.displayClientMessage(new TranslatableComponent("text.vampirism.village.totem_destroy.fail_other_faction"), true);
                return false;
            }
            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getBaseColors() {
        return this.baseColors;
    }

    @Override
    public Optional<EntityType<? extends Mob>> getCaptureEntityForFaction(@Nonnull IFaction<?> faction) {
        return WeightedRandom.getRandomItem(RNG, faction.getVillageData().getCaptureEntries()).map(CaptureEntityEntry::getEntity);
    }

    /**
     * @return 0-100. 80 if in stage 2
     */
    @OnlyIn(Dist.CLIENT)
    public int getCaptureProgress() {
        return this.capturingFaction == null ? 0 : this.phase == CAPTURE_PHASE.PHASE_2 ? 80 : (int) (this.captureTimer / (float) VampirismConfig.BALANCE.viPhase1Duration.get() * 80f);
    }

    @OnlyIn(Dist.CLIENT)
    public float[] getCapturingColors() {
        return this.progressColor;
    }

    @Nullable
    @Override
    public IFaction<?> getCapturingFaction() {
        return capturingFaction;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nullable
    @Override
    public IFaction<?> getControllingFaction() {
        return controllingFaction;
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    /**
     * gets the size of the village
     *
     * @return amount of {@link PoiRecord} related to this village totem
     */
    public int getSize() {
        return this.village.size();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @SuppressWarnings("ConstantConditions")
    public void initiateCapture(Player player) {
        if (!player.isAlive()) return;
        initiateCapture(VampirismPlayerAttributes.get(player).faction, player::displayClientMessage, -1, -1f);
    }

    @Nonnull
    @Override
    public AABB getVillageArea() {
        if (this.villageArea == null) {
            updateVillageArea();
        }
        return this.villageArea;
    }

    @Nonnull
    @Override
    public AABB getVillageAreaReduced() {
        if (this.villageAreaReduced == null) {
            updateVillageArea();
        }
        return this.villageAreaReduced;
    }

    /**
     * @param faction          attacking faction
     * @param feedback         interaction feedback supplier if capture cannot be started {@link #capturePreconditions(IFaction, BiConsumer)}
     * @param badOmenLevel     level of the badomen effect that triggered the raid (effect amplifier + 1). -1 if not triggered by bad omen.
     * @param strengthModifier modifier of the faction strength ration. See {@link #calculateAttackStrength(int, float)}
     */
    public void initiateCapture(IFaction<?> faction, @Nullable BiConsumer<Component, Boolean> feedback, int badOmenLevel, float strengthModifier) {
        this.updateTileStatus();
        if (!this.capturePreconditions(faction, feedback == null ? (a, b) -> {
        } : feedback)) return;
        this.forceVillageUpdate = true;
        this.captureAbortTimer = 0;
        this.captureDuration = 24000;
        this.captureTimer = 0;
        this.captureForceTargetTimer = 0;
        this.setCapturingFaction(faction);
        this.calculateAttackStrength(badOmenLevel, strengthModifier);
        this.timeSinceLastRaid = 0;

        if (this.controllingFaction == null) {
            this.phase = CAPTURE_PHASE.PHASE_1_NEUTRAL;
            this.notifyNearbyPlayers(new TranslatableComponent("text.vampirism.village.neutral_village_under_attack", faction.getNamePlural()));
        } else {
            this.phase = CAPTURE_PHASE.PHASE_1_OPPOSITE;
            this.notifyNearbyPlayers(new TranslatableComponent("text.vampirism.village.faction_village_under_attack", this.controllingFaction.getNamePlural(), faction.getNamePlural()));
        }


        this.setChanged();

        this.makeAgressive();
        LOGGER.debug("Initiated capture with strength {} by {} at {} with badomen level {}", this.strengthRatio, faction.getID(), this.getBlockPos(), badOmenLevel);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.badOmenLevel = compound.getInt("badOmenTriggered");
        this.isDisabled = compound.getBoolean("isDisabled");
        this.isComplete = compound.getBoolean("isComplete");
        this.isInsideVillage = compound.getBoolean("isInsideVillage");
        if (compound.contains("controllingFaction")) {
            this.setControllingFaction(VampirismAPI.factionRegistry().getFactionByID(new ResourceLocation(compound.getString("controllingFaction"))));
        } else {
            this.setControllingFaction(null);
        }
        if (compound.contains("capturingFaction")) {
            this.setCapturingFaction(VampirismAPI.factionRegistry().getFactionByID(new ResourceLocation(compound.getString("capturingFaction"))));
            this.captureTimer = compound.getInt("captureTimer");
            this.captureDuration = compound.getInt("captureDuration");
            this.phase = CAPTURE_PHASE.valueOf(compound.getString("phase"));
            this.strengthRatio = compound.getFloat("strengthRatio");
            this.captureAbortTimer = compound.getInt("captureAbortTimer");
            if (this.phase == CAPTURE_PHASE.PHASE_2) {
                this.setupPhase2();
            }
        } else {
            this.setCapturingFaction(null);
        }
        if (this.level != null) {
            if (compound.contains("villageArea")) {
                VampirismWorld.getOpt(this.level).ifPresent(vw -> {
                    AABB aabb = UtilLib.intToBB(compound.getIntArray("villageArea"));
                    vw.updateArtificialFogBoundingBox(this.worldPosition, this.controllingFaction == VReference.VAMPIRE_FACTION ? aabb : null);
                    if (this.isRaidTriggeredByBadOmen() && this.capturingFaction == VReference.VAMPIRE_FACTION) {
                        vw.updateTemporaryArtificialFog(this.worldPosition, aabb);
                    }
                });
            }
        }
        this.forceVillageUpdate = true;
        ListTag list = compound.getList("captureInfo", 10);
        for (Tag inbt : list) {
            Color color = new Color(((CompoundTag) inbt).getInt("color"), true);
            float perc = ((CompoundTag) inbt).getFloat("perc");
            this.captureInfo.setPercentage(color, perc);
        }
        this.timeSinceLastRaid = compound.getLong("timeSinceLastRaid");
    }

    public void notifyNearbyPlayers(Component textComponent) {
        //noinspection ConstantConditions
        for (Player player : this.level.players()) {
            if (player.distanceToSqr(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ()) > VampirismConfig.BALANCE.viNotifyDistanceSQ.get())
                continue;
            player.displayClientMessage(textComponent, true);
        }
    }

    /**
     * initiates a new capture or increases the badomen level of a running capture
     *
     * @param faction          attacking faction
     * @param feedback         interaction feedback supplier if capture cannot be started {@link #capturePreconditions(IFaction, BiConsumer)}
     * @param badOmenLevel     level of the badomen effect that triggered the raid (effect amplifier + 1). -1 if not triggered by bad omen.
     * @param strengthModifier modifier of the faction strength ration. See {@link #calculateAttackStrength(int, float)}
     * @return true if the badomen effect should be consumed
     */
    public boolean initiateCaptureOrIncreaseBadOmenLevel(IFaction<?> faction, @Nullable BiConsumer<Component, Boolean> feedback, int badOmenLevel, float strengthModifier) {
        if (this.capturingFaction == null) {
            this.initiateCapture(faction, feedback, badOmenLevel, strengthModifier);
            return true;
        }
        if (this.capturingFaction == faction) {
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (hasLevel()) this.handleUpdateTag(pkt.getTag());
    }

    public void ringBell(@Nonnull Player playerEntity) {
        if (this.capturingFaction != null) {
            IPlayableFaction<?> faction = VampirismPlayerAttributes.get(playerEntity).faction;
            boolean defender = faction == this.controllingFaction;
            boolean attacker = faction == this.capturingFaction;
            List<LivingEntity> entities = this.level.getEntitiesOfClass(LivingEntity.class, getVillageArea());
            for (LivingEntity entity : entities) {
                IFaction<?> f = VampirismAPI.factionRegistry().getFaction(entity);
                if (f == null) continue;
                if (entity instanceof ICaptureIgnore) continue;
                else if (attacker && this.capturingFaction == f) continue;
                else if (defender && this.controllingFaction == f) continue;
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120));
            }
        }
    }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("isDisabled", this.isDisabled);
        compound.putBoolean("isComplete", this.isComplete);
        compound.putBoolean("isInsideVillage", this.isInsideVillage);
        if (this.controllingFaction != null) {
            compound.putString("controllingFaction", this.controllingFaction.getID().toString());
        }
        if (this.capturingFaction != null) {
            compound.putString("capturingFaction", this.capturingFaction.getID().toString());
            compound.putInt("captureTimer", this.captureTimer);
            compound.putFloat("strengthRatio", this.strengthRatio);
            compound.putInt("captureDuration", this.captureDuration);
            compound.putInt("captureAbortTimer", this.captureAbortTimer);
            compound.putString("phase", this.phase.name());
        }
        if (!village.isEmpty()) {
            compound.putIntArray("villageArea", UtilLib.bbToInt(this.getVillageArea()));
        }
        ListTag list = new ListTag();
        for (Map.Entry<Color, Float> entry : this.captureInfo.getEntries().entrySet()) {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("color", entry.getKey().getRGB());
            nbt.putFloat("perc", entry.getValue());
            list.add(nbt);
        }
        compound.put("captureInfo", list);
        compound.putInt("badOmenTriggered", this.badOmenLevel);
        compound.putLong("timeSinceLastRaid", this.timeSinceLastRaid);
    }

    @Override
    public void setChanged() {
        if (this.level != null) {
            super.setChanged();
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition), 3);
            if (!this.village.isEmpty()) {
                VampirismWorld.getOpt(this.level).ifPresent(vw -> {
                    vw.updateArtificialFogBoundingBox(this.worldPosition, this.controllingFaction == VReference.VAMPIRE_FACTION ? this.getVillageArea() : null);
                    if (this.isRaidTriggeredByBadOmen() && this.capturingFaction == VReference.VAMPIRE_FACTION) {
                        vw.updateTemporaryArtificialFog(this.worldPosition, this.getVillageArea());
                    }
                });

            }
        }
    }

    public void setForcedFaction(@Nullable IFaction<?> faction) {
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

    public void onRemovedNotDueTuChunkUnload(){
        //Probably destroyed
        if (this.capturingFaction != null) {
            this.breakCapture();
        } else {
            this.updateBossinfoPlayers(null);
        }
    }

    @Override
    public void setRemoved() {
        //noinspection ConstantConditions
        VampirismWorld.getOpt(this.level).ifPresent(vw -> vw.updateArtificialFogBoundingBox(this.worldPosition, null));
        TotemHelper.removeTotem(this.level.dimension(), this.village, this.worldPosition, true);
        if(!unloaded){
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

    @OnlyIn(Dist.CLIENT)
    public float shouldRenderBeam() {
        if (!this.isComplete || isDisabled || !isInsideVillage) return 0f;
        if (this.capturingFaction == null) return 0f;
        //noinspection ConstantConditions
        int i = (int) (this.level.getGameTime() - this.beamRenderCounter);
        this.beamRenderCounter = this.level.getGameTime();
        if (i > 1) {
            this.beamRenderScale -= (float) i / 40.0f;
            if (this.beamRenderScale < 0f)
                this.beamRenderScale = 0f;
        }
        this.beamRenderScale += 0.025f;
        if (this.beamRenderScale > 1f)
            this.beamRenderScale = 1.0f;
        return this.beamRenderScale;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, TotemBlockEntity blockEntity) {
        if (level.getGameTime() % 10 == 7 && blockEntity.controllingFaction != null) {
            ModParticles.spawnParticlesClient(level, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "generic_4"), 20, blockEntity.controllingFaction.getColor(), 0.2F), pos.getX(), pos.getY(), pos.getZ(), 3, 30, level.random);
        }
    }

    /**
     * Ticked every second when no capture is active
     * Handle normal village live
     */
    private void serverTickSecondNonCapture(int timeInSeconds) {
        //Update/spawn entities
        if (this.controllingFaction != null && timeInSeconds % 16  == 0) {
            int beds = (int) ((ServerLevel) level).getPoiManager().getInRange(pointOfInterestType -> pointOfInterestType.equals(PoiType.HOME), this.worldPosition, ((int) Math.sqrt(Math.pow(this.getVillageArea().getXsize(), 2) + Math.pow(this.getVillageArea().getZsize(), 2))) / 2, PoiManager.Occupancy.ANY).count();
            boolean spawnTaskMaster = RNG.nextInt(6) == 0;
            int villager = this.level.getEntitiesOfClass(Villager.class, this.getVillageArea().inflate(20)).size();
            int max = Math.min(beds, VampirismConfig.BALANCE.viMaxVillagerRespawn.get());
            if (villager < max) {
                boolean isConverted = this.controllingFaction == VReference.VAMPIRE_FACTION && RNG.nextBoolean();
                if (isConverted) {
                    this.spawnVillagerVampire();
                } else {
                    this.spawnVillagerDefault(this.controllingFaction == VReference.HUNTER_FACTION);
                }
            } else {
                spawnTaskMaster = true;
            }
            if (spawnTaskMaster && this.level.getEntitiesOfClass(VampirismEntity.class, this.getVillageArea(), entity -> entity instanceof ITaskMasterEntity).isEmpty()) {
                this.spawnTaskMaster();
            }
            int defenderNumMax = Math.min(6, this.village.size() / 5);
            List<? extends Mob> guards = this.level.getEntitiesOfClass(this.controllingFaction.getVillageData().getGuardSuperClass(), this.getVillageArea());
            if (defenderNumMax > guards.size()) {
                getCaptureEntityForFaction(this.controllingFaction).ifPresent(entityType -> this.spawnEntity(entityType.create(this.level)));
            }
        }

        //Random raids
        if (timeSinceLastRaid > 12000 && this.level.getDifficulty() != Difficulty.PEACEFUL && this.level.random.nextFloat() < VampirismConfig.BALANCE.viRandomRaidChance.get()) {
            List<IFaction<?>> factions = Lists.newArrayList(VampirismAPI.factionRegistry().getFactions());
            if (this.controllingFaction != null) {
                factions.remove(this.controllingFaction);
            }
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
            IFaction<?> faction = VampirismAPI.factionRegistry().getFaction(entity);
            if (faction == null) continue;
            if (entity instanceof ICaptureIgnore) continue;
            if (!entity.isAlive()) continue;
            if (this.capturingFaction.equals(faction)) {
                currentAttacker++;
                attackerStrength += this.getStrength(entity);
                attackerMaxHealth += entity.getMaxHealth();
                attackerHealth += entity.getHealth();
                if (entity instanceof Player) attackerPlayer++;
                if (entity instanceof IVillageCaptureEntity) {
                    ((IVillageCaptureEntity) entity).attackVillage(captureInfo);
                }
            } else if (faction.equals(this.controllingFaction)) {
                currentDefender++;
                defenderStrength += this.getStrength(entity);
                defenderMaxHealth += entity.getMaxHealth();
                defenderHealth += entity.getHealth();
                if (entity instanceof Player) defenderPlayer++;
                if (entity instanceof IVillageCaptureEntity) {
                    ((IVillageCaptureEntity) entity).defendVillage(captureInfo);
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
                case PHASE_1_NEUTRAL:
                    if (this.captureTimer >= VampirismConfig.BALANCE.viPhase1Duration.get()) {
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
                    break;
                case PHASE_1_OPPOSITE:
                    if (captureTimer >= VampirismConfig.BALANCE.viPhase1Duration.get()) {
                        captureTimer = 1;
                        this.setupPhase2();
                        this.setChanged();
                        this.notifyNearbyPlayers(new TranslatableComponent("text.vampirism.village.almost_captured", currentDefender));
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
                    break;
                case PHASE_2:
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
                    break;
                default:
                    break;
            }
            this.handleBossBar(defenderMaxHealth, defenderHealth, attackerMaxHealth, attackerHealth);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TotemBlockEntity blockEntity) {
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
                blockEntity.serverTickSecondCapture((int)(time / 20));
            }
        }
        //Normal village life
        else {
            blockEntity.timeSinceLastRaid++;
            if (time % 20 == 7) {
                blockEntity.serverTickSecondNonCapture( (int)(time /20) );
            }

        }
    }

    /**
     * updates the tile status and determines the related {@link PoiRecord}s
     * <p>
     * this includes checking if the totem is placed in a village, the totem is complete, if there is another totem and forces a faction
     */
    public void updateTileStatus() {
        if (!(this.level instanceof ServerLevel)) return;

        Block b = this.level.getBlockState(this.worldPosition).getBlock();
        if (!(this.isComplete = b instanceof TotemTopBlock && this.level.getBlockState(this.worldPosition.below()).getBlock().equals(ModBlocks.totem_base.get())))
            return;
        ResourceLocation blockFaction = ((TotemTopBlock) b).faction;
        if (!(blockFaction.equals(this.controllingFaction == null ? nonFactionTotem : this.controllingFaction.getID()))) { //If block faction does not match tile faction, force the tile to update to the block faction
            this.forcedFaction = VampirismAPI.factionRegistry().getFactionByID(blockFaction);
        }
        Set<PoiRecord> points = TotemHelper.getVillagePointsOfInterest((ServerLevel) level, this.worldPosition);
        if (!(this.isInsideVillage = !points.isEmpty())) {
            this.village = Collections.emptySet();
            if (this.controllingFaction != null) {
                this.setControllingFaction(null);
            }
        } else if (!(7 == TotemHelper.isVillage(points, (ServerLevel) this.level, this.worldPosition, this.controllingFaction != null || this.capturingFaction != null))) {
            this.isInsideVillage = false;
            this.village = Collections.emptySet();
            if (this.controllingFaction != null) {
                this.setControllingFaction(null);
            }
        } else if (!(this.isDisabled = !TotemHelper.addTotem((ServerLevel) this.level, points, this.worldPosition))) {
            this.village.removeIf(points::contains);
            TotemHelper.removeTotem(this.level.dimension(), this.village, this.worldPosition, false);
            this.village = points;
        } else {
            this.village = Collections.emptySet();
        }
        this.setChanged();
    }

    @SuppressWarnings("ConstantConditions")
    public void updateTrainer(boolean toDummy) {
        List<? extends VampirismEntity> trainer;
        EntityType<? extends VampirismEntity> entityType;
        if (toDummy) {
            trainer = this.level.getEntitiesOfClass(HunterTrainerEntity.class, this.getVillageArea());
            entityType = ModEntities.hunter_trainer_dummy;
        } else {
            trainer = this.level.getEntitiesOfClass(DummyHunterTrainerEntity.class, this.getVillageArea());
            entityType = ModEntities.hunter_trainer;
        }
        for (VampirismEntity oldEntity : trainer) {
            VampirismEntity newEntity = entityType.create(this.level);
            if (newEntity == null) continue;
            newEntity.restoreFrom(oldEntity);
            newEntity.setUUID(Mth.createInsecureUUID());
            newEntity.setInvulnerable(true);
            UtilLib.replaceEntity(oldEntity, newEntity);
        }
    }

    private void applyVictoryBonus(boolean attackWin) {
        for (Player player : level.players()) {
            if (!player.isSpectator() && this.getVillageArea().contains(player.position())) {
                if (!player.isSpectator() && VampirismAPI.factionRegistry().getFaction(player) == (attackWin ? this.capturingFaction : this.controllingFaction)) {
                    if (!attackWin) {
                        player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, Math.max(this.badOmenLevel - 1, 0), false, false, true));
                    }
                    player.awardStat(ModStats.win_village_capture);
                    if (attackWin) {
                        player.awardStat(ModStats.capture_village);
                    } else {
                        player.awardStat(ModStats.defend_village);
                    }
                }
            }
        }
    }

    private boolean capturePreconditions(@Nullable IFaction<?> faction, @Nonnull BiConsumer<Component, Boolean> feedback) {
        if (faction == null) {
            feedback.accept(new TranslatableComponent("text.vampirism.village.no_faction"), true);
            return false;
        }
        if (capturingFaction != null) {
            feedback.accept(new TranslatableComponent("text.vampirism.village.capturing_in_progress"), true);
            return false;
        }
        if (faction.equals(controllingFaction)) {
            feedback.accept(new TranslatableComponent("text.vampirism.village.same_faction"), true);
            return false;
        }
        if (!isInsideVillage) {
            if (getControllingFaction() != null) {
                this.setControllingFaction(null);  //Reset the controlling faction only on interaction, not in tick. Maybe village is just temporarily unavailable #417
                this.setChanged();
            }
            //noinspection ConstantConditions
            Map<Integer, Integer> stats = TotemHelper.getVillageStats(TotemHelper.getVillagePointsOfInterest((ServerLevel) level, this.worldPosition), this.level);
            int status = TotemHelper.isVillage(stats, this.controllingFaction != null || this.capturingFaction != null);
            MutableComponent text = new TranslatableComponent("text.vampirism.village.missing_components");
            if ((status & 1) == 0) {
                text.append("\n  - ");
                text.append(new TranslatableComponent("text.vampirism.village.missing_components.home"));
                text.append(" " + stats.get(1) + "/" + MIN_HOMES);
            }
            if ((status & 2) == 0) {
                text.append("\n  - ");
                text.append(new TranslatableComponent("text.vampirism.village.missing_components.workstations"));
                text.append(" " + stats.get(2) + "/" + MIN_WORKSTATIONS);
            }
            if ((status & 4) == 0) {
                text.append("\n  - ");
                text.append(new TranslatableComponent("text.vampirism.village.missing_components.villager"));
                text.append(" " + stats.get(4) + "/" + MIN_VILLAGER);
            }
            feedback.accept(text, false);


            return false;
        }
        if (isDisabled) {
            feedback.accept(new TranslatableComponent("text.vampirism.village.othertotem"), true);
            return false;
        }
        VampirismVillageEvent.InitiateCapture event = new VampirismVillageEvent.InitiateCapture(this, faction);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult().equals(Event.Result.DENY)) {
            feedback.accept(new TranslatableComponent(event.getMessage()), true);
            return false;
        }
        return true;
    }

    private void completeCapture(boolean notifyPlayer, boolean fullConvert) {
        this.informEntitiesAboutCaptureStop();
        //noinspection ConstantConditions
        if (!this.level.isClientSide) {
            this.updateCreaturesOnCapture(fullConvert);
        }

        this.applyVictoryBonus(true);
        this.setControllingFaction(this.capturingFaction);
        this.setCapturingFaction(null);

        if (notifyPlayer) {
            assert controllingFaction != null;
            this.notifyNearbyPlayers(new TranslatableComponent("text.vampirism.village.village_captured_by", controllingFaction.getNamePlural()));
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
        Pair<Float, Float> strength = VampirismEventFactory.fireDefineRaidStrengthEvent(this, level, defenderStrength, attackerStrength);
        this.strengthRatio = strength.getRight() / (strength.getLeft() + strength.getRight());
    }

    private void informEntitiesAboutCaptureStop() {
        //noinspection ConstantConditions
        if (this.level.isClientSide) return;
        List<PathfinderMob> list = this.level.getEntitiesOfClass(PathfinderMob.class, this.getVillageArea());
        for (PathfinderMob e : list) {
            if (e instanceof IVillageCaptureEntity) {
                ((IVillageCaptureEntity) e).stopVillageAttackDefense();
            }
        }
    }

    private boolean checkTileStatus() {
        return this.isComplete && this.isInsideVillage && !this.isDisabled && !this.village.isEmpty();
    }

    private void makeAgressive() {
        if (VampirismConfig.SERVER.disableVillageGuards.get()) return;
        //noinspection ConstantConditions
        if (!this.level.isClientSide) {
            List<Villager> villagerEntities = this.level.getEntitiesOfClass(Villager.class, this.getVillageArea());
            for (Villager villager : villagerEntities) {
                if (VampirismEventFactory.fireMakeAggressive(this, villager)) {
                    if (VReference.VAMPIRE_FACTION.equals(this.capturingFaction)) {
                        if (villager instanceof IFactionEntity) continue;
                        if (villager.getAge() < 0) continue;
                        if (RNG.nextInt(3) == 0) {
                            makeAgressive(villager);
                        }
                    }
                }
            }
        }
    }

    private float getStrength(LivingEntity entity) {
        if (entity instanceof Player)
            return FactionPlayerHandler.getOpt((Player) entity).map(FactionPlayerHandler::getCurrentLevelRelative).orElse(0f);
        if (entity instanceof ConvertedVillagerEntity)
            return 0.5f;
        if (entity instanceof IAggressiveVillager)
            return 0.7f;
        if (entity instanceof Villager)
            return 0.4f;
        return 1f;
    }

    private void handleBossBar(float defenderMaxHealth, float defenderHealth, float attackerMaxHealth, float attackerHealth) {
        float neutralPerc;
        switch (this.phase) {
            case PHASE_1_NEUTRAL, PHASE_1_OPPOSITE -> neutralPerc = this.captureTimer / (float) VampirismConfig.BALANCE.viPhase1Duration.get();
            case PHASE_2 -> {
                neutralPerc = 1f;
                this.captureInfo.setName(new TranslatableComponent("text.vampirism.village.bossinfo.remaining"));
            }
            default -> neutralPerc = 0;
        }
        float max = defenderHealth + attackerHealth;
        this.captureInfo.setPercentage(neutralPerc * attackerHealth / max, 1 - neutralPerc, neutralPerc * defenderHealth / max);
    }

    private void setCapturingFaction(@Nullable IFaction<?> faction) {
        this.capturingFaction = faction;
        this.progressColor = faction != null ? new Color(faction.getColor()).getRGBColorComponents() : DyeColor.WHITE.getTextureDiffuseColors();
        if (faction != null) {
            this.captureInfo.setColors(new Color(faction.getColor()), Color.WHITE, this.controllingFaction == null ? Color.WHITE : new Color(this.controllingFaction.getColor()));
            this.captureInfo.setName(new TranslatableComponent("text.vampirism.village.bossinfo.raid", faction.getName().plainCopy().withStyle(style -> style.withColor((faction.getChatColor())))));
        }
    }

    private void setControllingFaction(@Nullable IFaction<?> faction) {
        this.controllingFaction = faction;
        this.baseColors = faction != null ? new Color(faction.getColor()).getRGBColorComponents() : DyeColor.WHITE.getTextureDiffuseColors();
        if (this.level != null) {
            BlockState oldBlockState = this.getBlockState();
            Block b = oldBlockState.getBlock();
            boolean crafted = b instanceof TotemTopBlock && ((TotemTopBlock) b).isCrafted();
            BlockState newBlockState = (faction == null ? crafted ? ModBlocks.totem_top_crafted.get() : ModBlocks.totem_top.get() : faction.getVillageData().getTotemTopBlock(crafted)).defaultBlockState();
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
            this.captureInfo.setName(new TranslatableComponent("text.vampirism.village.bossinfo.remaining"));
        }
    }

    private void spawnCaptureEntity(@Nullable IFaction<?> faction) {
        if (faction == null) return;
        assert this.level instanceof ServerLevel;
        EntityType<? extends Mob> entityType = this.getCaptureEntityForFaction(faction).orElse(null);
        if (entityType == null) {
            LOGGER.warn("No village capture entity registered for {}", faction);
            return;
        }
        Mob entity = entityType.create(this.level);
        if (entity instanceof VampireBaseEntity)
            ((VampireBaseEntity) entity).setSpawnRestriction(VampireBaseEntity.SpawnRestriction.SIMPLE);
        List<? extends Player> players = this.level.players();
        players.removeIf(Player::isSpectator);
        if (entity != null && !UtilLib.spawnEntityInWorld((ServerLevel) this.level, this.getVillageAreaReduced(), entity, 50, players, MobSpawnType.EVENT)) {
            entity.discard();
            entity = null;
        }
        if (entity instanceof IVillageCaptureEntity) {
            if (faction == this.controllingFaction)
                ((IVillageCaptureEntity) entity).defendVillage(new CaptureInfo(this));
            else
                ((IVillageCaptureEntity) entity).attackVillage(new CaptureInfo(this));
        } else if (entity != null) {
            LOGGER.warn("Creature registered for village capture does not implement IVillageCaptureEntity ({})", entityType.getRegistryName());
        } else {
            LOGGER.info("Failed to spawn capture creature");
        }
    }

    private void spawnEntity(Mob newEntity) {
        assert level instanceof ServerLevel;
        UtilLib.spawnEntityInWorld((ServerLevel) this.level, this.getVillageAreaReduced(), newEntity, 50, Lists.newArrayList(), MobSpawnType.EVENT);
    }

    //accessors for other classes --------------------------------------------------------------------------------------

    private void spawnEntity(Mob newEntity, Mob oldEntity, boolean replaceOld, boolean copyData) {
        if (copyData) {
            newEntity.restoreFrom(oldEntity);
        } else {
            newEntity.copyPosition(oldEntity);
        }
        newEntity.setUUID(Mth.createInsecureUUID());
        assert this.level != null;
        if (replaceOld) {
            UtilLib.replaceEntity(oldEntity, newEntity);
        } else {
            this.level.addFreshEntity(newEntity);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnTaskMaster() {
        assert level instanceof ServerLevel;
        assert this.controllingFaction != null;
        EntityType<? extends ITaskMasterEntity> entity = this.controllingFaction.getVillageData().getTaskMasterEntity();
        if (entity != null) {
            ITaskMasterEntity newEntity = entity.create(this.level);
            newEntity.setHome(this.getVillageAreaReduced());
            UtilLib.spawnEntityInWorld((ServerLevel) this.level, this.getVillageAreaReduced(), (Entity) newEntity, 25, Lists.newArrayList(), MobSpawnType.EVENT);
        }
    }

    private void spawnVillagerDefault(boolean poisonousBlood) {
        //noinspection ConstantConditions
        Villager newVillager = EntityType.VILLAGER.create(this.level);
        //noinspection ConstantConditions
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        newVillager = VampirismEventFactory.fireSpawnNewVillagerEvent(this, null, newVillager, false, poisonousBlood);
        spawnEntity(newVillager);
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnVillagerReplace(Mob oldEntity, boolean poisonousBlood) {
        Villager newVillager = EntityType.VILLAGER.create(this.level);
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        if (oldEntity instanceof Villager)
            newVillager.restrictTo(oldEntity.getRestrictCenter(), (int) oldEntity.getRestrictRadius());
        newVillager = VampirismEventFactory.fireSpawnNewVillagerEvent(this, oldEntity, newVillager, true, poisonousBlood);
        spawnEntity(newVillager, oldEntity, true, true);
    }

    //client------------------------------------------------------------------------------------------------------------

    @SuppressWarnings("ConstantConditions")
    private void spawnVillagerReplaceForced(Mob oldEntity, boolean poisonousBlood) {
        Villager newVillager = EntityType.VILLAGER.create(this.level);
        ExtendedCreature.getSafe(newVillager).ifPresent(e -> e.setPoisonousBlood(poisonousBlood));
        newVillager.copyPosition(oldEntity);
        if (oldEntity instanceof Villager) {
            newVillager.restrictTo(oldEntity.getRestrictCenter(), (int) oldEntity.getRestrictRadius());
        }
        newVillager = VampirismEventFactory.fireSpawnNewVillagerEvent(this, oldEntity, newVillager, true, poisonousBlood);
        UtilLib.replaceEntity(oldEntity, newVillager);
    }

    @SuppressWarnings("ConstantConditions")
    private void spawnVillagerVampire() {
        this.spawnEntity(ModEntities.villager_converted.create(this.level));
    }

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
                if (entity instanceof ServerPlayer) {
                    if (!oldList.remove(entity)) {
                        captureInfo.addPlayer(((ServerPlayer) entity));
                    }
                }
            }
        }
        for (ServerPlayer player : oldList) {
            captureInfo.removePlayer(player);
        }
    }

    private void updateCreaturesOnCapture(boolean fullConvert) {
        //noinspection ConstantConditions
        List<Villager> villagerEntities = this.level.getEntitiesOfClass(Villager.class, getVillageArea());
        if (VampirismEventFactory.fireVillagerCaptureEventPre(this, villagerEntities, fullConvert)) {
            return;
        }
        if (VReference.HUNTER_FACTION.equals(this.capturingFaction)) {
            List<HunterBaseEntity> hunterEntities = this.level.getEntitiesOfClass(HunterBaseEntity.class, getVillageArea());
            int i = Math.max(2, hunterEntities.size() / 2);
            for (HunterBaseEntity hunter : hunterEntities) {
                if (hunter instanceof ICaptureIgnore)
                    continue;
                if (i-- > 0) {
                    this.spawnVillagerReplace(hunter, true);
                }
            }
            for (int o = i; o > 0; o--) {
                this.spawnVillagerDefault(true);
            }
            for (Villager villager : villagerEntities) {
                ExtendedCreature.getSafe(villager).ifPresent(e -> e.setPoisonousBlood(true));
            }
            this.updateTrainer(false);

        } else if (VReference.HUNTER_FACTION.equals(this.controllingFaction)) {
            updateTrainer(true);
            for (Villager villager : villagerEntities) {
                ExtendedCreature.getSafe(villager).ifPresent(e -> e.setPoisonousBlood(false));
            }

            if (fullConvert) {
                List<HunterBaseEntity> hunterEntities = this.level.getEntitiesOfClass(HunterBaseEntity.class, getVillageArea());
                for (HunterBaseEntity hunter : hunterEntities) {
                    if (hunter instanceof ICaptureIgnore)
                        continue;
                    this.getCaptureEntityForFaction(this.capturingFaction).ifPresent(type -> this.spawnEntity(type.create(this.level), hunter, true, false));
                }
            }
        } else {
            updateTrainer(true);
        }

        if (VReference.VAMPIRE_FACTION.equals(this.capturingFaction)) {
            for (Villager villager : villagerEntities) {
                if (!fullConvert) {
                    if (RNG.nextInt(2) == 1) continue;
                    SanguinareEffect.addRandom(villager, false);
                } else {
                    villager.addEffect(new SanguinareEffectInstance(11));
                }
            }

        } else if (VReference.VAMPIRE_FACTION.equals(this.controllingFaction)) {
            for (Villager villager : villagerEntities) {
                if (villager.hasEffect(ModEffects.sanguinare))
                    villager.removeEffect(ModEffects.sanguinare);
                if (fullConvert) {
                    if (villager instanceof ConvertedVillagerEntity) {
                        this.spawnVillagerReplaceForced(villager, this.capturingFaction == VReference.HUNTER_FACTION);
                    }
                }
            }
            if (fullConvert) {
                List<VampireBaseEntity> vampireEntities = this.level.getEntitiesOfClass(VampireBaseEntity.class, getVillageArea());
                for (VampireBaseEntity vampire : vampireEntities) {
                    if (vampire instanceof ICaptureIgnore)
                        continue;
                    this.getCaptureEntityForFaction(this.capturingFaction).ifPresent(type -> this.spawnEntity(type.create(this.level), vampire, true, false));
                }
            }
        }

        villagerEntities = this.level.getEntitiesOfClass(Villager.class, getVillageArea());

        for (Villager villager : villagerEntities) {
            if (villager.getVillagerData().getProfession() instanceof FactionVillagerProfession) {
                villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.NONE));
            }
        }
        VampirismEventFactory.fireVillagerCaptureEventPost(this, villagerEntities, fullConvert);
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
        StructureStart start = UtilLib.getStructureStartAt(this.level, this.worldPosition, StructureFeature.VILLAGE);
        if (start != null && start != StructureStart.INVALID_START && start.isValid()) {
            totem = totem.minmax(UtilLib.MBtoAABB(start.getBoundingBox()));
        }
        this.villageArea = totem;
        this.villageAreaReduced = totem.inflate(-30, -10, -30);
    }

    private enum CAPTURE_PHASE {
        PHASE_1_NEUTRAL, PHASE_1_OPPOSITE, PHASE_2
    }

    public static class CaptureInfo implements ICaptureAttributes {
        @Nullable
        private final IFaction<?> defendingFaction;
        @Nullable
        private final IFaction<?> attackingFaction;
        private final AABB villageArea;
        private final BlockPos pos;
        private final boolean shouldForceTargets;

        private CaptureInfo(TotemBlockEntity totem) {
            this.defendingFaction = totem.controllingFaction;
            this.attackingFaction = totem.capturingFaction;
            this.villageArea = totem.getVillageAreaReduced();
            this.pos = totem.worldPosition;
            this.shouldForceTargets = totem.captureForceTargetTimer > VampirismConfig.BALANCE.viForceTargetTime.get();
        }

        @Nullable
        @Override
        public IFaction<?> getAttackingFaction() {
            return this.attackingFaction;
        }

        @Nullable
        @Override
        public IFaction<?> getDefendingFaction() {
            return this.defendingFaction;
        }

        @Override
        public BlockPos getPosition() {
            return this.pos;
        }

        @Override
        public AABB getVillageArea() {
            return this.villageArea;
        }

        public boolean shouldForceTargets() {
            return this.shouldForceTargets;
        }

    }
}
