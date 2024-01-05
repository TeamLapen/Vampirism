package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.Lists;
import com.mojang.serialization.Dynamic;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModAi;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.VampirismVillagerEntity;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.entity.villager.Trades;
import de.teamlapen.vampirism.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Vampire Villager
 */
public class ConvertedVillagerEntity extends VampirismVillagerEntity implements CurableConvertedCreature<Villager, ConvertedVillagerEntity> {

    public static final @NotNull List<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES;
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedVillagerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> OVERLAY_TEXTURE = SynchedEntityData.defineId(ConvertedVillagerEntity.class, EntityDataSerializers.STRING);

    static {
        SENSOR_TYPES = Lists.newArrayList(Villager.SENSOR_TYPES);
        SENSOR_TYPES.remove(SensorType.VILLAGER_HOSTILES);
        SENSOR_TYPES.add(ModAi.VAMPIRE_VILLAGER_HOSTILES.get());
    }

    private @NotNull EnumStrength garlicCache = EnumStrength.NONE;
    private boolean sundamageCache;
    private int bloodTimer = 0;
    private int conversionTime;
    private @Nullable UUID conversationStarter;
    private final Data<Villager> convertedData = new Data<>();

    public ConvertedVillagerEntity(EntityType<? extends ConvertedVillagerEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.addAdditionalSaveDataC(compound);
    }

    @Override
    public void aiStep() {
        aiStepC(EntityType.VILLAGER);
        bloodTimer++;
        super.aiStep();
    }

    /**
     *
     * For vampire expert villagers on server side this will return an asynchronously found block pos (or empty if still searching).
     * For all other villagers it returns an empty optional
     * @return The location of the closest vampire forest if available
     */
    public Optional<BlockPos> getClosestVampireForest(Level level, BlockPos blockPos) {
        return level instanceof ServerLevel serverLevel ? TotemHelper.getTotemNearPos(serverLevel, blockPos, true).flatMap(TotemBlockEntity::getVampireForestLocation) : Optional.empty();
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public @NotNull Villager cureEntity(@NotNull ServerLevel world, @NotNull PathfinderMob entity, @NotNull EntityType<Villager> newType) {
        Villager villager = CurableConvertedCreature.super.cureEntity(world, entity, newType);
        if (this.conversationStarter != null) {
            Player playerentity = world.getPlayerByUUID(this.conversationStarter);
            if (playerentity instanceof ServerPlayer) {
                ModAdvancements.TRIGGER_CURED_VAMPIRE_VILLAGER.trigger((ServerPlayer) playerentity, this, villager);
                world.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, playerentity, villager);
            }
        }
        return villager;
    }

    @Override
    public boolean doHurtTarget(@NotNull Entity entity) {
        if (!level().isClientSide && wantsBlood() && entity instanceof Player player && !Helper.isHunter(player) && !UtilLib.canReallySee(player, this, true)) {
            int amt = VampirePlayer.getOpt(player).map(vampire -> vampire.onBite(this)).orElse(0);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION, new DrinkBloodContext(player));
            return true;
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public @NotNull EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    public @NotNull EntityDataAccessor<String> getSourceEntityDataParam() {
        return OVERLAY_TEXTURE;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining, IDrinkBloodContext drinkContext) {
        BloodDrinkEvent.@NotNull EntityDrinkBloodEvent event = VampirismEventFactory.fireVampireDrinkBlood(this, amt, saturationMod, useRemaining, drinkContext);
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, event.getAmount() * 20));
        bloodTimer = -1200 - random.nextInt(1200);
    }

    @Override
    public @NotNull LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    public void handleEntityEventSuper(byte id) {
        super.handleEntityEvent(id);
    }

    @Override
    public InteractionResult mobInteractSuper(@NotNull Player player, @NotNull InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurtSuper(DamageSource damageSource, float amount) {
        return super.hurt(damageSource, amount);
    }

    @Override
    public boolean hurt(@NotNull DamageSource src, float amount) {
        return this.hurtC(src, amount);
    }

    @Override
    protected @NotNull Component getTypeName() {
        ResourceLocation profName = RegUtil.id(this.getVillagerData().getProfession());
        return Component.translatable(EntityType.VILLAGER.getDescriptionId() + '.' + (!"minecraft".equals(profName.getNamespace()) ? profName.getNamespace() + '.' : "") + profName.getPath());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (!handleSound(id, this)) {
            super.handleEntityEvent(id);
        }
    }

    @NotNull
    @Override
    public EnumStrength isGettingGarlicDamage(LevelAccessor iWorld, boolean forceRefresh) {
        if (forceRefresh) {
            garlicCache = Helper.getGarlicStrength(this, iWorld);
        }
        return garlicCache;
    }

    @Override
    public boolean isGettingSundamage(LevelAccessor iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.level().getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    @NotNull
    @Override
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return this.mobInteractC(player, hand);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.readAdditionalSaveDataC(compound);
    }

    @Override
    public void registerBrainGoals(@NotNull Brain<Villager> brain) {
        super.registerBrainGoals(brain);
        if (!this.isBaby()) {
            brain.setSchedule(ModVillage.CONVERTED_DEFAULT.get());
            brain.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
        }
    }

    @Override
    public boolean wantsBlood() {
        return bloodTimer > 0;
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, amt * 20));
        bloodTimer = 0;
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.registerConvertingData(this);
    }

    /**
     * copied from {@link Villager#makeBrain(Dynamic)} but with {@link #SENSOR_TYPES}, where {@link SensorType#VILLAGER_HOSTILES} is replaced by {@link ModVillage#VAMPIRE_VILLAGER_HOSTILES}
     */
    @NotNull
    @Override
    protected Brain<?> makeBrain(@NotNull Dynamic<?> dynamicIn) {
        Brain<Villager> brain = Brain.provider(MEMORY_TYPES, SENSOR_TYPES).makeBrain(dynamicIn);
        this.registerBrainGoals(brain);
        return brain;
    }

    @Override
    protected void updateTrades() {
        super.updateTrades();
        if (!this.getOffers().isEmpty() && this.getRandom().nextInt(3) == 0) {
            this.addOffersFromItemListings(this.getOffers(), Trades.converted_trades, 1);
        }
    }

    @Override
    public Data<Villager> data() {
        return this.convertedData;
    }

    @Override
    public void die(@NotNull DamageSource pCause) {
        super.die(pCause);
        this.dieC(pCause);
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        this.tickDeathC();
    }

}
