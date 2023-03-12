package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.ConvertedCreature;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.SoulOrbEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RestrictSunGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public interface CurableConvertedCreature<T extends CreatureEntity, Z extends CreatureEntity & ICurableConvertedCreature<T>> extends ConvertedCreature<T>, ICurableConvertedCreature<T> {

    class Data<T> {
        public boolean vulnerableToFire = true;
        public EnumStrength garlicCache = EnumStrength.NONE;
        public T entityCreature;
        public boolean sundamageCache;
        public boolean dropSoul = false;
        @Nullable
        public ITextComponent name;
        public int conversionTime;
        public UUID conversationStarter;
    }

    Data<T> data();

    /**
     * return in {@link CreatureEntity#hurt(DamageSource, float)}
     */
    default boolean hurtC(DamageSource damageSource, float amount) {
        CreatureEntity entity = ((CreatureEntity) this);
        if (data().vulnerableToFire) {
            if (DamageSource.IN_FIRE.equals(damageSource)) {
                return entity.hurt(VReference.VAMPIRE_IN_FIRE, calculateFireDamage(amount));
            } else if (DamageSource.ON_FIRE.equals(damageSource)) {
                return entity.hurt(VReference.VAMPIRE_ON_FIRE, calculateFireDamage(amount));
            }
        }
        return hurtSuper(damageSource, amount);
    }

    /**
     * return in {@link CreatureEntity#mobInteract(PlayerEntity, Hand)}
     */
    default ActionResultType mobInteractC(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.GOLDEN_APPLE) {
            return interactWithCureItem(player, stack, ((CreatureEntity) this));
        }
        return mobInteractSuper(player, hand);
    }

    @Override
    default void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        ((CreatureEntity) this).addEffect(new EffectInstance(Effects.REGENERATION, amt * 20));
    }

    /**
     * return in {@link CreatureEntity#getTypeName()} ()}
     */
    default ITextComponent getNameC(Supplier<ITextComponent> baseName) {
        if (data().name == null) {
            this.data().name = new TranslationTextComponent("entity.vampirism.vampire").append(baseName.get());
        }
        return data().name;
    }

    /**
     * call in {@link CreatureEntity#handleEntityEvent(byte)}
     */
    default void handleEntityEventC(byte id) {
        if (!handleSound(id, ((CreatureEntity) this))) {
            handleEntityEventSuper(id);
        }
    }

    default boolean doesResistGarlic(EnumStrength strength) {
        return !strength.isStrongerThan(EnumStrength.NONE);
    }

    @Nonnull
    @Override
    default EnumStrength isGettingGarlicDamage(IWorld iWorld, boolean forceRefresh) {
        if (forceRefresh) {
            data().garlicCache = Helper.getGarlicStrength(((CreatureEntity) this), iWorld);
        }
        return data().garlicCache;
    }

    @Override
    default boolean isGettingSundamage(IWorld iWorld, boolean forceRefresh) {
        if (!forceRefresh)
            return data().sundamageCache;
        return (data().sundamageCache = Helper.gettingSundamge(((CreatureEntity) this), iWorld, ((CreatureEntity) this).level.getProfiler()));
    }

    @Override
    default boolean isIgnoringSundamage() {
        return ((CreatureEntity) this).hasEffect(ModEffects.SUNSCREEN.get());
    }

    /**
     * call in {@link CreatureEntity#aiStep()}
     */
    default void aiStepC(EntityType<T> originalType) {
        CreatureEntity entity = ((CreatureEntity) this);
        if (!entity.level.isClientSide && entity.isAlive() && this.isConverting(entity)) {
            --data().conversionTime;
            if (data().conversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(entity, originalType, (timer) -> data().conversionTime = timer)) {
                this.cureEntity((ServerWorld) entity.level, entity, originalType);
            }
        }
        if (entity.tickCount % REFERENCE.REFRESH_GARLIC_TICKS == 1) {
            isGettingGarlicDamage(entity.level, true);
        }
        if (entity.tickCount % REFERENCE.REFRESH_SUNDAMAGE_TICKS == 2) {
            isGettingSundamage(entity.level, true);
        }
        if (!entity.level.isClientSide) {
            if (isGettingSundamage(entity.level) && entity.tickCount % 40 == 11) {
                double dmg = entity.getAttribute(ModAttributes.SUNDAMAGE.get()).getValue();
                if (dmg > 0) entity.hurt(VReference.SUNDAMAGE, (float) dmg);
            }
            if (isGettingGarlicDamage(entity.level) != EnumStrength.NONE) {
                DamageHandler.affectVampireGarlicAmbient(this, isGettingGarlicDamage(entity.level), entity.tickCount);
            }
            if (entity.isAlive() && entity.isInWater()) {
                entity.setAirSupply(300);
                if (entity.tickCount % 16 == 4) {
                    entity.addEffect(new EffectInstance(Effects.WEAKNESS, 80, 0));
                }
            }
        }
    }

    /**
     * call in {@link CreatureEntity#die(DamageSource)}
     */
    default void dieC(DamageSource cause) {
        if (cause.getDirectEntity() instanceof CrossbowArrowEntity && Helper.isHunter(cause.getEntity())) {
            data().dropSoul = true;
        } else if (cause.getDirectEntity() instanceof PlayerEntity && Helper.isHunter(cause.getDirectEntity())) {
            ItemStack weapon = ((PlayerEntity) cause.getDirectEntity()).getMainHandItem();
            if (!weapon.isEmpty() && weapon.getItem() instanceof IVampireFinisher) {
                data().dropSoul = true;
            }
        } else {
            data().dropSoul = false;//In case a previous death has been canceled somehow
        }
    }

    /**
     * call in {@link CreatureEntity#readAdditionalSaveData(CompoundNBT)}
     */
    default void readAdditionalSaveDataC(@Nonnull CompoundNBT compound) {
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null, compound.getInt("ConversionTime"), ((CreatureEntity) this));
        }
    }

    /**
     * call in {@link CreatureEntity#addAdditionalSaveData(CompoundNBT)} ()}
     */
    default void addAdditionalSaveDataC(@Nonnull CompoundNBT compound) {
        compound.putInt("ConversionTime", this.isConverting(((CreatureEntity) this)) ? data().conversionTime : -1);
        if (data().conversationStarter != null) {
            compound.putUUID("ConversionPlayer", data().conversationStarter);
        }
    }

    @Override
    default void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @Nonnull CreatureEntity entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        data().conversationStarter = conversionStarterIn;
        data().conversionTime = conversionTimeIn;
    }

    @Override
    default boolean useBlood(int amt, boolean allowPartial) {
        ((CreatureEntity) this).addEffect(new EffectInstance(Effects.WEAKNESS, amt * 20));
        return true;
    }

    @Override
    default boolean wantsBlood() {
        return false;
    }

    /**
     * Calculates the increased fire damage is this vampire creature is especially vulnerable to fire
     *
     * @param amount
     * @return
     */
    default float calculateFireDamage(float amount) {
        return amount;
    }

    /**
     * call in {@link CreatureEntity#tickDeath()}
     */
    default void tickDeathC() {
        CreatureEntity entity = ((CreatureEntity) this);
        if (entity.deathTime == 19) {
            if (!entity.level.isClientSide && (data().dropSoul && entity.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
                entity.level.addFreshEntity(new SoulOrbEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), SoulOrbEntity.VARIANT.VAMPIRE));
            }
        }
    }

    /**
     * call in {@link CreatureEntity#registerGoals()}
     */
    default void registerGoalsC() {
        CreatureEntity entity = ((CreatureEntity) this);
        entity.goalSelector.addGoal(1, new AvoidEntityGoal<>(entity, CreatureEntity.class, 10, 1, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        entity.goalSelector.addGoal(4, new RestrictSunGoal(entity));

        entity.targetSelector.addGoal(1, new HurtByTargetGoal(entity));
        entity.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(entity, PlayerEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        entity.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(entity, CreatureEntity.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, false, false, false, null)));
    }

    @Override
    default LivingEntity getRepresentingEntity() {
        return ((CreatureEntity) this);
    }

    /**
     * implement as super call for {@link CreatureEntity#handleEntityEvent(byte)}
     */
    void handleEntityEventSuper(byte id);

    /**
     * implement as super call for {@link CreatureEntity#mobInteract(PlayerEntity, Hand)}
     */
    ActionResultType mobInteractSuper(@Nonnull PlayerEntity player, @Nonnull Hand hand);

    /**
     * implement as super call for {@link CreatureEntity#hurt(DamageSource, float)}
     */
    boolean hurtSuper(DamageSource damageSource, float amount);

    static <T extends CreatureEntity, Z extends CreatureEntity & ICurableConvertedCreature<T>> void createFrom(){

    }
}
