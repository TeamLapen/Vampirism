package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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

    public VampirismVillagerEntity(@NotNull EntityType<? extends VampirismVillagerEntity> type, @NotNull Level worldIn, @NotNull VillagerType villagerType) {
        super(type, worldIn, villagerType);
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor worldIn, @NotNull MobSpawnType spawnReasonIn) {
        return (peaceful || worldIn.getDifficulty() != Difficulty.PEACEFUL) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    public boolean doHurtTarget(@NotNull Entity entity) {
        float f = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        int i = 0;

        if (entity instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), entity.getType());
            i += EnchantmentHelper.getKnockbackBonus(this);
        }

        boolean flag = DamageHandler.hurtVanilla(entity, s -> s.mobAttack(this), f);

        if (flag) {
            if (i > 0) {
                entity.push(-Mth.sin(this.getYRot() * (float) Math.PI / 180.0F) * (float) i * 0.5F, 0.1D, Mth.cos(this.getYRot() * (float) Math.PI / 180.0F) * (float) i * 0.5F);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1D, 0.6D));
            }

            int j = EnchantmentHelper.getFireAspect(this);

            if (j > 0) {
                entity.igniteForTicks(j * 4);
            }

            this.doEnchantDamageEffects(this, entity);

        }


        return flag;
    }

    @Override
    public boolean hurt(@NotNull DamageSource src, float amount) {
        if (this.isInvulnerableTo(src)) {
            return false;
        } else if (super.hurt(src, amount)) {
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

        if (!this.level().isClientSide && !peaceful && this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (--this.randomTickDivider <= 0) {
            this.randomTickDivider = 200;
        }
    }
}
