package de.teamlapen.vampirism.entity;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.IEntityWithHome;
import de.teamlapen.vampirism.api.entity.IVampirismEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.tileentity.TotemHelper;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Random;

/**
 * Base class for most vampirism mobs
 */
public abstract class VampirismEntity extends CreatureEntity implements IEntityWithHome, IVampirismEntity {

    public static boolean spawnPredicateLight(IServerWorld world, BlockPos blockPos, Random random) {
        if (world.getLightFor(LightType.SKY, blockPos) > random.nextInt(32)) {
            return false;
        } else {
            int lvt_3_1_ = world.getWorld().isThundering() ? world.getNeighborAwareLightSubtracted(blockPos, 10) : world.getLight(blockPos);
            return lvt_3_1_ <= random.nextInt(8);
        }
    }

    public static boolean spawnPredicateVampireFog(IWorld world, BlockPos blockPos) {
        return ModBiomes.vampire_forest.getRegistryName().equals(Helper.getBiomeId(world, blockPos)) || ModBiomes.vampire_forest_hills.getRegistryName().equals(Helper.getBiomeId(world, blockPos)) || (world instanceof World && TotemHelper.isInsideVampireAreaCached(((World) world).getDimensionKey(), blockPos));
    }

    public static boolean spawnPredicateCanSpawn(EntityType<? extends MobEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        BlockPos blockpos = blockPos.down();
        return spawnReason == SpawnReason.SPAWNER || world.getBlockState(blockpos).canEntitySpawn(world, blockpos, entityType);
    }
    private final Goal moveTowardsRestriction;
    protected boolean hasArms = true;
    protected boolean peaceful = false;
    /**
     * Whether the home should be saved to nbt or not
     */
    protected boolean saveHome = false;
    @Nullable
    private AxisAlignedBB home;
    private boolean moveTowardsRestrictionAdded = false;
    private int moveTowardsRestrictionPrio = -1;
    /**
     * Counter which reaches zero every 70 to 120 ticks
     */
    private int randomTickDivider;
    private boolean doImobConversion = false;

    public VampirismEntity(EntityType<? extends VampirismEntity> type, World world) {
        super(type, world);
        moveTowardsRestriction = new MoveTowardsRestrictionGoal(this, 1.0F);
    }

    @Override
    public boolean canSpawn(IWorld worldIn, SpawnReason spawnReasonIn) {
        return (peaceful || worldIn.getDifficulty() != Difficulty.PEACEFUL) && super.canSpawn(worldIn, spawnReasonIn);
    }

    @Nullable
    @Override
    public AxisAlignedBB getHome() {
        return home;
    }

    @Override
    public void setHome(@Nullable AxisAlignedBB home) {
        this.home = home;
        if (home != null) {
            int posX, posY, posZ;
            posX = (int) (home.minX + (home.maxX - home.minX) / 2);
            posY = (int) (home.minY + (home.maxY - home.minY) / 2);
            posZ = (int) (home.minZ + (home.maxZ - home.minZ) / 2);
            super.setHomePosAndDistance(new BlockPos(posX, posY, posZ), (int) home.getAverageEdgeLength());
        } else {
            super.setHomePosAndDistance(new BlockPos(0, 0, 0), -1);
        }
    }

    @Override
    public boolean isWithinHomeDistance(double x, double y, double z) {
        if (home != null) {
            return home.contains(new Vector3d(x, y, z));
        }
        return true;
    }

