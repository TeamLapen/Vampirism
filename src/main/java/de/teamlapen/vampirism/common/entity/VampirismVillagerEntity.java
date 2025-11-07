package de.teamlapen.vampirism.common.entity;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;

/**
 * Villager extended with the ability to attack and some other things
 */
public class VampirismVillagerEntity extends Villager {

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return Villager.createAttributes().add(Attributes.ATTACK_DAMAGE);
    }

    protected boolean peaceful = false;
    /**
     * A timer which reaches 0 every 70 to 120 ticks
     */
    private int randomTickDivider;

    public VampirismVillagerEntity(@NotNull EntityType<? extends VampirismVillagerEntity> type, @NotNull Level worldIn) {
        super(type, worldIn);
    }

    public VampirismVillagerEntity(@NotNull EntityType<? extends VampirismVillagerEntity> type, @NotNull Level worldIn, @NotNull ResourceKey<VillagerType> villagerType) {
        super(type, worldIn, villagerType);
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor worldIn, @NotNull EntitySpawnReason spawnReasonIn) {
        return (peaceful || worldIn.getDifficulty() != Difficulty.PEACEFUL) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    @Override
    public boolean hurtServer(ServerLevel level, @NotNull DamageSource src, float amount) {
        if (this.isInvulnerableTo(level, src)) {
            return false;
        } else if (super.hurtServer(level, src, amount)) {
            Entity entity = src.getEntity();
            if (entity instanceof LivingEntity) {
                this.setTarget((LivingEntity) entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && !peaceful && this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        if (--this.randomTickDivider <= 0) {
            this.randomTickDivider = 200;
        }
    }
}
