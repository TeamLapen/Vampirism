package de.teamlapen.vampirism.entity.converted;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import de.teamlapen.lib.lib.util.UtilLib;
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
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Vampire Villager
 */
public class ConvertedVillagerEntity extends VampirismVillagerEntity implements ICurableConvertedCreature<VillagerEntity> {
    public static final List<SensorType<? extends Sensor<? super VillagerEntity>>> SENSOR_TYPES;
    private EnumStrength garlicCache = EnumStrength.NONE;
    private boolean sundamageCache;
    private int bloodTimer = 0;
    private int conversionTime;
    private UUID converstionStarter;

    static {
        SENSOR_TYPES = Lists.newArrayList(VillagerEntity.SENSOR_TYPES);
        SENSOR_TYPES.remove(SensorType.VILLAGER_HOSTILES);
        SENSOR_TYPES.add(ModVillage.vampire_villager_hostiles);
    }

    public ConvertedVillagerEntity(EntityType<? extends ConvertedVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.registerConvertingData(this);
    }

    /**
     * copied from {@link VillagerEntity#createBrain(Dynamic)} but with {@link #SENSOR_TYPES}, where {@link SensorType#VILLAGER_HOSTILES} is replaced by {@link ModVillage#vampire_villager_hostiles}
     */
    @Nonnull
    @Override
    protected Brain<?> createBrain(@Nonnull Dynamic<?> dynamicIn) {
        Brain<VillagerEntity> brain = Brain.createCodec(MEMORY_TYPES, SENSOR_TYPES).deserialize(dynamicIn);
        this.initBrain(brain);
        return brain;
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (!world.isRemote && wantsBlood() && entity instanceof PlayerEntity && !Helper.isHunter(entity) && !UtilLib.canReallySee((LivingEntity) entity, this, true)) {
            int amt = VampirePlayer.getOpt((PlayerEntity) entity).map(vampire -> vampire.onBite(this)).orElse(0);
            drinkBlood(amt, IBloodStats.MEDIUM_SATURATION);
            return true;
        }
        return super.attackEntityAsMob(entity);
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        Team team = this.getTeam();
        if (this.getCustomName() != null) {
            return super.getDisplayName();
        } else {
            VillagerProfession villagerprofession = this.getVillagerData().getProfession();
            IFormattableTextComponent itextcomponent1 = (new TranslationTextComponent(EntityType.VILLAGER.getTranslationKey() + '.' + (!"minecraft".equals(Helper.getIDSafe(villagerprofession).getNamespace()) ? Helper.getIDSafe(villagerprofession).getNamespace() + '.' : "") + Helper.getIDSafe(villagerprofession).getPath())).modifyStyle((p_211516_1_) -> p_211516_1_.setHoverEvent(this.getHoverEvent()).setInsertion(this.getCachedUniqueIdString()));
            if (team != null) {
                itextcomponent1.mergeStyle(team.getColor());
            }

            return itextcomponent1;
        }
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return false;
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addPotionEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
        bloodTimer = -1200 - rand.nextInt(1200);
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
    }

    @Nonnull
    @Override
    public EnumStrength isGettingGarlicDamage(IWorld iWorld, boolean forceRefresh) {
        if (forceRefresh) {
            garlicCache = Helper.getGarlicStrength(this, Helper.getWorldKey(iWorld));
        }
        return garlicCache;
    }

    @Override
    public boolean isGettingSundamage(IWorld iWorld, boolean forceRefresh) {
        if (!forceRefresh) return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.world.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return false;
    }

