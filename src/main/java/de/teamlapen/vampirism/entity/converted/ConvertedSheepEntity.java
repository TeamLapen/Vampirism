package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.BalanceMobProps;
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
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

public class ConvertedSheepEntity extends Sheep implements CurableConvertedCreature<Sheep, ConvertedSheepEntity> {

    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedSheepEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> OVERLAY_TEXTURE = SynchedEntityData.defineId(ConvertedSheepEntity.class, EntityDataSerializers.STRING);

    private final Data<Sheep> data = new Data<>();

    public ConvertedSheepEntity(EntityType<? extends ConvertedSheepEntity> type, Level world) {
        super(type, world);
    }

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return Sheep.createAttributes().add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG).add(ModAttributes.SUNDAMAGE.get(), BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    public static boolean checkConvertedSheepSpawnRules(EntityType<? extends Animal> pGoat, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL && Sheep.checkAnimalSpawnRules(pGoat, pLevel, pSpawnType, pPos, pRandom);
    }

    @Override
    public @NotNull EntityDataAccessor<Boolean> getConvertingDataParam() {
        return CONVERTING;
    }

    @Override
    public @NotNull EntityDataAccessor<String> getSourceEntityDataParam() {
        return OVERLAY_TEXTURE;
    }

    @Override
    public Data<Sheep> data() {
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
        aiStepC(EntityType.SHEEP);
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
        return this.getNameC(EntityType.SHEEP::getDescription);
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

    @Override
    public void tick() {
        super.tick();
        this.tickC();
    }
}
