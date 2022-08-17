package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.Lists;
import com.mojang.serialization.Dynamic;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import de.teamlapen.vampirism.api.entity.player.vampire.IBloodStats;
import de.teamlapen.vampirism.core.ModAdvancements;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.VampirismVillagerEntity;
import de.teamlapen.vampirism.entity.villager.Trades;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Vampire Villager
 */
public class ConvertedVillagerEntity extends VampirismVillagerEntity implements ICurableConvertedCreature<Villager> {
    public static final @NotNull List<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES;
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedVillagerEntity.class, EntityDataSerializers.BOOLEAN);

    static {
        SENSOR_TYPES = Lists.newArrayList(Villager.SENSOR_TYPES);
        SENSOR_TYPES.remove(SensorType.VILLAGER_HOSTILES);
        SENSOR_TYPES.add(ModVillage.VAMPIRE_VILLAGER_HOSTILES.get());
    }

    private @NotNull EnumStrength garlicCache = EnumStrength.NONE;
    private boolean sundamageCache;
    private int bloodTimer = 0;
    private int conversionTime;
    private @Nullable UUID conversationStarter;

    public ConvertedVillagerEntity(EntityType<? extends ConvertedVillagerEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ConversionTime", this.isConverting(this) ? this.conversionTime : -1);
        if (this.conversationStarter != null) {
            compound.putUUID("ConversionPlayer", this.conversationStarter);
        }
    }

    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive() && this.isConverting(this)) {
            --this.conversionTime;
            if (this.conversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.conversionTime = timer)) {
                this.cureEntity((ServerLevel) this.level, this, EntityType.VILLAGER);
            }
        }

        if (this.tickCount % REFERENCE.REFRESH_GARLIC_TICKS == 1) {
            isGettingGarlicDamage(level, true);
        }
        if (this.tickCount % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(level, true);
        }
        if (!level.isClientSide) {
            if (isGettingSundamage(level) && tickCount % 40 == 11) {
                this.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 42));
            }
            if (isGettingGarlicDamage(level) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(level), this.tickCount);
            }
        }
        bloodTimer++;
        super.aiStep();
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public @NotNull Villager cureEntity(@NotNull ServerLevel world, @NotNull PathfinderMob entity, @NotNull EntityType<Villager> newType) {
        Villager villager = ICurableConvertedCreature.super.cureEntity(world, entity, newType);
        villager.setVillagerData(this.getVillagerData());
        villager.setGossips(this.getGossips().store(NbtOps.INSTANCE).getValue());
        villager.setOffers(this.getOffers());
        villager.setVillagerXp(this.getVillagerXp());
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
        if (!level.isClientSide && wantsBlood() && entity instanceof Player && !Helper.isHunter(entity) && !UtilLib.canReallySee((LivingEntity) entity, this, true)) {
            int amt = VampirePlayer.getOpt((Player) entity).map(vampire -> vampire.onBite(this)).orElse(0);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION);
            return true;
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, amt * 20));
        bloodTimer = -1200 - random.nextInt(1200);
    }

    @Override
    public @NotNull LivingEntity getRepresentingEntity() {
        return this;
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
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.level.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    @NotNull
    @Override
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() != ModItems.CURE_APPLE.get()) return super.mobInteract(player, hand);
        return interactWithCureItem(player, stack, this);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null, compound.getInt("ConversionTime"), this);
        }
    }

    @Override
    public void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @NotNull PathfinderMob entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        this.conversationStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
    }

    @Override
    public void registerBrainGoals(@NotNull Brain<Villager> brain) {
        super.registerBrainGoals(brain);
        if (!this.isBaby()) {
            brain.setSchedule(ModVillage.CONVERTED_DEFAULT.get());
            brain.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
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

    public static class ConvertingHandler implements IConvertingHandler<Villager> {

        @Override
        public IConvertedCreature<Villager> createFrom(@NotNull Villager entity) {
            CompoundTag nbt = new CompoundTag();
            entity.saveWithoutId(nbt);
            ConvertedVillagerEntity converted = ModEntities.VILLAGER_CONVERTED.get().create(entity.level);
            converted.load(nbt);
            converted.setUUID(Mth.createInsecureUUID(converted.random));
            converted.yBodyRot = entity.yBodyRot;
            converted.yHeadRot = entity.yHeadRot;
            return converted;
        }
    }
}
