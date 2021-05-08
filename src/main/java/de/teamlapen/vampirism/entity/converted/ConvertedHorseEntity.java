package de.teamlapen.vampirism.entity.converted;

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
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
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
    private static final DataParameter<Boolean> CONVERTING = EntityDataManager.createKey(ConvertedHorseEntity.class, DataSerializers.BOOLEAN);
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
    public DataParameter<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.registerConvertingData(this);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        if (vulnerableToFire) {
            if (DamageSource.IN_FIRE.equals(damageSource)) {
                return this.attackEntityFrom(VReference.VAMPIRE_IN_FIRE, calculateFireDamage(amount));
            } else if (DamageSource.ON_FIRE.equals(damageSource)) {
                return this.attackEntityFrom(VReference.VAMPIRE_ON_FIRE, calculateFireDamage(amount));
            }
        }
        return super.attackEntityFrom(damageSource, amount);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return !isTame();
    }

    @Override
    public boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(EnumStrength.NONE);
    }

    @Override
    public void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        this.addPotionEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
    }

    @Override
    public CreatureAttribute getCreatureAttribute() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return AbstractHorseEntity.func_234237_fg_()
                .createMutableAttribute(SharedMonsterAttributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG)
                .createMutableAttribute(ModAttributes.sundamage, BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    @Nonnull
    @Override
    public ActionResultType func_230254_b_(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() != ModItems.cure_apple) return super.func_230254_b_(player, hand);
        return interactWithCureItem(player, stack, this);
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
        if (!forceRefresh)
            return sundamageCache;
        return (sundamageCache = Helper.gettingSundamge(this, iWorld, this.world.getProfiler()));
    }

    @Override
    public boolean isIgnoringSundamage() {
        return this.isPotionActive(ModEffects.sunscreen);
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
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (cause.getImmediateSource() instanceof CrossbowArrowEntity && Helper.isHunter(cause.getTrueSource())) {
            dropSoul = true;
        } else if (cause.getImmediateSource() instanceof PlayerEntity && Helper.isHunter(cause.getImmediateSource())) {
            ItemStack weapon = ((PlayerEntity) cause.getImmediateSource()).getHeldItemMainhand();
            if (!weapon.isEmpty() && weapon.getItem() instanceof IVampireFinisher) {
                dropSoul = true;
            }
        } else {
            dropSoul = false;//In case a previous death has been canceled somehow
        }
    }

    @Override
    public boolean useBlood(int amt, boolean allowPartial) {
        this.addPotionEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
        return true;
    }

    @Override
    public boolean wantsBlood() {
        return false;
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
    protected void onDeathUpdate() {
        if (this.deathTime == 19) {
            if (!this.world.isRemote && (dropSoul && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
                this.world.addEntity(new SoulOrbEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), SoulOrbEntity.VARIANT.VAMPIRE));
            }
        }
        super.onDeathUpdate();
    }

    @Override
    public void livingTick() {
        if (!this.world.isRemote && this.isAlive() && this.isConverting(this)) {
            --this.conversionTime;
            if (this.conversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(this, EntityType.HORSE, (timer) -> this.conversionTime = timer)) {
                this.cureEntity((ServerWorld)this.world, this, EntityType.HORSE);
            }
        }
        if (this.ticksExisted % REFERENCE.REFRESH_GARLIC_TICKS == 1) {
            isGettingGarlicDamage(this.world, true);
        }
        if (this.ticksExisted % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(this.world, true);
        }
        if (!world.isRemote) {
            if (isGettingSundamage(world) && ticksExisted % 40 == 11) {
                double dmg = getAttribute(ModAttributes.sundamage).getValue();
                if (dmg > 0) this.attackEntityFrom(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage(world) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(world), this.ticksExisted);
            }
            if (isAlive() && isInWater()) {
                setAir(300);
                if (ticksExisted % 16 == 4) {
                    addPotionEffect(new EffectInstance(Effects.WEAKNESS, 80, 0));
                }
            }
        }
        super.livingTick();
    }

    @Override
    protected void func_230273_eI_() {
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() * 1.5);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (!handleSound(id, this)){
            super.handleStatusUpdate(id);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, CreatureEntity.class, 10, 1, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        this.goalSelector.addGoal(4, new RestrictSunGoal(this));
        this.experienceValue = 2;

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, false, false, false, null)));
    }

    @Override
    public void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @Nonnull CreatureEntity entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        this.conversationStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("ConversionTime", this.isConverting(this) ? this.conversionTime : -1);
        if (this.conversationStarter != null) {
            compound.putUniqueId("ConversionPlayer", this.conversationStarter);
        }
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUniqueId("ConversionPlayer") ? compound.getUniqueId("ConversionPlayer") : null, compound.getInt("ConversionTime"),this);
        }
    }

    public static class ConvertingHandler extends DefaultConvertingHandler<HorseEntity> {
        public ConvertingHandler() {
            super(null);
        }

        @Override
        public IConvertedCreature<HorseEntity> createFrom(HorseEntity entity) {
            ConvertedHorseEntity converted = new ConvertedHorseEntity(ModEntities.converted_horse, entity.world);
            copyImportantStuff(converted, entity);
            converted.setUniqueId(MathHelper.getRandomUUID(converted.rand));
            converted.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 200, 2));
            return converted;
        }

        protected void copyImportantStuff(ConvertedHorseEntity converted, HorseEntity entity) {
            CompoundNBT nbt = new CompoundNBT();
            entity.writeWithoutTypeId(nbt);
            converted.renderYawOffset = entity.renderYawOffset;
            converted.rotationYawHead = entity.rotationYawHead;
            converted.read(nbt);
            converted.setHealth(converted.getMaxHealth() / 3 * 2);
        }
    }
}
