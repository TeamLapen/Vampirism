package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.util.Helper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

/**
 * Base class for most vampirism mobs
 */
public abstract class EntityVampirism extends EntityCreature {

    protected boolean hasArms = false;
    protected boolean peaceful = false;
    private AxisAlignedBB home;
    protected boolean saveHome = false;

    public EntityVampirism(World p_i1595_1_) {
        super(p_i1595_1_);
    }

    public boolean hasHome() {
        return home != null;
    }

    protected void attackedEntityAsMob(EntityLivingBase entity) {
    }

    public void onLivingUpdate() {
        if (hasArms) {
            this.updateArmSwingProgress();
        }
        super.onLivingUpdate();
    }

    public void onUpdate() {
        super.onUpdate();

        if (!this.worldObj.isRemote && !peaceful && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL) {
            this.setDead();
        }
    }

    public void setHome(AxisAlignedBB home) {
        this.home = home;
    }


    @Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if (this.isEntityInvulnerable()) {
            return false;
        } else if (super.attackEntityFrom(p_70097_1_, p_70097_2_)) {
            Entity entity = p_70097_1_.getEntity();
            if (entity instanceof EntityLivingBase) {
                this.setTarget(entity);
            }
            return true;
        }
        return false;
    }

    protected String getSwimSound() {
        return "game.hostile.swim";
    }

    protected String getSplashSound() {
        return "game.hostile.swim.splash";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound() {
        return "game.hostile.hurt";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound() {
        return "game.hostile.die";
    }

    protected String func_146067_o(int p_146067_1_) {
        return p_146067_1_ > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
    }

    public boolean attackEntityAsMob(Entity entity) {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int i = 0;

        if (entity instanceof EntityLivingBase) {
            f += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase) entity);
            i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase) entity);
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

            if (entity instanceof EntityLivingBase) {
                EnchantmentHelper.func_151384_a((EntityLivingBase) entity, this);
            }

            EnchantmentHelper.func_151385_b(this, entity);

            if (entity instanceof EntityLivingBase) {
                this.attackedEntityAsMob((EntityLivingBase) entity);
            }
        }


        return flag;
    }

    @Override
    public boolean isAIEnabled() {
        return true;
    }


    public boolean getCanSpawnHere() {
        return (peaceful || this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL) && super.getCanSpawnHere();
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
    }

    protected boolean func_146066_aG() {
        return true;
    }

    /**
     * Fakes a teleportation and actually just kills the entity
     */
    protected void teleportAway() {
        this.setInvisible(true);
        Helper.spawnParticlesAroundEntity(this, "portal", 5, 64);

        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.endermen.portal", 1.0F, 1.0F);
        this.playSound("mob.endermen.portal", 1.0F, 1.0F);

        this.setDead();
    }

    public boolean isWithinHomeDistance(double x, double y, double z) {
        if (home != null) {
            return home.isVecInside(Vec3.createVectorHelper(x, y, z));
        }
        return true;
    }

    @Override
    public boolean isWithinHomeDistanceCurrentPosition() {
        return this.isWithinHomeDistance(posX, posY, posZ);
    }

    @Override
    public boolean isWithinHomeDistance(int p_110176_1_, int p_110176_2_, int p_110176_3_) {
        return this.isWithinHomeDistance((double) p_110176_1_, (double) p_110176_2_, (double) p_110176_3_);
    }

    @Override
    public void setHomeArea(int x, int y, int z, int r) {
        this.setHome(AxisAlignedBB.getBoundingBox(x - r, y - r, z - r, x + r, y + r, z + r));
    }

    @Override
    public ChunkCoordinates getHomePosition() {
        if (!hasHome()) return new ChunkCoordinates(0, 0, 0);
        ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
        center.posX = (int) (home.minX + (home.maxX - home.minX) / 2);
        center.posY = (int) (home.minY + (home.maxY - home.minY) / 2);
        center.posZ = (int) (home.minZ + (home.maxZ - home.minZ) / 2);
        return center;
    }

    @Override
    public void detachHome() {
        this.home = null;
    }

    @Override
    protected void updateEntityActionState() {
    }

    public AxisAlignedBB getHome() {
        return home;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (saveHome && hasHome()) {
            int[] h = {(int) home.minX, (int) home.minY, (int) home.minZ, (int) home.maxX, (int) home.maxY, (int) home.maxZ};
            nbt.setIntArray("home", h);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("home")) {
            saveHome = true;
            int[] h = nbt.getIntArray("home");
            home = AxisAlignedBB.getBoundingBox(h[0], h[1], h[2], h[3], h[4], h[5]);
        }
    }
}
