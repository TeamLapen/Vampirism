package de.teamlapen.vampirism.common.world.entity.converted;

import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.core.ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

public class ConvertedGoatEntity extends Goat implements CurableConvertedCreature<Goat, ConvertedGoatEntity> {
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ConvertedGoatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> OVERLAY_TEXTURE = SynchedEntityData.defineId(ConvertedGoatEntity.class, EntityDataSerializers.STRING);

    private final Data<Goat> data = new Data<>();

    public ConvertedGoatEntity(EntityType<? extends Goat> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.@NotNull Builder createAttributes() {
        return Goat.createAttributes().add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.CONVERTED_MOB_DEFAULT_DMG).add(ModAttributes.SUNDAMAGE, BalanceMobProps.mobProps.VAMPIRE_MOB_SUN_DAMAGE);
    }

    public static boolean checkConvertedGoatSpawnRules(EntityType<? extends Animal> pGoat, LevelAccessor pLevel, EntitySpawnReason pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getDifficulty() != Difficulty.PEACEFUL && Goat.checkGoatSpawnRules(pGoat, pLevel, pSpawnType, pPos, pRandom);
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
    public boolean hurtSuper(ServerLevel level, DamageSource damageSource, float amount) {
        return super.hurtServer(level, damageSource, amount);
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.addAdditionalSaveDataC(output);
    }

    @Override
    public void aiStep() {
        if (this.level() instanceof ServerLevel serverLevel) {
            aiStepC(serverLevel, EntityType.GOAT);
        }
        super.aiStep();
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        super.die(pDamageSource);
        this.dieC(pDamageSource);
    }

    @Override
    public @NotNull Component getTypeName() {
        return this.getNameC(EntityType.GOAT::getDescription);
    }

    @Override
    public boolean hurtServer(ServerLevel level, @NotNull DamageSource pSource, float pAmount) {
        return this.hurtC(level, pSource, pAmount);
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand) {
        return mobInteractC(pPlayer, pHand);
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readAdditionalSaveDataC(input);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        this.registerConvertingData(builder);
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
