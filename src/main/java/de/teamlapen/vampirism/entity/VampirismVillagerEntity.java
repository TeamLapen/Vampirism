package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
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

    public boolean attackEntityAsMob(Entity entity) {
        float f = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
        int i = 0;

        if (entity instanceof LivingEntity) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity) entity).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if (flag) {
            if (i > 0) {
                entity.addVelocity(-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F, 0.1D, MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F);
                this.setMotion(this.getMotion().mul(0.6D, 1D, 0.6D));
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0) {
                entity.setFire(j * 4);
            }

            this.applyEnchantments(this, entity);

        }


        return flag;
    }

    @Override
    public boolean attackEntityFrom(DamageSource src, float p_70097_2_) {
        if (this.isInvulnerableTo(src)) {
            return false;
        } else if (super.attackEntityFrom(src, p_70097_2_)) {
            Entity entity = src.getTrueSource();
            if (entity instanceof LivingEntity) {
                this.setAttackTarget((LivingEntity) entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return (peaceful || worldIn.getDifficulty() != Difficulty.PEACEFUL) && super.canSpawn(worldIn, spawnReasonIn);
    }

//    @Nullable
//    public StructureStart getVillage() {
//        if (cachedVillage == StructureStart.DUMMY)
//            return null;
//        return cachedVillage;
//    }

    @Override
    public void livingTick() {
        this.updateArmSwingProgress();
        super.livingTick();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isRemote && !peaceful && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return VillagerEntity.registerAttributes().createMutableAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        if (--this.randomTickDivider <= 0) {
            this.randomTickDivider = 200;
        }
    }
}
