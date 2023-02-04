package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.core.ModAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ConvertedGoatEntity extends Goat implements CurableConvertedCreature<Goat, ConvertedGoatEntity> {
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedGoatEntity.class, EntityDataSerializers.BOOLEAN);

    private final Data<Goat> data = new Data<>();

    public ConvertedGoatEntity(EntityType<? extends Goat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Goat.createAttributes().add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG).add(ModAttributes.SUNDAMAGE.get(), BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    @Override
    public EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    public Data<Goat> data() {
        return this.data;
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
    public boolean hurtSuper(DamageSource damageSource, float amount) {
        return super.hurt(damageSource, amount);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addAdditionalSaveDataC(pCompound);
    }

    @Override
    public void aiStep() {
        aiStepC(EntityType.GOAT);
        super.aiStep();
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
        this.dieC(pDamageSource);
    }

    @Override
    public @NotNull MobType getMobType() {
        return VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    public @NotNull Component getName() {
        return this.getNameC(EntityType.GOAT::getDescription);
    }

    @Override
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return this.hurtC(pSource, pAmount);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        return mobInteractC(pPlayer, pHand);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readAdditionalSaveDataC(pCompound);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.registerConvertingData(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.registerGoalsC();
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        this.tickDeathC();
    }
}
