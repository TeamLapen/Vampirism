package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.EntityClassType;
import de.teamlapen.vampirism.api.entity.IEntityWithHome;
import de.teamlapen.vampirism.api.entity.IVampirismEntity;
import de.teamlapen.vampirism.api.entity.actions.EntityActionTier;
import de.teamlapen.vampirism.api.entity.actions.IEntityAction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.action.EntityActionHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for most vampirism mobs
 */
public abstract class EntityVampirism extends EntityCreature implements IEntityWithHome, IVampirismEntity {

    private final EntityAIBase moveTowardsRestriction;
    protected boolean hasArms = true;
    protected boolean peaceful = false;
    /** available actions for AI task & task */
    protected EntityActionHandler<?> entityActionHandler;
    protected EntityClassType entityclass;
    protected EntityActionTier entitytier;
    /**
     * Whether the home should be saved to nbt or not
     */
    protected boolean saveHome = false;
    private AxisAlignedBB home;
    private boolean moveTowardsRestrictionAdded = false;
    private int moveTowardsRestrictionPrio = -1;
    /**
     * Counter which reaches zero every 70 to 120 ticks
     */
    private int randomTickDivider;

    public EntityVampirism(World world) {
        super(world);
        setupEntityClass();
        moveTowardsRestriction = new EntityAIMoveTowardsRestriction(this, 1.0F);
    }

