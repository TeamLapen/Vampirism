package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.SoulOrbEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;


public class ConvertedHorseEntity extends HorseEntity implements ICurableConvertedCreature<HorseEntity> {
    private static final DataParameter<Boolean> CONVERTING = EntityDataManager.defineId(ConvertedHorseEntity.class, DataSerializers.BOOLEAN);

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return AbstractHorseEntity.createBaseHorseAttributes()
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG)
                .add(ModAttributes.sundamage, BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }
    protected boolean vulnerableToFire = true;
    private EnumStrength garlicCache = EnumStrength.NONE;
    private HorseEntity entityCreature;
    private boolean sundamageCache;
    private boolean dropSoul = false;
    @Nullable
    private ITextComponent name;
    private int conversionTime;
    private UUID conversationStarter;

    public ConvertedHorseEntity(EntityType<? extends HorseEntity> type, World worldIn) {
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
            if (this.conversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.HORSE, (timer) -> this.conversionTime = timer)) {
                this.cureEntity((ServerWorld) this.level, this, EntityType.HORSE);
            }
        }
        if (this.tickCount % REFERENCE.REFRESH_GARLIC_TICKS == 1) {
            isGettingGarlicDamage(this.level, true);
        }
        if (this.tickCount % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(this.level, true);
        }
        if (!level.isClientSide) {
            if (isGettingSundamage(level) && tickCount % 40 == 11) {
                double dmg = getAttribute(ModAttributes.sundamage).getValue();
                if (dmg > 0) this.hurt(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage(level) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(level), this.tickCount);
            }
            if (isAlive() && isInWater()) {
                setAirSupply(300);
                if (tickCount % 16 == 4) {
                    addEffect(new EffectInstance(Effects.WEAKNESS, 80, 0));
                }
            }
        }
        super.aiStep();
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(EnumStrength.NONE);
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (cause.getDirectEntity() instanceof CrossbowArrowEntity && Helper.isHunter(cause.getEntity())) {
            dropSoul = true;
        } else if (cause.getDirectEntity() instanceof PlayerEntity && Helper.isHunter(cause.getDirectEntity())) {
            ItemStack weapon = ((PlayerEntity) cause.getDirectEntity()).getMainHandItem();
            if (!weapon.isEmpty() && weapon.getItem() instanceof IVampireFinisher) {
                dropSoul = true;
            }
        } else {
            dropSoul = false;//In case a previous death has been canceled somehow
        }
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
    }

    @Override
    public DataParameter<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    public CreatureAttribute getMobType() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public ITextComponent getName() {
        if (hasCustomName()) {
            return super.getName();
        }
        if (name == null) {
            this.name = new TranslationTextComponent("entity.vampirism.vampire").append(new TranslationTextComponent("entity.horse"));
        }
        return name;
    }

    @Override
    public LivingEntity getRepresentingEntity() {
        return this;
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
    public boolean hurt(DamageSource damageSource, float amount) {
        if (vulnerableToFire) {
            if (DamageSource.IN_FIRE.equals(damageSource)) {
                return this.hurt(VReference.VAMPIRE_IN_FIRE, calculateFireDamage(amount));
            } else if (DamageSource.ON_FIRE.equals(damageSource)) {
                return this.hurt(VReference.VAMPIRE_ON_FIRE, calculateFireDamage(amount));
            }
        }
        return super.hurt(damageSource, amount);
    }

    @Override
    public boolean isGettingSundamage(IWorld iWorld, boolean forceRefresh) {
        if (!forceRefresh)
            return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.level.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.hasEffect(ModEffects.sunscreen);
    }

    @Nonnull
    @Override
    public ActionResultType mobInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() != ModItems.cure_apple) return super.mobInteract(player, hand);
        return interactWithCureItem(player, stack, this);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null, compound.getInt("ConversionTime"), this);
        }
    }

    @Override
    public void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @Nonnull CreatureEntity entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        this.conversationStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isTamed();
    }

    @Override
    public boolean wantsBlood() {
        return false;
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
        return true;
    }

    /**
     * Calculates the increased fire damage is this vampire creature is especially vulnerable to fire
     *
     * @param amount
     * @return
     */
    protected float calculateFireDamage(float amount) {
        return amount;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.registerConvertingData(this);
    }

    @Override
    protected void randomizeAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() * 1.5);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, CreatureEntity.class, 10, 1, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        this.goalSelector.addGoal(4, new RestrictSunGoal(this));
        this.xpReward = 2;

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, false, false, false, null)));
    }

    @Override
    protected void tickDeath() {
        if (this.deathTime == 19) {
            if (!this.level.isClientSide && (dropSoul && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
                this.level.addFreshEntity(new SoulOrbEntity(this.level, this.getX(), this.getY(), this.getZ(), SoulOrbEntity.VARIANT.VAMPIRE));
            }
        }
        super.tickDeath();
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<HorseEntity> {
        public ConvertingHandler() {
            super(null);
        }

        @Override
        public IConvertedCreature<HorseEntity> createFrom(HorseEntity entity) {
            ConvertedHorseEntity converted = new ConvertedHorseEntity(ModEntities.converted_horse, entity.level);
            copyImportantStuff(converted, entity);
            converted.setUUID(MathHelper.createInsecureUUID(converted.random));
            converted.addEffect(new EffectInstance(Effects.WEAKNESS, 200, 2));
            return converted;
        }

        protected void copyImportantStuff(ConvertedHorseEntity converted, HorseEntity entity) {
            CompoundNBT nbt = new CompoundNBT();
            entity.saveWithoutId(nbt);
            converted.yBodyRot = entity.yBodyRot;
            converted.yHeadRot = entity.yHeadRot;
            converted.load(nbt);
            converted.setHealth(converted.getMaxHealth() / 3 * 2);
        }
    }
}
