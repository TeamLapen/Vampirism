package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.util.SimpleSpawnerLogic;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
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
        super(ModTiles.TENT.get());
        this.spawnerLogicHunter = new SimpleSpawnerLogic<>(ModEntities.HUNTER.get()).setActivateRange(64).setSpawnRange(6).setMinSpawnDelay(600).setMaxSpawnDelay(1000).setMaxNearbyEntities(2).setDailyLimit(VampirismConfig.BALANCE.hunterTentMaxSpawn.get()).setLimitTotalEntities(VReference.HUNTER_CREATURE_TYPE).setOnSpawned(hunter -> hunter.makeCampHunter(this.worldPosition));
        this.spawnerLogicAdvancedHunter = new SimpleSpawnerLogic<>(ModEntities.ADVANCED_HUNTER.get()).setActivateRange(64).setSpawnRange(6).setMinSpawnDelay(1200).setMaxSpawnDelay(2000).setMaxNearbyEntities(1).setDailyLimit(1).setLimitTotalEntities(VReference.HUNTER_CREATURE_TYPE).setOnSpawned(hunter -> hunter.makeCampHunter(this.worldPosition));
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(1, 0, 1);
    }

    public boolean isSpawner() {
        return spawn;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("spawner_logic_1")) {
            spawnerLogicHunter.readFromNbt(nbt.getCompound("spawner_logic_1"));
        }
        if (nbt.contains("spawner_logic_2")) {
            spawnerLogicAdvancedHunter.readFromNbt(nbt.getCompound("spawner_logic_2"));
        }
        if (nbt.contains("advanced")) {
            advanced = nbt.getBoolean("advanced");
        }
        spawn = nbt.getBoolean("spawn");
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT compound) {
        CompoundNBT nbt = super.save(compound);
        CompoundNBT logic1 = new CompoundNBT();
        CompoundNBT logic2 = new CompoundNBT();
        this.spawnerLogicHunter.writeToNbt(logic1);
        this.spawnerLogicAdvancedHunter.writeToNbt(logic2);
        nbt.put("spawner_logic_1", logic1);
        nbt.put("spawner_logic_2", logic2);
        nbt.putBoolean("spawn", this.spawn);
        nbt.putBoolean("advanced", this.advanced);
        return nbt;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    @Override
    public void setLevelAndPosition(World worldIn, BlockPos pos) {
        super.setLevelAndPosition(worldIn, pos);
        this.spawnerLogicHunter.setWorld(worldIn);
        this.spawnerLogicAdvancedHunter.setWorld(worldIn);
        this.spawnerLogicHunter.setBlockPos(this.worldPosition); //Internal position should be set here using the immutable version of the given block pos
        this.spawnerLogicAdvancedHunter.setBlockPos(this.worldPosition);
    }

    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    @Override
    public void setPosition(BlockPos posIn) {
        super.setPosition(posIn);
        this.spawnerLogicHunter.setBlockPos(this.worldPosition); //Internal position should be set here using the immutable version of the given block pos
        this.spawnerLogicAdvancedHunter.setBlockPos(this.worldPosition);
    }

    @Override
    public void tick() {
        if (level == null) return;

        if (spawn) {
            if (!this.level.isClientSide && this.level.getGameTime() % 64 == 0) {
                if (UtilLib.isInsideStructure(this.level, this.worldPosition, Structure.VILLAGE)) {
                    this.spawn = false; //Disable spawning inside villages
                }
            }
            this.spawnerLogicHunter.updateSpawner();
            if (advanced) {
                this.spawnerLogicAdvancedHunter.updateSpawner();
            }
        }
    }

    public boolean triggerEvent(int id, int type) {
        return (this.spawnerLogicHunter.setDelayToMin(id) || this.spawnerLogicAdvancedHunter.setDelayToMin(id)) || super.triggerEvent(id, type);
    }
}
