package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.world.IVampirismVillage;
import de.teamlapen.vampirism.world.villages.VampirismVillageHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Villager extended with the ability to attack and some other things
 */
public class EntityVillagerVampirism extends VillagerEntity {

    protected boolean peaceful = false;
    protected
    @Nullable
    IVampirismVillage cachedVillage;
    /**
     * A timer which reaches 0 every 70 to 120 ticks
     */
    private int randomTickDivider;

    public EntityVillagerVampirism(EntityType<? extends EntityVillagerVampirism> type, World worldIn) {
        super(type, worldIn);
    }

    public EntityVillagerVampirism(EntityType<? extends EntityVillagerVampirism> type, World worldIn, IVillagerType villagerType) {
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
                entity.addVelocity((double) (-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F), 0.1D, (double) (MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) i * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
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
            if (cachedVillage != null) {
                cachedVillage.addOrRenewAggressor(entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canSpawn(IWorld worldIn, boolean fromSpawner) {
        return (peaceful || this.world.getDifficulty() != Difficulty.PEACEFUL) && super.canSpawn(worldIn, fromSpawner);
    }

    @Nullable
    public IVampirismVillage getVampirismVillage() {
        return cachedVillage;
    }

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

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        if (--this.randomTickDivider <= 0) {
            this.randomTickDivider = 70 + rand.nextInt(50);
            this.cachedVillage = VampirismVillageHelper.getNearestVillage(world, getPosition(), 32);
        }

    }

}
