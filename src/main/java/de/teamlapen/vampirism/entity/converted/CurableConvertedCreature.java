package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.convertible.ICurableConvertedCreature;
import de.teamlapen.vampirism.api.items.IVampireFinisher;
import de.teamlapen.vampirism.core.ModAttributes;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.CrossbowArrowEntity;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.entity.SoulOrbEntity;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public interface CurableConvertedCreature<T extends PathfinderMob, Z extends PathfinderMob & ICurableConvertedCreature<T>> extends ICurableConvertedCreature<T> {

    class Data<T> {
        public boolean vulnerableToFire = true;
        public @NotNull EnumStrength garlicCache = EnumStrength.NONE;
        public T entityCreature;
        public boolean sundamageCache;
        public boolean dropSoul = false;
        @Nullable
        public Component name;
        public int conversionTime;
        public @Nullable UUID conversationStarter;
    }

    Data<T> data();

    /**
     * return in {@link PathfinderMob#hurt(DamageSource, float)}
     */
    default boolean hurtC(DamageSource damageSource, float amount) {
        PathfinderMob entity = ((PathfinderMob) this);
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
     * return in {@link PathfinderMob#mobInteract(Player, InteractionHand)}
     */
    @SuppressWarnings("JavadocReference")
    default InteractionResult mobInteractC(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() != ModItems.CURE_APPLE.get()) {
            return mobInteractSuper(player, hand);
        }
        return interactWithCureItem(player, stack, ((PathfinderMob) this));
    }

    @Override
    default void drinkBlood(int amt, float saturationMod, boolean useRemaining) {
        ((PathfinderMob) this).addEffect(new MobEffectInstance(MobEffects.REGENERATION, amt * 20));
    }

    /**
     * return in {@link net.minecraft.world.entity.PathfinderMob#getTypeName()} ()}
     */
    @SuppressWarnings("JavadocReference")
    default @Nullable Component getNameC(@NotNull Supplier<Component> baseName) {
        if (data().name == null) {
            this.data().name = Component.translatable("entity.vampirism.vampire").append(baseName.get());
        }
        return data().name;
    }

    /**
     * call in {@link PathfinderMob#handleEntityEvent(byte)}
     */
    default void handleEntityEventC(byte id) {
        if (!handleSound(id, ((PathfinderMob) this))) {
            handleEntityEventSuper(id);
        }
    }

    default boolean doesResistGarlic(@NotNull EnumStrength strength) {
        return !strength.isStrongerThan(EnumStrength.NONE);
    }

    @NotNull
    @Override
    default EnumStrength isGettingGarlicDamage(LevelAccessor iWorld, boolean forceRefresh) {
        if (forceRefresh) {
            data().garlicCache = Helper.getGarlicStrength(((PathfinderMob) this), iWorld);
        }
        return data().garlicCache;
    }

    @Override
    default boolean isGettingSundamage(LevelAccessor iWorld, boolean forceRefresh) {
        if (!forceRefresh) {
            return data().sundamageCache;
        }
        return (data().sundamageCache = Helper.gettingSundamge(((PathfinderMob) this), iWorld, ((PathfinderMob) this).level.getProfiler()));
    }

    @Override
    default boolean isIgnoringSundamage() {
        return ((PathfinderMob) this).hasEffect(ModEffects.SUNSCREEN.get());
    }

    /**
     * call in {@link PathfinderMob#aiStep()}
     */
    default void aiStepC(@NotNull EntityType<T> originalType) {
        PathfinderMob entity = ((PathfinderMob) this);
        if (!entity.level.isClientSide && entity.isAlive() && this.isConverting(entity)) {
            --data().conversionTime;
            if (data().conversionTime <= 0 && net.minecraftforge.event.ForgeEventFactory.canLivingConvert(entity, originalType, (timer) -> data().conversionTime = timer)) {
                this.cureEntity((ServerLevel) entity.level, entity, originalType);
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
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
                }
            }
        }
    }

    /**
     * call in {@link PathfinderMob#die(DamageSource)}
     */
    default void dieC(@NotNull DamageSource cause) {
        if (cause.getDirectEntity() instanceof CrossbowArrowEntity && Helper.isHunter(cause.getEntity())) {
            data().dropSoul = true;
        } else if (cause.getDirectEntity() instanceof Player && Helper.isHunter(cause.getDirectEntity())) {
            ItemStack weapon = ((Player) cause.getDirectEntity()).getMainHandItem();
            if (!weapon.isEmpty() && weapon.getItem() instanceof IVampireFinisher) {
                data().dropSoul = true;
            }
        } else {
            data().dropSoul = false;//In case a previous death has been canceled somehow
        }
    }

    /**
     * call in {@link PathfinderMob#readAdditionalSaveData(CompoundTag)}
     */
    @SuppressWarnings("JavadocReference")
    default void readAdditionalSaveDataC(@NotNull CompoundTag compound) {
        if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
            this.startConverting(compound.hasUUID("ConversionPlayer") ? compound.getUUID("ConversionPlayer") : null, compound.getInt("ConversionTime"), ((PathfinderMob) this));
        }
    }

    /**
     * call in {@link PathfinderMob#addAdditionalSaveData(CompoundTag)}}
     */
    @SuppressWarnings("JavadocReference")
    default void addAdditionalSaveDataC(@NotNull CompoundTag compound) {
        compound.putInt("ConversionTime", this.isConverting(((PathfinderMob) this)) ? data().conversionTime : -1);
        if (data().conversationStarter != null) {
            compound.putUUID("ConversionPlayer", data().conversationStarter);
        }
    }

    @Override
    default void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn, @NotNull PathfinderMob entity) {
        ICurableConvertedCreature.super.startConverting(conversionStarterIn, conversionTimeIn, entity);
        data().conversationStarter = conversionStarterIn;
        data().conversionTime = conversionTimeIn;
    }

    @Override
    default boolean useBlood(int amt, boolean allowPartial) {
        ((PathfinderMob) this).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, amt * 20));
        return true;
    }

    @Override
    default boolean wantsBlood() {
        return false;
    }

    /**
     * Calculates the increased fire damage is this vampire creature is especially vulnerable to fire
     */
    default float calculateFireDamage(float amount) {
        return amount;
    }

    /**
     * call in {@link PathfinderMob#tickDeath()}
     */
    @SuppressWarnings("JavadocReference")
    default void tickDeathC() {
        PathfinderMob entity = ((PathfinderMob) this);
        if (entity.deathTime == 19) {
            if (!entity.level.isClientSide && (data().dropSoul && entity.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
                entity.level.addFreshEntity(new SoulOrbEntity(entity.level, entity.getX(), entity.getY(), entity.getZ(), SoulOrbEntity.VARIANT.VAMPIRE));
            }
        }
    }

    /**
     * call in {@link PathfinderMob#registerGoals()}
     */
    @SuppressWarnings("JavadocReference")
    default void registerGoalsC() {
        PathfinderMob entity = ((PathfinderMob) this);
        entity.goalSelector.addGoal(1, new AvoidEntityGoal<>(entity, PathfinderMob.class, 10, 1, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        entity.goalSelector.addGoal(4, new RestrictSunGoal(entity));

        entity.targetSelector.addGoal(1, new HurtByTargetGoal(entity));
        entity.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(entity, Player.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), true, false, true, false, null)));
        entity.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(entity, PathfinderMob.class, 5, true, false, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, false, false, false, null)));
    }

    @Override
    default @NotNull LivingEntity getRepresentingEntity() {
        return ((PathfinderMob) this);
    }

    /**
     * implement as super call for {@link PathfinderMob#handleEntityEvent(byte)}
     */
    void handleEntityEventSuper(byte id);

    /**
     * implement as super call for {@link PathfinderMob#mobInteract(Player, InteractionHand)}
     */
    @SuppressWarnings("JavadocReference")
    InteractionResult mobInteractSuper(@NotNull Player player, @NotNull InteractionHand hand);

    /**
     * implement as super call for {@link PathfinderMob#hurt(DamageSource, float)}
     */
    boolean hurtSuper(DamageSource damageSource, float amount);

    static <T extends PathfinderMob, Z extends PathfinderMob & ICurableConvertedCreature<T>> void createFrom() {

    }
}
