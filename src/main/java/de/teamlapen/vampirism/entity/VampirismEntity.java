package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.api.entity.IEntityWithHome;
import de.teamlapen.vampirism.api.entity.IVampirismEntity;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.tileentity.TotemTile;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Base class for most vampirism mobs
 */
public abstract class VampirismEntity extends CreatureEntity implements IEntityWithHome, IVampirismEntity {

    private final Goal moveTowardsRestriction;
    protected boolean hasArms = true;
    protected boolean peaceful = false;

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

    public VampirismEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
        moveTowardsRestriction = new MoveTowardsRestrictionGoal(this, 1.0F);
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
                this.setMotion(this.getMotion().mul(0.6D, 1D, 0.6D));
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if (j > 0) {
                entity.setFire(j * 4);
            }

            this.applyEnchantments(this, entity);

            if (entity instanceof LivingEntity) {
                this.attackedEntityAsMob((LivingEntity) entity);
            }
        }

        return flag;
    }

    @Override
    public boolean detachHome() {
        this.home = null;
        return true;
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return (peaceful || this.world.getDifficulty() != Difficulty.PEACEFUL) && super.canSpawn(worldIn, spawnReasonIn);
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
        if (home == null)
            return new BlockPos(0, 0, 0);
        int posX, posY, posZ;
        posX = (int) (home.minX + (home.maxX - home.minX) / 2);
        posY = (int) (home.minY + (home.maxY - home.minY) / 2);
        posZ = (int) (home.minZ + (home.maxZ - home.minZ) / 2);
        return new BlockPos(posX, posY, posZ);
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
    }

    @Override
    public void readAdditional(CompoundNBT nbt) {
        super.readAdditional(nbt);
        if (nbt.contains("home")) {
            saveHome = true;
            int[] h = nbt.getIntArray("home");
            home = new AxisAlignedBB(h[0], h[1], h[2], h[3], h[4], h[5]);
            if (nbt.contains("homeMovePrio")) {
                this.setMoveTowardsRestriction(nbt.getInt("moveHomePrio"), true);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.world.isRemote && !peaceful && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
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
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        if (saveHome && home != null) {
            int[] h = { (int) home.minX, (int) home.minY, (int) home.minZ, (int) home.maxX, (int) home.maxY, (int) home.maxZ };
            nbt.putIntArray("home", h);
            if (moveTowardsRestrictionAdded && moveTowardsRestrictionPrio > -1) {
                nbt.putInt("homeMovePrio", moveTowardsRestrictionPrio);
            }
        }
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
    }

    /**
     * Called after an EntityLivingBase has been attacked as mob
     */
    protected void attackedEntityAsMob(LivingEntity entity) {
    }

    @Override
    protected boolean canDropLoot() {
        return true;
    }


    /**
     * Removes the MoveTowardsRestriction task
     */
    protected void disableMoveTowardsRestriction() {
        if (moveTowardsRestrictionAdded) {
            this.goalSelector.removeGoal(moveTowardsRestriction);
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

        if (this.world.getLightFor(LightType.SKY, blockpos) > this.rand.nextInt(32)) {
            return false;
        } else {
            int i = this.world.getLight(blockpos);

            if (this.world.isThundering()) {
                int j = this.world.getSkylightSubtracted();
                this.world.setLastLightningBolt(10);
                i = this.world.getLight(blockpos);
                this.world.setLastLightningBolt(j);
            }

            return i <= this.rand.nextInt(8);
        }
    }

    /**
     * Called every 70 to 120 ticks during {@link CreatureEntity#updateAITasks()}
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
            this.goalSelector.removeGoal(moveTowardsRestriction);
            moveTowardsRestrictionAdded = false;
        }
        if (active) {
            goalSelector.addGoal(prio, moveTowardsRestriction);
            moveTowardsRestrictionAdded = true;
            moveTowardsRestrictionPrio = prio;
        }

    }

    /**
     * Fakes a teleportation and actually just kills the entity
     */
    protected void teleportAway() {
        this.setInvisible(true);
        ModParticles.spawnParticles(this.world, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "effect_6"), 10, 0x0A0A0A, 0.6F), this.posX, this.posY, this.posZ, 20, 1, this.rand);//TODO particle textureindex: 134
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

    public static boolean spawnPredicateVampire(EntityType<? extends VampirismEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && (spawnPredicateLight(world, blockPos, random) || spawnPredicateVampireFog(world, blockPos)) && spawnPredicateCanSpawn(entityType, world, spawnReason, blockPos, random);
    }

    public static boolean spawnPredicateHunter(EntityType<? extends VampirismEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && spawnPredicateCanSpawn(entityType, world, spawnReason, blockPos, random);
    }

    public static boolean spawnPredicateLight(IWorld world, BlockPos blockPos, Random random) {
        if (world.getLightFor(LightType.SKY, blockPos) > random.nextInt(32)) {
            return false;
        } else {
            int lvt_3_1_ = world.getWorld().isThundering() ? world.getNeighborAwareLightSubtracted(blockPos, 10) : world.getLight(blockPos);
            return lvt_3_1_ <= random.nextInt(8);
        }
    }

    public static boolean spawnPredicateVampireFog(IWorld world, BlockPos blockPos) {
        return world.getBiome(blockPos).getRegistryName() == ModBiomes.vampire_forest.getRegistryName() || TotemTile.isInsideVampireAreaCached(world.getDimension(), blockPos);
    }

    public static boolean spawnPredicateCanSpawn(EntityType<? extends MobEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        BlockPos blockpos = blockPos.down();
        return spawnReason == SpawnReason.SPAWNER || world.getBlockState(blockpos).canEntitySpawn(world, blockpos, entityType);
    }
}