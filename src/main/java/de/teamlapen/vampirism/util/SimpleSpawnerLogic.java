package de.teamlapen.vampirism.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Simple mob spawning logic. More configurable than {@link MobSpawnerBaseLogic} but less functional.
 */
public abstract class SimpleSpawnerLogic {

    private String entityName = "Pig";
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int activateRange = 16;

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }

    private int spawnCount = 1;


    public void setMaxNearbyEntities(int maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
    }

    private int maxNearbyEntities = 4;


    public void setSpawnRange(int spawnRange) {
        this.spawnRange = spawnRange;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public void setMinSpawnDelay(int minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
    }


    public void setMaxSpawnDelay(int maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
    }


    public void setActivateRange(int activateRange) {
        this.activateRange = activateRange;
    }

    private int spawnRange = 4;
    private int spawnDelay = 20;

    public boolean isActivated() {
        return this.getSpawnerWorld().getClosestPlayer((double) this.getSpawnerX() + 0.5D, (double) this.getSpawnerY() + 0.5D, (double) this.getSpawnerZ() + 0.5D, (double) this.activateRange) != null;
    }

    public void updateSpawner() {
        if (isActivated()) {
            if (!getSpawnerWorld().isRemote) {
                if (this.spawnDelay == -1) {
                    this.resetTimer();
                }

                if (this.spawnDelay > 0) {
                    --this.spawnDelay;
                    return;
                }

                boolean flag = false;

                for (int i = 0; i < this.spawnCount; ++i) {
                    Entity entity = EntityList.createEntityByName(this.getEntityName(), this.getSpawnerWorld());

                    if (entity == null) {
                        break;
                    }

                    int j = this.getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(), getSpawningBox()).size();

                    if (j >= this.maxNearbyEntities) {
                        this.resetTimer();
                        break;
                    }

                    if (Helper.spawnEntityInWorld(getSpawnerWorld(), getSpawningBox(), entity, 1)) {
                        onSpawned(entity);
                        flag = true;
                    }
                }
                if (flag) {
                    this.resetTimer();
                }
            }
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

    protected void onSpawned(Entity e) {
        if (e instanceof EntityLiving) {
            ((EntityLiving) e).spawnExplosionParticle();
        }
    }

    protected abstract void onReset();

    protected AxisAlignedBB getSpawningBox() {
        return AxisAlignedBB.getBoundingBox((double) this.getSpawnerX(), (double) this.getSpawnerY(), (double) this.getSpawnerZ(), (double) (this.getSpawnerX() + 1), (double) (this.getSpawnerY() + 1), (double) (this.getSpawnerZ() + 1)).expand((double) (this.spawnRange * 2), 4.0D, (double) (this.spawnRange * 2));
    }

    public abstract World getSpawnerWorld();

    public abstract int getSpawnerX();

    public abstract int getSpawnerY();

    public abstract int getSpawnerZ();

    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setString("entity_name", entityName);
        nbt.setInteger("min_delay", minSpawnDelay);
        nbt.setInteger("max_delay", maxSpawnDelay);
        nbt.setInteger("max_nearby", maxNearbyEntities);
        nbt.setInteger("delay", spawnDelay);
        nbt.setInteger("activate_range", activateRange);
        nbt.setInteger("spawn_range", spawnRange);
        nbt.setInteger("spawn_count", spawnCount);
    }

    public void readFromNbt(NBTTagCompound nbt) {
        entityName = nbt.getString("entity_name");
        minSpawnDelay = nbt.getInteger("min_delay");
        maxSpawnDelay = nbt.getInteger("max_delay");
        maxNearbyEntities = nbt.getInteger("max_nearby");
        spawnDelay = nbt.getInteger("delay");
        activateRange = nbt.getInteger("activate_range");
        spawnRange = nbt.getInteger("spawn_range");
        spawnCount = nbt.getInteger("spawn_count");
    }

    public boolean setDelayToMin(int p_98268_1_) {
        if (p_98268_1_ == 1 && this.getSpawnerWorld().isRemote) {
            this.spawnDelay = this.minSpawnDelay;
            return true;
        } else {
            return false;
        }
    }
}
