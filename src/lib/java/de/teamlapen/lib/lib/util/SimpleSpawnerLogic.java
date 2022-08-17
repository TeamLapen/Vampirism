package de.teamlapen.lib.lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Simple mob spawning logic. More configurable than {@link net.minecraft.world.level.BaseSpawner} but less functional.
 */
public class SimpleSpawnerLogic<T extends Entity> {

    private final static Logger LOGGER = LogManager.getLogger();
    private static final int MOB_COUNT_DIV = (int) Math.pow(17.0D, 2.0D);

    @NotNull
    private final EntityType<T> entityType;
    @Nullable
    private Consumer<T> onSpawned;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int activateRange = 16;
    private int dailyLimit = Integer.MAX_VALUE;
    private int spawnCount = 1;
    private int maxNearbyEntities = 4;
    private int spawnRange = 4;
    private int spawnDelay = 20;
    private int spawnedToday = 0;
    private long spawnedLast = 0L;
    private boolean flag = true;
    private MobCategory limitType;
    private final Random rng = new Random();

    public SimpleSpawnerLogic(@NotNull EntityType<T> entityTypeIn) {
        this.entityType = entityTypeIn;
    }

    @NotNull
    public EntityType<T> getEntityType() {
        return entityType;
    }

    public int getSpawnedToday() {
        return spawnedToday;
    }

    public boolean isActivated(Level level, BlockPos pos) {
        if (level == null) return false;
        if (pos == null) return false;
        return level.hasNearbyAlivePlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, this.activateRange);
    }

    public void readFromNbt(CompoundTag nbt) {
        this.spawnDelay = nbt.getInt("delay");
        this.spawnedToday = nbt.getInt("spawned_today");
        this.spawnedLast = nbt.getLong("spawned_last");
        this.flag = nbt.getBoolean("spawner_flag");
    }

    public SimpleSpawnerLogic<T> setActivateRange(int activateRange) {
        this.activateRange = activateRange;
        return this;
    }

    public SimpleSpawnerLogic<T> setDailyLimit(int dailyLimit) {
        this.dailyLimit = dailyLimit;
        return this;
    }

    public boolean setDelayToMin(int id, Level level) {
        if (id == 1 && (level.isClientSide)) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if any more creatures of the given type are allowed in the world before spawning
     */
    public SimpleSpawnerLogic<T> setLimitTotalEntities(MobCategory creatureType) {
        limitType = creatureType;
        return this;
    }

    public SimpleSpawnerLogic<T> setMaxNearbyEntities(int maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
        return this;
    }

    public SimpleSpawnerLogic<T> setMaxSpawnDelay(int maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
        return this;
    }

    public SimpleSpawnerLogic<T> setMinSpawnDelay(int minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
        return this;
    }

    public SimpleSpawnerLogic<T> setOnSpawned(Consumer<T> onSpawned) {
        this.onSpawned = onSpawned;
        return this;
    }

    public SimpleSpawnerLogic<T> setSpawn(boolean spawn) {
        this.flag = spawn;
        return this;
    }

    public SimpleSpawnerLogic<T> setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
        return this;
    }

    public SimpleSpawnerLogic<T> setSpawnRange(int spawnRange) {
        this.spawnRange = spawnRange;
        return this;
    }

    public void serverTick(Level level, BlockPos pos) {
        if (isActivated(level, pos)) {
            if (level instanceof ServerLevel) {
                if (this.spawnDelay == -1) {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    return;
                }


                if ((level.getGameTime()) > this.spawnedLast + 24000) {
                    this.spawnedToday = 0;
                    this.flag = true;
                } else if (this.spawnedToday >= dailyLimit) {
                    this.flag = false;
                }
                if (!this.flag)
                    return;

                boolean flag1 = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    T entity = this.getEntityType().create(level);

                    if (entity == null) {
                        break;
                    }

                    int j = level.getEntitiesOfClass(entity.getClass(), getSpawningBox(pos).inflate(5)).size();

                    if (j >= this.maxNearbyEntities) {
                        this.resetTimer();
                        break;
                    }

                    if (limitType != null) {
                        @Nullable
                        NaturalSpawner.SpawnState densityManager = ((ServerLevel) level).getChunkSource().getLastSpawnState();
                        try {
                            if (densityManager != null && !densityManager.canSpawnForCategory(limitType, new ChunkPos(pos.getX() / 16, pos.getZ() / 16))) {
                                this.resetTimer();
                                break;
                            }
                        } catch (NoSuchMethodError e) {
                            //Workaround for https://github.com/TeamLapen/Vampirism/issues/983
                            //Maybe remove when https://github.com/magmafoundation/Magma-1.16.x/issues/154 is solved
                        }

                    }

                    if (UtilLib.spawnEntityInWorld((ServerLevel) level, getSpawningBox(pos), entity, 1, Collections.emptyList(), MobSpawnType.SPAWNER)) {
                        onSpawned(entity);
                        flag1 = true;
                    }
                }
                if (flag1) {
                    this.resetTimer();
                    this.spawnedToday++;
                    this.spawnedLast = level.getGameTime();
                }
            }
        }
    }

    public void writeToNbt(CompoundTag nbt) {
        nbt.putInt("delay", spawnDelay);
        nbt.putInt("spawned_today", spawnedToday);
        nbt.putLong("spawned_last", spawnedLast);
        nbt.putBoolean("spawner_flag", flag);
    }

    protected AABB getSpawningBox(BlockPos pos) {
        if (pos == null) return AABB.ofSize(Vec3.ZERO, 0, 0, 0);
        return (new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)).inflate(this.spawnRange, this.spawnRange, this.spawnRange);

    }

    protected void onSpawned(T e) {
        if (e instanceof Mob mob) {
            mob.spawnAnim();
        }
        if (this.onSpawned != null) {
            this.onSpawned.accept(e);
        }
    }

    private void resetTimer() {
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            int i = this.maxSpawnDelay - this.minSpawnDelay;
            this.spawnDelay = this.minSpawnDelay + (rng.nextInt(i));
        }
    }
}