package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

public class ConvertedDonkeyEntity extends Donkey implements CurableConvertedCreature<Donkey, ConvertedDonkeyEntity> {

    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedDonkeyEntity.class, EntityDataSerializers.BOOLEAN);

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return AbstractHorse.createBaseHorseAttributes()
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG)
                .add(ModAttributes.SUNDAMAGE.get(), BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    public static boolean checkConvertedDonkeySpawnRules(EntityType<? extends Animal> pGoat, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL && Donkey.checkAnimalSpawnRules(pGoat, pLevel, pSpawnType, pPos, pRandom);
    }

    private final Data<Donkey> data = new Data<>();

    public ConvertedDonkeyEntity(@NotNull EntityType<? extends Donkey> type, @NotNull Level worldIn) {
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
    public @NotNull InteractionResult mobInteractSuper(@NotNull Player player, @NotNull InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurtSuper(@NotNull DamageSource damageSource, float amount) {
        return super.hurt(damageSource, amount);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        this.addAdditionalSaveDataC(compound);
    }

    @Override
    public void aiStep() {
        aiStepC(EntityType.DONKEY);
        super.aiStep();
    }

    @Override
    public void die(@NotNull DamageSource cause) {
        super.die(cause);
        dieC(cause);
    }

    @Override
    public void tick() {
        super.tick();
        tickC();
    }

    @Override
    public EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @NotNull
    @Override
    public MobType getMobType() {
        return VampirismConfig.SERVER.vampiresAreUndeadType.get() ? MobType.UNDEAD : VReference.VAMPIRE_CREATURE_ATTRIBUTE;
    }

    @Override
    protected Component getTypeName() {
        return this.getNameC(EntityType.DONKEY::getDescription);
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float amount) {
        return this.hurtC(damageSource, amount);
    }

    @NotNull
    @Override
    public InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return mobInteractC(player, hand);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
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
