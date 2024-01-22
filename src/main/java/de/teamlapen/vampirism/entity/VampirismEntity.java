package de.teamlapen.vampirism.entity;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.IEntityWithHome;
import de.teamlapen.vampirism.api.entity.IVampirismEntity;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import de.teamlapen.vampirism.util.Helper;
import de.teamlapen.vampirism.world.fog.FogLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Base class for most vampirism mobs
 */
public abstract class VampirismEntity extends PathfinderMob implements IEntityWithHome, IVampirismEntity {

    public static boolean spawnPredicateVampireFog(@NotNull LevelAccessor world, @NotNull BlockPos blockPos) {
        return world.getBiome(blockPos).is(ModTags.Biomes.IS_VAMPIRE_BIOME) || (world instanceof Level && FogLevel.getOpt((Level) world).map(vh -> vh.isInsideArtificialVampireFogArea(blockPos)).orElse(false));
    }

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return PathfinderMob.createLivingAttributes().add(Attributes.ATTACK_DAMAGE).add(Attributes.FOLLOW_RANGE, 16).add(Attributes.ATTACK_KNOCKBACK);
    }

    private final @NotNull Goal moveTowardsRestriction;
    protected boolean hasArms = true;
    protected boolean peaceful = false;
    /**
     * Whether the home should be saved to nbt or not
     */
    protected boolean saveHome = false;
    @Nullable
    private AABB home;
    private boolean moveTowardsRestrictionAdded = false;
    private int moveTowardsRestrictionPrio = -1;
    /**
     * Counter which reaches zero every 70 to 120 ticks
     */
    private int randomTickDivider;
    private boolean doImobConversion = false;

    public VampirismEntity(@NotNull EntityType<? extends VampirismEntity> type, @NotNull Level world) {
        super(type, world);
        moveTowardsRestriction = new MoveTowardsRestrictionGoal(this, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
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
    public AABB getHome() {
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
    public boolean checkSpawnRules(@NotNull LevelAccessor worldIn, @NotNull MobSpawnType spawnReasonIn) {
        return (peaceful || worldIn.getDifficulty() != Difficulty.PEACEFUL) && super.checkSpawnRules(worldIn, spawnReasonIn);
    }

    @Override
    public boolean isWithinHomeDistance(double x, double y, double z) {
        if (home != null) {
            return home.contains(new Vec3(x, y, z));
        }
        return true;
    }

    @Override
    public @NotNull BlockPos getHomePosition() {
        return getRestrictCenter();
    }

    @Override
    public boolean isWithinRestriction() {
        return this.isWithinHomeDistance(getX(), getY(), getZ());
    }

    @Override
    public boolean isWithinRestriction(@NotNull BlockPos pos) {
        return this.isWithinHomeDistance(pos);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("home")) {
            saveHome = true;
            int[] h = nbt.getIntArray("home");
            this.setHome(new AABB(h[0], h[1], h[2], h[3], h[4], h[5]));
            if (nbt.contains("homeMovePrio")) {
                this.setMoveTowardsRestriction(nbt.getInt("moveHomePrio"), true);
            }
        }
    }

    @Override
    public void restrictTo(@NotNull BlockPos pos, int distance) {
        this.setHomeArea(pos, distance);
    }

    @Override
    public void setHome(@Nullable AABB home) {
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
    public void setHomeArea(@NotNull BlockPos pos, int r) {
        this.setHome(new AABB(Vec3.atLowerCornerOf(pos.offset(-r, -r, -r)), Vec3.atLowerCornerWithOffset(pos.offset(r, r, r), 1,1,1)));
    }

    @Override
    public void tick() {
        super.tick();
        this.checkImobConversion();
        if (!this.level().isClientSide && !peaceful && this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
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

    @NotNull
    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.HOSTILE_SMALL_FALL, SoundEvents.HOSTILE_BIG_FALL);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.HOSTILE_HURT;
    }

    /**
     * @param iMob Whether we want the iMob or non iMob variant
     * @return Must be LivingEntity type
     */
    protected @NotNull EntityType<?> getIMobTypeOpt(boolean iMob) {
        return this.getType();
    }

    @NotNull
    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    @NotNull
    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    protected boolean isLowLightLevel(@NotNull LevelAccessor iWorld) {
        //copy of Monster#isDarkEnoughToSSpawn, but not requiring server level
        BlockPos blockpos = new BlockPos((int) this.getX(), (int) this.getBoundingBox().minY, (int) this.getZ());
        if (iWorld.getBrightness(LightLayer.SKY, blockpos) > this.random.nextInt(32)) {
            return false;
        } else if (iWorld.getBrightness(LightLayer.BLOCK, blockpos) > 0) {
            return false;
        } else {
            int i = iWorld.getMaxLocalRawBrightness(blockpos);

            if (iWorld instanceof Level && ((Level) iWorld).isThundering()) {
                i = iWorld.getMaxLocalRawBrightness(blockpos);
            }

            return i <= this.random.nextInt(8);
        }
    }

    /**
     * Called every 70 to 120 ticks during {@link Mob#customServerAiStep()}
     */
    @SuppressWarnings("EmptyMethod")
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
            if (active && moveTowardsRestrictionPrio == prio) {
                return;
            }
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
    public boolean shouldDropExperience() {
        return true;
    }

    /**
     * Fakes a teleportation and actually just kills the entity
     */
    protected void teleportAway() {
        this.setInvisible(true);
        ModParticles.spawnParticlesServer(this.level(), new GenericParticleOptions(new ResourceLocation("minecraft", "effect_6"), 10, 0x0A0A0A, 0.6F), this.getX(), this.getY(), this.getZ(), 20, 1, 1, 1, 0);
        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1, 1);
        this.discard();
    }

    private void checkImobConversion() {
        if (doImobConversion && !this.level().isClientSide) {
            if (this.tickCount % 256 == 0 && this.isAlive()) {
                boolean current = this instanceof Enemy;
                boolean convert = false;
                VampirismConfig.Server.IMobOptions opt = VampirismConfig.SERVER.entityIMob.get();
                if (ServerLifecycleHooks.getCurrentServer().isDedicatedServer()) {
                    convert = (opt == VampirismConfig.Server.IMobOptions.ALWAYS_IMOB) != current;
                } else {
                    if (opt == VampirismConfig.Server.IMobOptions.SMART) {
                        Player player = VampirismMod.proxy.getClientPlayer();
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
                    Helper.createEntity(t, this.level()).ifPresent(newEntity -> {
                        CompoundTag nbt = new CompoundTag();
                        this.saveWithoutId(nbt);
                        newEntity.load(nbt);
                        newEntity.setUUID(Mth.createInsecureUUID(this.random));
                        assert newEntity instanceof LivingEntity;
                        UtilLib.replaceEntity(this, (LivingEntity) newEntity);
                    });

                }
            }
        }
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return false;
    }
}