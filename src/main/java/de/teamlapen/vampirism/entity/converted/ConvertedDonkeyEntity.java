package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class ConvertedDonkeyEntity extends Donkey implements CurableConvertedCreature<Donkey, ConvertedDonkeyEntity> {

    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedDonkeyEntity.class, EntityDataSerializers.BOOLEAN);

    public static AttributeSupplier.Builder getAttributeBuilder() {
        return AbstractHorse.createBaseHorseAttributes()
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG)
                .add(ModAttributes.SUNDAMAGE.get(), BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    private final Data<Donkey> data = new Data<>();

    public ConvertedDonkeyEntity(EntityType<? extends Donkey> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public Data<Donkey> data() {
        return this.data;
    }

    @Override
    public void handleEntityEventSuper(byte id) {
        super.handleEntityEvent(id);
    }

    @Override
    public InteractionResult mobInteractSuper(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurtSuper(DamageSource damageSource, float amount) {
        return super.hurt(damageSource, amount);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.addAdditionalSaveDataC(compound);
    }

    @Override
    public void aiStep() {
        aiStepC(EntityType.DONKEY);
        super.aiStep();
    }

    @Override
    public void die(@Nonnull DamageSource cause) {
        super.die(cause);
        dieC(cause);
    }

    @Override
    public EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Nonnull
    @Override
    public MobType getMobType() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    protected Component getTypeName() {
        return this.getNameC(EntityType.DONKEY::getDescription);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource damageSource, float amount) {
        return this.hurtC(damageSource, amount);
    }

    @Nonnull
    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        return mobInteractC(player, hand);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        readAdditionalSaveDataC(compound);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return !isTamed();
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.registerConvertingData(this);
    }

    @Override
    protected void randomizeAttributes(RandomSource random) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(this.getMaxHealth() * 1.5);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.registerGoalsC();
        this.xpReward = 2;
    }

    @Override
    protected void tickDeath() {
        this.tickDeathC();
        super.tickDeath();
    }
}
