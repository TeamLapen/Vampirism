package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.SimpleSpawnerLogic;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Tile entity which spawns hunters for tents
 */
public class TentTileEntity extends TileEntity implements ITickableTileEntity {


    private final SimpleSpawnerLogic<BasicHunterEntity> spawnerLogicHunter;
    private final SimpleSpawnerLogic<AdvancedHunterEntity> spawnerLogicAdvancedHunter;
    private boolean spawn = false;
    private boolean advanced = false;

    public TentTileEntity() {
        super(ModTiles.tent);
        this.spawnerLogicHunter = new SimpleSpawnerLogic<>(ModEntities.hunter).setActivateRange(64).setSpawnRange(6).setMinSpawnDelay(600).setMaxSpawnDelay(1000).setMaxNearbyEntities(2).setLimitTotalEntities(VReference.HUNTER_CREATURE_TYPE).setOnSpawned(hunter -> hunter.makeCampHunter(this.pos));
        this.spawnerLogicAdvancedHunter = new SimpleSpawnerLogic<>(ModEntities.advanced_hunter).setActivateRange(64).setSpawnRange(6).setMinSpawnDelay(1200).setMaxSpawnDelay(2000).setMaxNearbyEntities(1).setLimitTotalEntities(VReference.HUNTER_CREATURE_TYPE).setOnSpawned(hunter -> hunter.makeCampHunter(this.pos));
    }

    public boolean isSpawner() {
        return spawn;
    }

    public boolean receiveClientEvent(int id, int type) {
        return (this.spawnerLogicHunter.setDelayToMin(id) || this.spawnerLogicAdvancedHunter.setDelayToMin(id)) || super.receiveClientEvent(id, type);
    }

    @Override
    public void tick() {
        if (world == null) return;
        if (this.spawnerLogicHunter.getSpawnedToday() >= VampirismConfig.BALANCE.hunterTentMaxSpawn.get()) {
            this.spawnerLogicHunter.setSpawn(false);
        }
        if (advanced) {
            if (this.spawnerLogicAdvancedHunter.getSpawnedToday() >= VampirismConfig.BALANCE.hunterTentMaxSpawn.get()) {
                this.spawnerLogicAdvancedHunter.setSpawn(false);
            }
        }
        if (spawn) {
            if (!this.world.isRemote && this.world.getGameTime() % 64 == 0) {
                if (Feature.VILLAGE.isPositionInsideStructure(world, pos)) {
                    this.spawn = false; //Disable spawning inside villages
                }
            }
            this.spawnerLogicHunter.updateSpawner();
            if (advanced) {
                this.spawnerLogicAdvancedHunter.updateSpawner();
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        CompoundNBT logic1 = new CompoundNBT();
        CompoundNBT logic2 = new CompoundNBT();
        this.spawnerLogicHunter.writeToNbt(logic1);
        this.spawnerLogicAdvancedHunter.writeToNbt(logic2);
        nbt.put("spawner_logic_1", logic1);
        nbt.put("spawner_logic_2", logic2);
        nbt.putBoolean("spawn", this.spawn);
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        if (nbt.contains("spawner_logic_1")) {
            spawnerLogicHunter.readFromNbt(nbt.getCompound("spawner_logic_1"));
        }
        if (nbt.contains("spawner_logic_2")) {
            spawnerLogicAdvancedHunter.readFromNbt(nbt.getCompound("spawner_logic_2"));
        }
        spawn = nbt.getBoolean("spawn");
    }

    @Override
    public void setWorldAndPos(World worldIn, BlockPos pos) {
        super.setWorldAndPos(worldIn, pos);
        this.spawnerLogicHunter.setWorld(worldIn);
        this.spawnerLogicAdvancedHunter.setWorld(world);
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        this.spawnerLogicHunter.setBlockPos(posIn);
        this.spawnerLogicAdvancedHunter.setBlockPos(posIn);
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().grow(1, 0, 1);
    }
}