    @Override
    public boolean isWithinHomeDistanceCurrentPosition() {
        return this.isWithinHomeDistance(getPosX(), getPosY(), getPosZ());
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
            this.setHome(new AxisAlignedBB(h[0], h[1], h[2], h[3], h[4], h[5]));
            if (nbt.contains("homeMovePrio")) {
                this.setMoveTowardsRestriction(nbt.getInt("moveHomePrio"), true);
            }
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
    public void tick() {
        super.tick();
        this.checkImobConversion();
        if (!this.world.isRemote && !peaceful && this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    public void writeAdditional(CompoundNBT nbt) {
        super.writeAdditional(nbt);
        if (saveHome && home != null) {
            int[] h = {(int) home.minX, (int) home.minY, (int) home.minZ, (int) home.maxX, (int) home.maxY, (int) home.maxZ};
            nbt.putIntArray("home", h);
            if (moveTowardsRestrictionAdded && moveTowardsRestrictionPrio > -1) {
                nbt.putInt("homeMovePrio", moveTowardsRestrictionPrio);
            }
        }
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

    protected void enableImobConversion() {
        if (this instanceof IFactionEntity) {
            this.doImobConversion = true;
        } else {
            throw new IllegalStateException("Can only do IMob conversion for IFactionEntity");
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

    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return this.getType();
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    protected boolean isLowLightLevel(IWorld iWorld) {
        BlockPos blockpos = new BlockPos(this.getPosX(), this.getBoundingBox().minY, this.getPosZ());

        if (iWorld.getLightFor(LightType.SKY, blockpos) > this.rand.nextInt(32)) {
            return false;
        } else {
            int i = iWorld.getLight(blockpos);

            if (iWorld instanceof World && ((World) iWorld).isThundering()) {
                i = iWorld.getLight(blockpos);
            }

            return i <= this.rand.nextInt(8);
        }
    }

    /**
     * Called every 70 to 120 ticks during {@link CreatureEntity#updateAITasks()}
     */
    protected void onRandomTick() {

    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return CreatureEntity.registerAttributes().createMutableAttribute(Attributes.ATTACK_DAMAGE).createMutableAttribute(SharedMonsterAttributes.FOLLOW_RANGE, 16).createMutableAttribute(Attributes.ATTACK_KNOCKBACK);
    }

    protected void setDontDropEquipment() {
        Arrays.fill(this.inventoryArmorDropChances, 0);
        Arrays.fill(this.inventoryHandsDropChances, 0);
    }

    /**
     * Add the MoveTowardsRestriction task with the given priority.
     * Overrides prior priorities if existent
     * e
     *
     * @param prio   Priority of the task
     * @param active If the task should be active or not
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
        ModParticles.spawnParticlesServer(this.world, new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "effect_6"), 10, 0x0A0A0A, 0.6F), this.getPosX(), this.getPosY(), this.getPosZ(), 20, 1, 1, 1, 0);
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

    private void checkImobConversion() {
        if (doImobConversion && !this.world.isRemote) {
            if (this.ticksExisted % 256 == 0) {
                boolean current = this instanceof IMob;
                boolean convert = false;
                VampirismConfig.Server.IMobOptions opt = VampirismConfig.SERVER.entityIMob.get();
                if (ServerLifecycleHooks.getCurrentServer().isDedicatedServer()) {
                    convert = (opt == VampirismConfig.Server.IMobOptions.ALWAYS_IMOB) != current;
                } else {
                    if (opt == VampirismConfig.Server.IMobOptions.SMART) {
                        PlayerEntity player = VampirismMod.proxy.getClientPlayer();
                        if (player != null && player.isAlive()) {
                            IPlayableFaction f = FactionPlayerHandler.get(player).getCurrentFaction();
                            IFaction thisFaction = ((IFactionEntity) this).getFaction();

                            boolean hostile = f == null ? thisFaction.isHostileTowardsNeutral() : !thisFaction.equals(f);
                            convert = hostile != current;

                        }
                    } else {
                        convert = (opt == VampirismConfig.Server.IMobOptions.ALWAYS_IMOB) != current;
                    }
                }
                if (convert) {
                    EntityType<?> t = getIMobTypeOpt(!current);
                    Helper.createEntity(t, this.world).ifPresent(newEntity -> {
                        CompoundNBT nbt = new CompoundNBT();
                        this.writeWithoutTypeId(nbt);
                        newEntity.read(nbt);
                        newEntity.setUniqueId(MathHelper.getRandomUUID(this.rand));
                        this.remove();
                        this.world.addEntity(newEntity);
                    });

                }
            }
        }
    }
}