    @Override
    public void livingTick() {
        if (!this.world.isRemote && this.isAlive() && this.isConverting(this)) {
            int i = this.getConversionProgress(this);
            this.conversionTime -= i;
            if (this.conversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.VILLAGER, (timer) -> this.conversionTime = timer)) {
                this.cureEntity((ServerWorld)this.world, this, EntityType.VILLAGER);
            }
        }

        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 1) {
            isGettingGarlicDamage(world, true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(world, true);
        }
        if (!world.isRemote) {
            if (isGettingSundamage(world) && ticksExisted % 40 == 11) {
                this.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 42));
            }
            if (isGettingGarlicDamage(world) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(world), this.ticksExisted);
            }
        }
        bloodTimer++;
        super.livingTick();
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addPotionEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
        bloodTimer = 0;
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return bloodTimer > 0;
    }

    @Override
    public void initBrain(@Nonnull Brain<VillagerEntity> brain) {
        VillagerProfession villagerprofession = this.getVillagerData().getProfession();
        float f = (float) this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
        if (this.isChild()) {
            brain.setSchedule(Schedule.VILLAGER_BABY);
            brain.registerActivity(Activity.PLAY, VillagerTasks.play(f));
        } else {
            brain.setSchedule(ModVillage.converted_default);
            brain.registerActivity(Activity.WORK, VillagerTasks.work(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryModuleStatus.VALUE_PRESENT)));
        }

        brain.registerActivity(Activity.CORE, VillagerTasks.core(villagerprofession, f));
        brain.registerActivity(Activity.MEET, VillagerTasks.meet(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleStatus.VALUE_PRESENT)));
        brain.registerActivity(Activity.REST, VillagerTasks.rest(villagerprofession, f));
        brain.registerActivity(Activity.IDLE, VillagerTasks.idle(villagerprofession, f));
        brain.registerActivity(Activity.PANIC, VillagerTasks.panic(villagerprofession, f));
        brain.registerActivity(Activity.PRE_RAID, VillagerTasks.preRaid(villagerprofession, f));
        brain.registerActivity(Activity.RAID, VillagerTasks.raid(villagerprofession, f));
        brain.registerActivity(Activity.HIDE, VillagerTasks.hide(villagerprofession, f));
        brain.setDefaultActivities(ImmutableSet.of(Activity.CORE));
        brain.setFallbackActivity(Activity.IDLE);
        brain.switchTo(Activity.IDLE);
        brain.updateActivity(this.world.getDayTime(), this.world.getGameTime());
    }

    @Override
    protected void populateTradeData() {
        super.populateTradeData();
        if (!this.getOffers().isEmpty() && this.getRNG().nextInt(3) == 0) {
            this.addTrades(this.getOffers(), Trades.converted_trades, 1);
        }
    }

    @Nonnull
    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() != ModItems.cure_apple) return super.func_230254_b_(player, hand);
        return interactWithCureItem(player, stack, this);
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("ConversionTime", this.isConverting(this) ? this.conversionTime : -1);
        if (this.converstionStarter != null) {
            compound.putUniqueId("ConversionPlayer", this.converstionStarter);
        }
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUniqueId("ConversionPlayer") ? compound.getUniqueId("ConversionPlayer") : null, compound.getInt("ConversionTime"),this);
        }
    }

    @Override
    public void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, CreatureEntity entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        this.converstionStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (!handleSound(id, this)){
            super.handleStatusUpdate(id);
        }
    }

    @Override
    public VillagerEntity cureEntity(ServerWorld world, CreatureEntity entity, EntityType<VillagerEntity> newType) {
        VillagerEntity villager = ICurableConvertedCreature.super.cureEntity(world, entity, newType);
        villager.setVillagerData(this.getVillagerData());
        villager.setGossips(this.getGossip().write(NBTDynamicOps.INSTANCE).getValue());
        villager.setOffers(this.getOffers());
        villager.setXp(this.getXp());
        if (this.converstionStarter != null) {
            PlayerEntity playerentity = world.getPlayerByUuid(this.converstionStarter);
            if (playerentity instanceof ServerPlayerEntity) {
                ModAdvancements.TRIGGER_CURED_VAMPIRE_VILLAGER.trigger((ServerPlayerEntity)playerentity, this, villager);
                world.updateReputation(IReputationType.ZOMBIE_VILLAGER_CURED, playerentity, villager);
            }
        }
        return villager;
    }

    public static class ConvertingHandler implements IConvertingHandler<VillagerEntity> {

        @Override
        public IConvertedCreature<VillagerEntity> createFrom(VillagerEntity entity) {
            CompoundNBT nbt = new CompoundNBT();
            entity.writeWithoutTypeId(nbt);
            ConvertedVillagerEntity converted = ModEntities.villager_converted.create(entity.world);
            converted.read(nbt);
            converted.setUniqueId(MathHelper.getRandomUUID(converted.rand));
            converted.renderYawOffset = entity.renderYawOffset;
            converted.rotationYawHead = entity.rotationYawHead;
            return converted;
        }
    }
}