    public boolean attackEntityAsMob(Entity entity) {
        float f = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
        int i = 0;

        if (entity instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) entity).getCreatureAttribute());
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
    public void detachHome() {
        this.home = null;
    }

    @Override
    public boolean getCanSpawnHere() {
        return (peaceful || this.world.getDifficulty() != EnumDifficulty.PEACEFUL) && super.getCanSpawnHere();
    }

    @Nullable
    @Override
    public AxisAlignedBB getHome() {
        return home;
    }

    @Override
    public void setHome(@Nullable AxisAlignedBB home) {
        this.home = home;
    }

    @Override
    public BlockPos getHomePosition() {
        if (!hasHome())
            return new BlockPos(0, 0, 0);
        int posX, posY, posZ;
        posX = (int) (home.minX + (home.maxX - home.minX) / 2);
        posY = (int) (home.minY + (home.maxY - home.minY) / 2);
        posZ = (int) (home.minZ + (home.maxZ - home.minZ) / 2);
        return new BlockPos(posX, posY, posZ);
    }

    @Override
    public boolean hasHome() {
        return home != null;
    }

    @Override
    public boolean isWithinHomeDistance(double x, double y, double z) {
        if (home != null) {
            return home.contains(new Vec3d(x, y, z));
        }
        return true;
    }

    @Override
    public boolean isWithinHomeDistanceCurrentPosition() {
        return this.isWithinHomeDistance(posX, posY, posZ);
    }

    @Override
    public boolean isWithinHomeDistanceFromPosition(BlockPos pos) {
        return this.isWithinHomeDistance(pos);
    }

    @Override
    public void livingTick() {
        if (hasArms) {
            this.updateArmSwingProgress();
        }
        super.livingTick();
        if (entityActionHandler != null) {
            entityActionHandler.handle();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isRemote && !peaceful && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    public void readAdditional(NBTTagCompound nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("home")) {
            saveHome = true;
            int[] h = nbt.getIntArray("home");
            home = new AxisAlignedBB(h[0], h[1], h[2], h[3], h[4], h[5]);
            if (nbt.contains("homeMovePrio")) {
                this.setMoveTowardsRestriction(nbt.getInt("moveHomePrio"), true);
            }
        }
        if (entityActionHandler != null) {
            entityActionHandler.read(nbt);
        }
        if (nbt.contains("entityclasstype")) {
            EntityClassType type = EntityClassType.getEntityClassType(nbt.getInt("entityclasstype"));
            if (type != null)
                entityclass = type;
        }
    }

    @Override
    public void setHomeArea(BlockPos pos, int r) {
        this.setHome(new AxisAlignedBB(pos.add(-r, -r, -r), pos.add(r, r, r)));
    }

    @Override
    public void setHomePosAndDistance(BlockPos pos, int distance) {
        this.setHomeArea(pos, distance);
    }

    @Override
    public void writeAdditional(NBTTagCompound nbt) {
        super.writeAdditional(nbt);
        if (saveHome && hasHome()) {
            int[] h = { (int) home.minX, (int) home.minY, (int) home.minZ, (int) home.maxX, (int) home.maxY, (int) home.maxZ };
            nbt.putIntArray("home", h);
            if (moveTowardsRestrictionAdded && moveTowardsRestrictionPrio > -1) {
                nbt.putInt("homeMovePrio", moveTowardsRestrictionPrio);
            }
        }
        if (entityActionHandler != null) {
            entityActionHandler.write(nbt);
        }
        nbt.putInt("entityclasstype", EntityClassType.getID(entityclass));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }

    /**
     * Called after an EntityLivingBase has been attacked as mob
     */
    protected void attackedEntityAsMob(EntityLivingBase entity) {
    }

    @Override
    protected boolean canDropLoot() {
        return true;
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

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HOSTILE_DEATH;
    }

    @Override
    protected SoundEvent getFallSound(int heightIn) {
        return heightIn > 4 ? SoundEvents.ENTITY_HOSTILE_BIG_FALL : SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
    }

    protected SoundEvent getHurtSound() {
        return SoundEvents.ENTITY_HOSTILE_HURT;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    protected boolean isLowLightLevel() {
        BlockPos blockpos = new BlockPos(this.posX, this.getBoundingBox().minY, this.posZ);

        if (this.world.getLightFor(EnumSkyBlock.SKY, blockpos) > this.rand.nextInt(32)) {
            return false;
        } else {
            int i = this.world.getLightFromNeighbors(blockpos);

            if (this.world.isThundering()) {
                int j = this.world.getSkylightSubtracted();
                this.world.setSkylightSubtracted(10);
                i = this.world.getLightFromNeighbors(blockpos);
                this.world.setSkylightSubtracted(j);
            }

            return i <= this.rand.nextInt(8);
        }
    }

    /**
     * Called every 70 to 120 ticks during {@link EntityCreature#updateAITasks()}
     */
    protected void onRandomTick() {

    }

    protected void setDontDropEquipment() {
        for (int i = 0; i < this.inventoryArmorDropChances.length; ++i) {
            this.inventoryArmorDropChances[i] = 0;
        }

        for (int j = 0; j < this.inventoryHandsDropChances.length; ++j) {
            this.inventoryHandsDropChances[j] = 0;
        }
    }

    /**
     * Add the MoveTowardsRestriction task with the given priority.
     * Overrides prior priorities if existent
     *
     * @param prio
     *            Priority of the task
     * @param active
     *            If the task should be active or not
     */
    protected void setMoveTowardsRestriction(int prio, boolean active) {
        if (moveTowardsRestrictionAdded) {
            if (active && moveTowardsRestrictionPrio == prio)
                return;
            this.tasks.removeTask(moveTowardsRestriction);
            moveTowardsRestrictionAdded = false;
        }
        if (active) {
            tasks.addTask(prio, moveTowardsRestriction);
            moveTowardsRestrictionAdded = true;
            moveTowardsRestrictionPrio = prio;
        }

    }

    /**
     * Fakes a teleportation and actually just kills the entity
     */
    protected void teleportAway() {
        this.setInvisible(true);
        VampLib.proxy.getParticleHandler().spawnParticles(this.world, ModParticles.GENERIC_PARTICLE, this.posX, this.posY + this.height / 2, this.posZ, 20, 1, this.rand, 134, 10, 0x0A0A0A, 0.6);
        this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1, 1);

        this.remove();
    }

    @Override
    protected void updateAITasks() {
        super.updateAITasks();
        if (--this.randomTickDivider <= 0) {
            this.randomTickDivider = 70 + rand.nextInt(50);
            onRandomTick();
        }
    }

    /**
     * gets all available actions for this entity.
     * 
     * @throws ClassCastException
     *             if the entity isn't instanceof {@link IFactionEntity}
     */
    public List<IEntityAction> getAvailableActions() {
        return VampirismAPI.entityActionManager().getAllEntityActionsByTierAndClassType(((IFactionEntity) this).getFaction(), entitytier, entityclass);
    }

    public EntityClassType getEntityClass() {
        return entityclass;
    }

    public EntityActionTier getEntityTier() {
        return entitytier;
    }

    /**
     * sets entity Tier & Class, applies class modifier
     */
    @Nullable
    protected void setupEntityClass() {
        entitytier = EntityActionTier.Default;
        entityclass = EntityClassType.getRandomClass(this.getRNG());
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(entityclass.getHealthModifier());
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(entityclass.getDamageModifier());
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(entityclass.getSpeedModifier());
    }
}