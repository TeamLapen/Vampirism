package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.IEntityWithHome;
import de.teamlapen.vampirism.api.entity.IVampirismEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModBiomes;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.world.VampirismWorld;
import net.minecraft.entity.*;
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
        if (world.getBrightness(LightType.SKY, blockPos) > random.nextInt(32)) {
            return false;
        } else {
            int lvt_3_1_ = world.getLevel().isThundering() ? world.getMaxLocalRawBrightness(blockPos, 10) : world.getMaxLocalRawBrightness(blockPos);
            return lvt_3_1_ <= random.nextInt(8);
        }
    }

    public static boolean spawnPredicateVampireFog(IWorld world, BlockPos blockPos) {
        return ModBiomes.VAMPIRE_FOREST.get().getRegistryName().equals(Helper.getBiomeId(world, blockPos)) || ModBiomes.VAMPIRE_FOREST_HILLS.get().getRegistryName().equals(Helper.getBiomeId(world, blockPos)) || (world instanceof World && VampirismWorld.getOpt((World) world).map(vh -> vh.isInsideArtificialVampireFogArea(blockPos)).orElse(false));
    }

    public static boolean spawnPredicateCanSpawn(EntityType<? extends MobEntity> entityType, IWorld world, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        BlockPos blockpos = blockPos.below();
        return spawnReason == SpawnReason.SPAWNER || world.getBlockState(blockpos).isValidSpawn(world, blockpos, entityType);
    }

    public static AttributeModifierMap.MutableAttribute getAttributeBuilder() {
        return CreatureEntity.createLivingAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.ATTACK_KNOCKBACK);
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
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        if (saveHome && home != null) {
            int[] h = {(int) home.minX, (int) home.minY, (int) home.minZ, (int) home.maxX, (int) home.maxY, (int) home.maxZ};
            nbt.putIntArray("home", h);
            if (moveTowardsRestrictionAdded && moveTowardsRestrictionPrio > -1) {
                nbt.putInt("homeMovePrio", moveTowardsRestrictionPrio);
            }
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getHome() {
        return home;
    }

    @Override
    public void aiStep() {
        if (hasArms) {
            this.updateSwingTime();
        }
        super.aiStep();
    }

    @Override
    public boolean checkSpawnRules(IWorld worldIn, SpawnReason spawnReasonIn) {
        return (peaceful || worldIn.getDifficulty() != Difficulty.PEACEFUL) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    @Override
    public boolean isWithinHomeDistance(double x, double y, double z) {
        if (home != null) {
            return home.contains(new Vector3d(x, y, z));
        }
        return true;
    }

    @Override
    public BlockPos getHomePosition() {
        return getRestrictCenter();
    }

    @Override
    public boolean isWithinRestriction() {
        return this.isWithinHomeDistance(getX(), getY(), getZ());
    }

    @Override
    public boolean isWithinRestriction(BlockPos pos) {
        return this.isWithinHomeDistance(pos);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
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
    public void restrictTo(BlockPos pos, int distance) {
        this.setHomeArea(pos, distance);
    }

    @Override
    public void setHome(@Nullable AxisAlignedBB home) {
        this.home = home;
        if (home != null) {
            int posX, posY, posZ;
            posX = (int) (home.minX + (home.maxX - home.minX) / 2);
            posY = (int) (home.minY + (home.maxY - home.minY) / 2);
            posZ = (int) (home.minZ + (home.maxZ - home.minZ) / 2);
            super.restrictTo(new BlockPos(posX, posY, posZ), (int) home.getSize());
        } else {
            super.restrictTo(new BlockPos(0, 0, 0), -1);
        }
    }

    @Override
    public void setHomeArea(BlockPos pos, int r) {
        this.setHome(new AxisAlignedBB(pos.offset(-r, -r, -r), pos.offset(r, r, r)));
    }

    @Override
    public void tick() {
        super.tick();
        this.checkImobConversion();
        if (!this.level.isClientSide && !peaceful && this.level.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (--this.randomTickDivider <= 0) {
            this.randomTickDivider = 70 + random.nextInt(50);
            onRandomTick();
        }
    }

    protected void disableImobConversion() {
        this.doImobConversion = false;
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
        return SoundEvents.HOSTILE_DEATH;
    }

    @Override
    protected SoundEvent getFallDamageSound(int heightIn) {
        return heightIn > 4 ? SoundEvents.HOSTILE_BIG_FALL : SoundEvents.HOSTILE_SMALL_FALL;
    }

    protected SoundEvent getHurtSound() {
        return SoundEvents.HOSTILE_HURT;
    }

    /**
     * @param iMob Whether we want the iMob or non iMob variant
     * @return Must be LivingEntity type
     */
    protected EntityType<?> getIMobTypeOpt(boolean iMob) {
        return this.getType();
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    protected boolean isLowLightLevel(IWorld iWorld) {
        BlockPos blockpos = new BlockPos(this.getX(), this.getBoundingBox().minY, this.getZ());

        if (iWorld.getBrightness(LightType.SKY, blockpos) > this.random.nextInt(32)) {
            return false;
        } else {
            int i = iWorld.getMaxLocalRawBrightness(blockpos);

            if (iWorld instanceof World && ((World) iWorld).isThundering()) {
                i = iWorld.getMaxLocalRawBrightness(blockpos);
            }

            return i <= this.random.nextInt(8);
        }
    }

    /**
     * Called every 70 to 120 ticks during {@link CreatureEntity#updateAITasks()}
     */
    protected void onRandomTick() {

    }

    protected void setDontDropEquipment() {
        Arrays.fill(this.armorDropChances, 0);
        Arrays.fill(this.handDropChances, 0);
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

    @Override
    protected boolean shouldDropExperience() {
        return true;
    }

    /**
     * Fakes a teleportation and actually just kills the entity
     */
    protected void teleportAway() {
        this.setInvisible(true);
        ModParticles.spawnParticlesServer(this.level, new GenericParticleData(ModParticles.GENERIC.get(), new ResourceLocation("minecraft", "effect_6"), 10, 0x0A0A0A, 0.6F), this.getX(), this.getY(), this.getZ(), 20, 1, 1, 1, 0);
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);

        this.remove();
    }

    private void checkImobConversion() {
        if (doImobConversion && !this.level.isClientSide) {
            if (this.tickCount % 256 == 0 && this.isAlive()) {
                boolean current = this instanceof IMob;
                boolean convert = false;
                VampirismConfig.Server.IMobOptions opt = VampirismConfig.SERVER.entityIMob.get();
                if (ServerLifecycleHooks.getCurrentServer().isDedicatedServer()) {
                    convert = (opt == VampirismConfig.Server.IMobOptions.ALWAYS_IMOB) != current;
                } else {
                    if (opt == VampirismConfig.Server.IMobOptions.SMART) {
                        PlayerEntity player = VampirismMod.proxy.getClientPlayer();
                        if (player != null && player.isAlive()) {
                            IPlayableFaction<?> f = VampirismPlayerAttributes.get(player).faction;
                            IFaction<?> thisFaction = ((IFactionEntity) this).getFaction();

                            boolean hostile = f == null ? thisFaction.isHostileTowardsNeutral() : !thisFaction.equals(f);
                            convert = hostile != current;

                        }
                    } else {
                        convert = (opt == VampirismConfig.Server.IMobOptions.ALWAYS_IMOB) != current;
                    }
                }
                if (convert) {
                    EntityType<?> t = getIMobTypeOpt(!current);
                    Helper.createEntity(t, this.level).ifPresent(newEntity -> {
                        CompoundNBT nbt = new CompoundNBT();
                        this.saveWithoutId(nbt);
                        newEntity.load(nbt);
                        newEntity.setUUID(MathHelper.createInsecureUUID(this.random));
                        assert newEntity instanceof LivingEntity;
                        UtilLib.replaceEntity(this, (LivingEntity) newEntity);
                    });

                }
            }
        }
    }

    @Override
    public boolean canBeLeashed(PlayerEntity player) {
        return false;
    }
}