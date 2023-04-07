package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
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
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.VampirismVillagerEntity;
import de.teamlapen.vampirism.entity.villager.Trades;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.VillagerTasks;
import net.minecraft.entity.merchant.IReputationType;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Vampire Villager
 */
public class ConvertedVillagerEntity extends VampirismVillagerEntity implements ICurableConvertedCreature<VillagerEntity> {
    public static final List<SensorType<? extends Sensor<? super VillagerEntity>>> SENSOR_TYPES;
    private static final DataParameter<Boolean> CONVERTING = EntityDataManager.defineId(ConvertedVillagerEntity.class, DataSerializers.BOOLEAN);

    static {
        SENSOR_TYPES = Lists.newArrayList(VillagerEntity.SENSOR_TYPES);
        SENSOR_TYPES.remove(SensorType.VILLAGER_HOSTILES);
        SENSOR_TYPES.add(ModVillage.VAMPIRE_VILLAGER_HOSTILES.get());
    }

    private EnumStrength garlicCache = EnumStrength.NONE;
    private boolean sundamageCache;
    private int bloodTimer = 0;
    private int conversionTime;
    private UUID conversationStarter;

    public ConvertedVillagerEntity(EntityType<? extends ConvertedVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
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
                this.cureEntity((ServerWorld) this.level, this, EntityType.VILLAGER);
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
                this.addEffect(new EffectInstance(Effects.WEAKNESS, 42));
            }
            if (isGettingGarlicDamage(level) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(level), this.tickCount);
            }
        }
        bloodTimer++;
        super.aiStep();
    }

    public Optional<BlockPos> getClosestVampireForest() {
        if(this.getLevel() instanceof ServerWorld){
        }
        return Optional.empty();
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public VillagerEntity cureEntity(ServerWorld world, CreatureEntity entity, EntityType<VillagerEntity> newType) {
        VillagerEntity villager = ICurableConvertedCreature.super.cureEntity(world, entity, newType);
        villager.setVillagerData(this.getVillagerData());
        villager.setGossips(this.getGossips().store(NBTDynamicOps.INSTANCE).getValue());
        villager.setOffers(this.getOffers());
        villager.setVillagerXp(this.getVillagerXp());
        if (this.conversationStarter != null) {
            PlayerEntity playerentity = world.getPlayerByUUID(this.conversationStarter);
            if (playerentity instanceof ServerPlayerEntity) {
                ModAdvancements.TRIGGER_CURED_VAMPIRE_VILLAGER.trigger((ServerPlayerEntity) playerentity, this, villager);
                world.onReputationEvent(IReputationType.ZOMBIE_VILLAGER_CURED, playerentity, villager);
            }
        }
        return villager;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!level.isClientSide && wantsBlood() && entity instanceof PlayerEntity && !Helper.isHunter(entity) && !UtilLib.canReallySee((LivingEntity) entity, this, true)) {
            int amt = VampirePlayer.getOpt((PlayerEntity) entity).map(vampire -> vampire.onBite(this)).orElse(0);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION);
            return true;
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public DataParameter<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
        bloodTimer = -1200 - random.nextInt(1200);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Override
    protected ITextComponent getTypeName() {
        net.minecraft.util.ResourceLocation profName = this.getVillagerData().getProfession().getRegistryName();
        return new TranslationTextComponent(EntityType.VILLAGER.getDescriptionId() + '.' + (!"minecraft".equals(profName.getNamespace()) ? profName.getNamespace() + '.' : "") + profName.getPath());
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (!handleSound(id, this)) {
            super.handleEntityEvent(id);
        }
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(IWorld iWorld, boolean forceRefresh) {
        if (forceRefresh) {
            garlicCache = Helper.getGarlicStrength(this, iWorld);
        }
        return garlicCache;
    }

    @Override
    public boolean isGettingSundamage(IWorld iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.level.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    @Nonnull
    @Override
    public ActionResultType mobInteract(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.GOLDEN_APPLE) {
            return interactWithCureItem(player, stack, this);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null, compound.getInt("ConversionTime"), this);
        }
    }

    @Override
    public void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, CreatureEntity entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        this.conversationStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
    }

    @Override
    public void registerBrainGoals(@Nonnull Brain<VillagerEntity> brain) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();
        float f = (float) this.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
        if (this.isBaby()) {
            brain.setSchedule(Schedule.VILLAGER_BABY);
            brain.addActivity(Activity.PLAY, VillagerTasks.getPlayPackage(f));
        } else {
            brain.setSchedule(ModVillage.CONVERTED_DEFAULT.get());
            brain.addActivityWithConditions(Activity.WORK, VillagerTasks.getWorkPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT)));
        }

        brain.addActivity(Activity.CORE, VillagerTasks.getCorePackage(villagerprofession, f));
        brain.addActivityWithConditions(Activity.MEET, VillagerTasks.getMeetPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT)));
        brain.addActivity(Activity.REST, VillagerTasks.getRestPackage(villagerprofession, f));
        brain.addActivity(Activity.IDLE, VillagerTasks.getIdlePackage(villagerprofession, f));
        brain.addActivity(Activity.PANIC, VillagerTasks.getPanicPackage(villagerprofession, f));
        brain.addActivity(Activity.PRE_RAID, VillagerTasks.getPreRaidPackage(villagerprofession, f));
        brain.addActivity(Activity.RAID, VillagerTasks.getRaidPackage(villagerprofession, f));
        brain.addActivity(Activity.HIDE, VillagerTasks.getHidePackage(villagerprofession, f));
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.setActiveActivityIfPossible(Activity.IDLE);
        brain.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
        //TODO can't we just use super function and overwrite schedule and refresh activity afterwards?
    }

    @Override
    public boolean wantsBlood() {
        return bloodTimer > 0;
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
        bloodTimer = 0;
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.registerConvertingData(this);
    }

    /**
     * copied from {@link VillagerEntity#createBrain(Dynamic)} but with {@link #SENSOR_TYPES}, where {@link SensorType#VILLAGER_HOSTILES} is replaced by {@link ModVillage#vampire_villager_hostiles}
     */
    @Nonnull
    @Override
    protected Brain<?> makeBrain(@Nonnull Dynamic<?> dynamicIn) {
        Brain<VillagerEntity> brain = Brain.provider(MEMORY_TYPES, SENSOR_TYPES).makeBrain(dynamicIn);
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

    public static class ConvertingHandler implements IConvertingHandler<VillagerEntity> {

        @Override
        public IConvertedCreature<VillagerEntity> createFrom(VillagerEntity entity) {
            CompoundNBT nbt = new CompoundNBT();
            entity.saveWithoutId(nbt);
            ConvertedVillagerEntity converted = ModEntities.VILLAGER_CONVERTED.get().create(entity.level);
            converted.load(nbt);
            converted.setUUID(MathHelper.createInsecureUUID(converted.random));
            converted.yBodyRot = entity.yBodyRot;
            converted.yHeadRot = entity.yHeadRot;
            return converted;
        }
    }
}
