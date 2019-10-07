package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.SimpleSpawnerLogic;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Tile entity which spawns hunters for tents
 */
public class TentTileEntity extends TileEntity implements ITickableTileEntity {


    private SimpleSpawnerLogic spawnerLogic = new SimpleSpawnerLogic() {
        @Override
        public BlockPos getSpawnerPosition() {
            return TentTileEntity.this.getPos();
        }

        @Override
        public World getSpawnerWorld() {
            return TentTileEntity.this.world;
        }

        @Override
        protected void onReset() {
            //TentTileEntity.this.worldObj.addBlockEvent(getSpawnerPosition(), ModBlocks.tentMain, 1, 0);
        }

        @Override
        protected void onSpawned(Entity e) {
            super.onSpawned(e);
            if (e instanceof BasicHunterEntity) {
                ((BasicHunterEntity) e).makeCampHunter(getSpawningBox());
            }
        }
    };
    private boolean spawn = false;

    public TentTileEntity() {
        super(ModTiles.tent);
        spawnerLogic.setEntityType(ModEntities.vampire_hunter);
        spawnerLogic.setActivateRange(64);
        spawnerLogic.setSpawnRange(6);
        spawnerLogic.setMinSpawnDelay(600);
        spawnerLogic.setMaxSpawnDelay(1000);
        spawnerLogic.setMaxNearbyEntities(2);
        spawnerLogic.setLimitTotalEntities(VReference.HUNTER_CREATURE_TYPE);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().grow(1, 0, 1);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return null;//new SPacketUpdateTileEntity(this.getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public boolean hasFastRenderer() {
        return super.hasFastRenderer();
    }

    public boolean isSpawner() {
        return spawn;
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        if (nbt.contains("spawner_logic")) {
            spawnerLogic.readFromNbt(nbt.getCompound("spawner_logic"));
        }
        spawn = nbt.getBoolean("spawn");
    }

    public boolean receiveClientEvent(int id, int type) {
        return this.spawnerLogic.setDelayToMin(id) || super.receiveClientEvent(id, type);
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    @Override
    public void tick() {
        if (world == null) return;
        if (spawnerLogic.getSpawnedToday() >= VampirismConfig.BALANCE.hunterTentMaxSpawn.get()) {
            spawnerLogic.setSpawn(false);
        }
        if (spawn) {
            spawnerLogic.updateSpawner();
            if (!this.world.isRemote && this.world.getGameTime() % 64 == 0) {
                if (Feature.VILLAGE.isPositionInsideStructure(world, pos)) {
                    this.spawn = false; //Disable spawning inside villages
                }
            }
        }
    }


    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        CompoundNBT logic = new CompoundNBT();
        spawnerLogic.writeToNbt(logic);
        nbt.put("spawner_logic", logic);
        nbt.putBoolean("spawn", spawn);
        return nbt;
    }
}
