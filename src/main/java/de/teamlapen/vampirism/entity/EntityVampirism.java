package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.IEntityWithHome;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

/**
 * Base class for most vampirism mobs
 */
public abstract class EntityVampirism extends EntityCreature implements IEntityWithHome {

    private final EntityAIBase moveTowardsRestriction;
    protected boolean hasArms = true;
    protected boolean peaceful = false;
    /**
     * Whether the home should be saved to nbt or not
     */
    protected boolean saveHome = false;
    private AxisAlignedBB home;
    private boolean moveTowardsRestrictionAdded = false;

    public EntityVampirism(World world) {
        super(world);
        moveTowardsRestriction = new EntityAIMoveTowardsRestriction(this, 1.0F);
    }

    public boolean attackEntityAsMob(Entity entity) {
        float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        int i = 0;

        if (entity instanceof EntityLivingBase) {
            f += EnchantmentHelper.func_152377_a(this.getHeldItem(), ((EntityLivingBase) entity).getCreatureAttribute());
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

            if (entity instanceof EntityLivingBase) {
                this.attackedEntityAsMob((EntityLivingBase) entity);
            }
        }


        return flag;
    }

    @Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if (this.isEntityInvulnerable(p_70097_1_)) {
            return false;
        } else if (super.attackEntityFrom(p_70097_1_, p_70097_2_)) {
            Entity entity = p_70097_1_.getEntity();
            if (entity instanceof EntityLivingBase) {
                this.setAttackTarget((EntityLivingBase) entity);
            }
            return true;
        }
        return false;
    }

    @Override
    public void detachHome() {
        this.home = null;
    }

    public boolean getCanSpawnHere() {
        return (peaceful || this.worldObj.getDifficulty() != EnumDifficulty.PEACEFUL) && super.getCanSpawnHere();
    }

    public AxisAlignedBB getHome() {
        return home;
    }

    public void setHome(AxisAlignedBB home) {
        this.home = home;
    }

    public BlockPos getHomePosition() {
        if (!hasHome()) return new BlockPos(0, 0, 0);
        int posX, posY, posZ;
        posX = (int) (home.minX + (home.maxX - home.minX) / 2);
        posY = (int) (home.minY + (home.maxY - home.minY) / 2);
        posZ = (int) (home.minZ + (home.maxZ - home.minZ) / 2);
        return new BlockPos(posX, posY, posZ);
    }

    public boolean hasHome() {
        return home != null;
    }

    public boolean isWithinHomeDistance(double x, double y, double z) {
        if (home != null) {
            return home.isVecInside(new Vec3(x, y, z));
        }
        return true;
    }

    public boolean isWithinHomeDistance(BlockPos pos) {
        return this.isWithinHomeDistance(pos.getX(), pos.getY(), pos.getZ());
    }

    public boolean isWithinHomeDistance(int posX, int posY, int posZ) {
        return this.isWithinHomeDistance((double) posX, (double) posY, (double) posZ);
    }

    @Override
    public boolean isWithinHomeDistanceCurrentPosition() {
        return this.isWithinHomeDistance(posX, posY, posZ);
    }

    @Override
    public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
        return this.isWithinHomeDistance(pos);
    }

    public void onLivingUpdate() {
        if (hasArms) {
            this.updateArmSwingProgress();
        }
        super.onLivingUpdate();
    }

    public void onUpdate() {
        super.onUpdate();

        if (!this.worldObj.isRemote && !peaceful && this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.setDead();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        if (nbt.hasKey("home")) {
            saveHome = true;
            int[] h = nbt.getIntArray("home");
            home = new AxisAlignedBB(h[0], h[1], h[2], h[3], h[4], h[5]);
        }
    }

    public void setHomeArea(BlockPos pos, int r) {
        this.setHome(new AxisAlignedBB(pos.add(-r, -r, -r), pos.add(r, r, r)));
    }

    @Override
    public void setHomePosAndDistance(BlockPos pos, int distance) {
        this.setHomeArea(pos, distance);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        if (saveHome && hasHome()) {
            int[] h = {(int) home.minX, (int) home.minY, (int) home.minZ, (int) home.maxX, (int) home.maxY, (int) home.maxZ};
            nbt.setIntArray("home", h);
        }
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
    }

    protected void attackedEntityAsMob(EntityLivingBase entity) {
    }

    /**
     * Clears tasks and targetTasks
     */
    protected void clearAITasks() {
        tasks.taskEntries.clear();
        targetTasks.taskEntries.clear();
    }

    /**
     * Removes the MoveTowardsRestriction task
     */
    protected void disableMoveTowardsRestriction() {
        if (moveTowardsRestrictionAdded) {
            this.tasks.removeTask(moveTowardsRestriction);
            moveTowardsRestrictionAdded = false;
        }
    }

    protected boolean func_146066_aG() {
        return true;
    }

    protected String func_146067_o(int p_146067_1_) {
        return p_146067_1_ > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
    }

    /**
     * Returns the sound this mob makes on death.
     */
    protected String getDeathSound() {
        return "game.hostile.die";
    }

    /**
     * Returns the sound this mob makes when it is hurt.
     */
    protected String getHurtSound() {
        return "game.hostile.hurt";
    }

    protected String getSplashSound() {
        return "game.hostile.swim.splash";
    }

    protected String getSwimSound() {
        return "game.hostile.swim";
    }

    /**
     * Add the MoveTowardsRestriction task with the given priority.
     * Overrides prior priorities if existent
     *
     * @param prio
     */
    protected void setMoveTowardsRestriction(int prio) {
        if (moveTowardsRestrictionAdded) {
            this.tasks.removeTask(moveTowardsRestriction);
        }
        tasks.addTask(prio, moveTowardsRestriction);
        moveTowardsRestrictionAdded = true;
    }

    /**
     * Fakes a teleportation and actually just kills the entity
     */
    protected void teleportAway() {
        this.setInvisible(true);
        Helper.spawnParticlesAroundEntity(this, EnumParticleTypes.PORTAL, 5, 64);

        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "mob.endermen.portal", 1.0F, 1.0F);
        this.playSound("mob.endermen.portal", 1.0F, 1.0F);

        this.setDead();
    }
}