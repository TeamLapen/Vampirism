package de.teamlapen.vampirism.common.world.entity.converted;

import de.teamlapen.faction.api.world.entities.ICaptureStrengthProvider;
import de.teamlapen.faction.common.util.TotemHelper;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismApi;
import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.api.util.VampirismEventFactory;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.attachments.NearestVillage;
import de.teamlapen.vampirism.common.world.entity.VampirismVillagerEntity;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.vampire.DrinkBloodContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Vampire Villager
 */
public class ConvertedVillagerEntity extends VampirismVillagerEntity implements CurableConvertedCreature<Villager, ConvertedVillagerEntity>, ICaptureStrengthProvider {

    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedVillagerEntity.class, EntityDataSerializers.BOOLEAN);

    protected static final Brain.Provider<Villager> BRAIN_PROVIDER;


    static {
        var old = Villager.BRAIN_PROVIDER;
        List<SensorType<? extends Sensor<? super Villager>>> sensorTypes = new ArrayList<>(old.sensorTypes);
        sensorTypes.remove(SensorType.VILLAGER_HOSTILES);
        sensorTypes.add(ModAi.VAMPIRE_VILLAGER_HOSTILES.get());
        BRAIN_PROVIDER = Brain.provider(sensorTypes,old.activities);
    }

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return VampirismVillagerEntity.getAttributeBuilder().add(ModAttributes.SUNDAMAGE);
    }

    private @NotNull EnumStrength garlicCache = EnumStrength.NONE;
    private boolean sundamageCache;
    private int bloodTimer = 0;
    private final Data<Villager> convertedData = new Data<>();

    public ConvertedVillagerEntity(EntityType<? extends ConvertedVillagerEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public @NotNull EntityType<Villager> getCuredEntityType() {
        return EntityType.VILLAGER;
    }

    @Override
    public float getCaptureStrength() {
        return 0.5f;
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.addAdditionalSaveDataC(output);
    }

    @Override
    public void aiStep() {
        aiStepCommonC();
        if (this.level() instanceof ServerLevel serverLevel) {
            aiStepC(serverLevel);
        }
        bloodTimer++;
        super.aiStep();
    }

    /**
     * For vampire expert villagers on server side this will return an asynchronously found block pos (or empty if still searching).
     * For all other villagers it returns an empty optional
     *
     * @return The location of the closest vampire forest if available
     */
    public Optional<BlockPos> getClosestVampireForest(Level level, BlockPos blockPos) {
        return level instanceof ServerLevel serverLevel ? TotemHelper.getTotemNearPos(serverLevel, blockPos, true).flatMap(x -> NearestVillage.get(x).getClosestVampireForest()) : Optional.empty();
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public @NotNull Villager cureEntity(@NotNull ServerLevel world, @NotNull PathfinderMob entity) {
        Villager villager = CurableConvertedCreature.super.cureEntity(world, entity);
        if (this.data().conversationStarter != null) {
            Player playerentity = world.getPlayerByUUID(this.data().conversationStarter);
            if (playerentity instanceof ServerPlayer) {
                ModAdvancements.TRIGGER_CURED_VAMPIRE_VILLAGER.get().trigger((ServerPlayer) playerentity, this, villager);
                world.onReputationEvent(ReputationEventType.ZOMBIE_VILLAGER_CURED, playerentity, villager);
            }
        }
        return villager;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, @NotNull Entity entity) {
        if (!level().isClientSide() && wantsBlood() && entity instanceof Player player && !Helper.isHunter(player) && !UtilLib.canReallySee(player, this, true)) {
            int amt = VampirePlayer.get(player).onBite(this);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION, new DrinkBloodContext(player));
            return true;
        }
        return super.doHurtTarget(level, entity);
    }

    @Override
    public @NotNull EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining, IDrinkBloodContext drinkContext) {
        BloodDrinkEvent.@NotNull EntityDrinkBloodEvent event = VampirismEventFactory.fireVampireDrinkBlood(this, amt, saturationMod, useRemaining, drinkContext);
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, event.getAmount() * 20));
        bloodTimer = -1200 - random.nextInt(1200);
    }

    @Override
    public @NotNull LivingEntity asEntity() {
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
    public boolean hurtSuper(ServerLevel level, DamageSource damageSource, float amount) {
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    public boolean hurtServer(ServerLevel level, @NotNull DamageSource src, float amount) {
        return this.hurtC(level, src, amount);
    }

    @Override
    protected @NotNull Component getTypeName() {
        var profName = this.getVillagerData().profession().getKey().identifier();
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
        return (sundamageCache = VampirismApi.services().sunDamageRegistry().isGettingSundamage(this, iWorld));
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
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readAdditionalSaveDataC(input);
    }

    @Override
    public void registerBrainGoals(@NotNull Brain<Villager> brain) {
        super.registerBrainGoals(brain);
        if (!this.isBaby()) {
            brain.setSchedule(ModVillage.CONVERTED_DEFAULT.get());
            brain.updateActivityFromSchedule(this.level().environmentAttributes(), this.level().getGameTime(), position());
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        this.registerConvertingData(builder);
    }

    /**
     * copied from {@link Villager#makeBrain(net.minecraft.world.entity.ai.Brain.Packed)} but with {@link #SENSOR_TYPES}, where {@link SensorType#VILLAGER_HOSTILES} is replaced by {@link ModAi#VAMPIRE_VILLAGER_HOSTILES}
     */
    @NotNull
    @Override
    protected Brain<Villager> makeBrain(@NotNull Brain.Packed packedBrain) {
        Brain<Villager> brain = BRAIN_PROVIDER.makeBrain(this, packedBrain);
        this.registerBrainGoals(brain);
        return brain;
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        super.updateTrades(level);
        Holder<VillagerProfession> profession = this.getVillagerData().profession();
        if (!this.getOffers().isEmpty() && !profession.is(ModVillage.VAMPIRE_EXPERT) && !profession.is(VillagerProfession.BUTCHER) && this.getRandom().nextInt(3) == 0) {
            this.addOffersFromTradeSet(level, this.getOffers(), ModTrades.VAMPIRE_VILLAGER);
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
