package de.teamlapen.lib.lib.util;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;

/**
 * Simple mob spawning logic. More configurable than {@link MobSpawnerBaseLogic} but less functional.
 */
public abstract class SimpleSpawnerLogic {

    private static final int MOB_COUNT_DIV = (int) Math.pow(17.0D, 2.0D);

    @Nullable
    private ResourceLocation entityName = null;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int activateRange = 16;
    private int spawnCount = 1;
    private int maxNearbyEntities = 4;
    private int spawnRange = 4;
    private int spawnDelay = 20;
    private int spawnedToday = 0;
    private long spawnedLast = 0L;
    private boolean flag = true;
    private EnumCreatureType limitType;

    @Nullable
    public ResourceLocation getEntityName() {
        return entityName;
    }

    public void setEntityName(@Nullable ResourceLocation entityName) {
        this.entityName = entityName;
    }

    public abstract BlockPos getSpawnerPosition();

    public abstract World getSpawnerWorld();

    public boolean isActivated() {
        if (entityName == null) return false;
        BlockPos blockpos = this.getSpawnerPosition();
        return this.getSpawnerWorld().isAnyPlayerWithinRangeAt((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D, (double) this.activateRange);
    }

    public void readFromNbt(NBTTagCompound nbt) {
        //Compat for 1.10 worlds
        if (nbt.contains("entity_name")) {
            String n = nbt.getString("entity_name");
            if (n.contains("vampirism.")) {
                entityName = new ResourceLocation(REFERENCE.MODID, n.replace("vampirism.", ""));
            } else {
                entityName = null;
            }
        } else {
            String s = nbt.getString("id");
            entityName = StringUtils.isNullOrEmpty(s) ? null : new ResourceLocation(s);
        }
        minSpawnDelay = nbt.getInt("min_delay");
        maxSpawnDelay = nbt.getInt("max_delay");
        maxNearbyEntities = nbt.getInt("max_nearby");
        spawnDelay = nbt.getInt("delay");
        activateRange = nbt.getInt("activate_range");
        spawnRange = nbt.getInt("spawn_range");
        spawnCount = nbt.getInt("spawn_count");
        spawnedToday = nbt.getInt("spawned_today");
        spawnedLast = nbt.getLong("spawned_last");
        flag = nbt.getBoolean("spawner_flag");
    }

    public void setActivateRange(int activateRange) {
        this.activateRange = activateRange;
    }

    public boolean setDelayToMin(int p_98268_1_) {
        if (p_98268_1_ == 1 && this.getSpawnerWorld().isRemote) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if any more creatures of the given type are allowed in the world before spawning
     */
    public void setLimitTotalEntities(EnumCreatureType creatureType) {
        limitType = creatureType;
    }

    public void setMaxNearbyEntities(int maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
    }

    public void setMaxSpawnDelay(int maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
    }

    public void setMinSpawnDelay(int minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    public void setSpawnRange(int spawnRange) {
        this.spawnRange = spawnRange;
    }

    public void updateSpawner() {
        if (isActivated()) {
            BlockPos blockpos = this.getSpawnerPosition();
            if (!getSpawnerWorld().isRemote) {
                if (this.spawnDelay == -1) {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    return;
                }

                if ((getSpawnerWorld().getGameTime()) % 24000 < this.spawnedLast) {
                    this.spawnedToday = 0;
                    this.flag = true;
                }
                if (!this.flag)
                    return;

                boolean flag1 = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    Entity entity = EntityType.create(this.getSpawnerWorld(), this.getEntityName());

                    if (entity == null) {
                        break;
                    }

                    int j = this.getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(), getSpawningBox()).size();

                    if (j >= this.maxNearbyEntities) {
                        this.resetTimer();
                        break;
                    }

                    if (limitType != null) {
                        int total = this.getSpawnerWorld().countEntities(limitType, true);
                        total = total * UtilLib.countPlayerLoadedChunks(this.getSpawnerWorld()) / MOB_COUNT_DIV;
                        if (total > limitType.getMaxNumberOfCreature()) {
                            this.resetTimer();
                            break;
                        }
                    }

                    if (UtilLib.spawnEntityInWorld(getSpawnerWorld(), getSpawningBox(), entity, 1, Collections.emptyList())) {
                        onSpawned(entity);
                        flag1 = true;
                    }
                }
                if (flag1) {
                    this.resetTimer();
                    this.spawnedToday++;
                    this.spawnedLast = getSpawnerWorld().getGameTime() % 24000;
                }
            }
        }
    }

    public void writeToNbt(NBTTagCompound nbt) {
        if (entityName != null) nbt.putString("id", entityName.toString());
        nbt.putInt("min_delay", minSpawnDelay);
        nbt.putInt("max_delay", maxSpawnDelay);
        nbt.putInt("max_nearby", maxNearbyEntities);
        nbt.putInt("delay", spawnDelay);
        nbt.putInt("activate_range", activateRange);
        nbt.putInt("spawn_range", spawnRange);
        nbt.putInt("spawn_count", spawnCount);
        nbt.putInt("spawned_today", spawnedToday);
        nbt.putLong("spawned_last", spawnedLast);
        nbt.putBoolean("spawner_flag", flag);
    }

    protected AxisAlignedBB getSpawningBox() {
        BlockPos blockpos = getSpawnerPosition();
        return (new AxisAlignedBB((double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), (double) (blockpos.getX() + 1), (double) (blockpos.getY() + 1), (double) (blockpos.getZ() + 1))).grow((double) this.spawnRange, (double) this.spawnRange, (double) this.spawnRange);

    }

    protected abstract void onReset();

    protected void onSpawned(Entity e) {
        if (e instanceof EntityLiving) {
            ((EntityLiving) e).spawnExplosionParticle();
        }
    }

    private void resetTimer() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + this.getSpawnerWorld().rand.nextInt(i);
        }
        onReset();
    }

    public int getSpawnedToday() {
        return spawnedToday;
    }

    public void setSpawn(boolean spawn) {
        this.flag = spawn;
    }
}