package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModAttributes;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ConvertedDonkeyEntity extends DonkeyEntity implements CurableConvertedCreature<DonkeyEntity, ConvertedDonkeyEntity> {

    private static final DataParameter<Boolean> CONVERTING = EntityDataManager.defineId(ConvertedDonkeyEntity.class, DataSerializers.BOOLEAN);

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return AbstractHorseEntity.createBaseHorseAttributes()
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG)
                .add(ModAttributes.SUNDAMAGE.get(), BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    private final Data<DonkeyEntity> data = new Data<>();

    public ConvertedDonkeyEntity(EntityType<? extends DonkeyEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public Data<DonkeyEntity> data() {
        return this.data;
    }

    @Override
    public void handleEntityEventSuper(byte id) {
        super.handleEntityEvent(id);
    }

    @Override
    public ActionResultType mobInteractSuper(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurtSuper(DamageSource damageSource, float amount) {
        return super.hurt(damageSource, amount);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
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
    public DataParameter<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Nonnull
    @Override
    public CreatureAttribute getMobType() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource damageSource, float amount) {
        return this.hurtC(damageSource, amount);
    }

    @Override
    protected ITextComponent getTypeName() {
        return this.getNameC(EntityType.DONKEY::getDescription);
    }

    @Nonnull
    @Override
    public ActionResultType mobInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand) {
        return mobInteractC(player, hand);
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
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
    protected void randomizeAttributes() {
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
