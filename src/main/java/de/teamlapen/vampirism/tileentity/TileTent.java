package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.SimpleSpawnerLogic;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Tile entity which spawns hunters for tents
 */
public class TileTent extends TileEntity implements ITickable {
    private SimpleSpawnerLogic spawnerLogic = new SimpleSpawnerLogic() {
        @Override
        public BlockPos getSpawnerPosition() {
            return TileTent.this.getPos();
        }

        @Override
        public World getSpawnerWorld() {
            return TileTent.this.worldObj;
        }

        @Override
        protected void onReset() {
            //TileTent.this.worldObj.addBlockEvent(getSpawnerPosition(), ModBlocks.tentMain, 1, 0);
        }

        @Override
        protected void onSpawned(Entity e) {
            super.onSpawned(e);
            if (e instanceof EntityBasicHunter) {
                ((EntityBasicHunter) e).setCampArea(getSpawningBox());
            }
        }
    };
    private boolean spawn = false;

    public TileTent() {
        spawnerLogic.setEntityName(ModEntities.BASIC_HUNTER_NAME);
        spawnerLogic.setActivateRange(64);
        spawnerLogic.setSpawnRange(6);
        spawnerLogic.setMinSpawnDelay(400);
        spawnerLogic.setMaxSpawnDelay(800);
        spawnerLogic.setMaxNearbyEntities(2);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new SPacketUpdateTileEntity(this.getPos(), 1, nbttagcompound);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().expand(1, 0, 1);
    }

    @Override
    public boolean hasFastRenderer() {
        return super.hasFastRenderer();
    }

    public boolean isSpawner() {
        return spawn;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        spawnerLogic.readFromNbt(nbt);
        spawn = nbt.getBoolean("spawn");
    }

    public boolean receiveClientEvent(int id, int type) {
        return this.spawnerLogic.setDelayToMin(id) || super.receiveClientEvent(id, type);
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    @Override
    public void update() {
        if (spawn) {
            spawnerLogic.updateSpawner();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        spawnerLogic.writeToNbt(nbt);
        nbt.setBoolean("spawn", spawn);
    }
}
