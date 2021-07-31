package de.teamlapen.vampirism.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

/**
 * Villager extended with the ability to attack and some other things
 */
public class VampirismVillagerEntity extends VillagerEntity {

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VillagerEntity.createAttributes().add(Attributes.ATTACK_DAMAGE);
    }
    protected boolean peaceful = false;
    /**
     * A timer which reaches 0 every 70 to 120 ticks
     */
    private int randomTickDivider;

    public VampirismVillagerEntity(EntityType<? extends VampirismVillagerEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public VampirismVillagerEntity(EntityType<? extends VampirismVillagerEntity> type, World worldIn, VillagerType villagerType) {
        super(type, worldIn, villagerType);
    }

    @Override
    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    @Override
    public boolean checkSpawnRules(IWorld worldIn, SpawnReason spawnReasonIn) {
        return (peaceful || worldIn.getDifficulty() != Difficulty.PEACEFUL) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

//    @Nullable
//    public StructureStart getVillage() {
//        if (cachedVillage == StructureStart.DUMMY)
//            return null;
//        return cachedVillage;
//    }

    public boolean doHurtTarget(Entity entity) {
        float f = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        int i = 0;

        if (entity instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity) entity).getMobType());
            i += EnchantmentHelper.getKnockbackBonus(this);
        }

        boolean flag = entity.hurt(DamageSource.mobAttack(this), f);

        if (flag) {
            if (i > 0) {
                entity.push(-MathHelper.sin(this.yRot * (float) Math.PI / 180.0F) * (float) i * 0.5F, 0.1D, MathHelper.cos(this.yRot * (float) Math.PI / 180.0F) * (float) i * 0.5F);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1D, 0.6D));
            }

            int j = EnchantmentHelper.getFireAspect(this);

            if (j > 0) {
                entity.setSecondsOnFire(j * 4);
            }

            this.doEnchantDamageEffects(this, entity);

        }


        return flag;
    }

    @Override
    public boolean hurt(DamageSource src, float amount) {
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

        if (!this.level.isClientSide && !peaceful && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
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
