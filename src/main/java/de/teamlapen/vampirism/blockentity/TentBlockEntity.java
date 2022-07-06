package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.util.SimpleSpawnerLogic;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.entity.hunter.BasicHunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * spawns hunters for tents
 */
public class TentBlockEntity extends BlockEntity {


    private final SimpleSpawnerLogic<BasicHunterEntity> spawnerLogicHunter;
    private final SimpleSpawnerLogic<AdvancedHunterEntity> spawnerLogicAdvancedHunter;
    private boolean spawn = false;
    private boolean advanced = false;

    public TentBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.TENT.get(), pos, state);
        this.spawnerLogicHunter = new SimpleSpawnerLogic<>(ModEntities.HUNTER.get()).setActivateRange(64).setSpawnRange(6).setMinSpawnDelay(600).setMaxSpawnDelay(1000).setMaxNearbyEntities(2).setDailyLimit(VampirismConfig.BALANCE.hunterTentMaxSpawn.get()).setLimitTotalEntities(VReference.HUNTER_CREATURE_TYPE).setOnSpawned(hunter -> hunter.makeCampHunter(this.worldPosition));
        this.spawnerLogicAdvancedHunter = new SimpleSpawnerLogic<>(ModEntities.ADVANCED_HUNTER.get()).setActivateRange(64).setSpawnRange(6).setMinSpawnDelay(1200).setMaxSpawnDelay(2000).setMaxNearbyEntities(1).setDailyLimit(1).setLimitTotalEntities(VReference.HUNTER_CREATURE_TYPE).setOnSpawned(hunter -> hunter.makeCampHunter(this.worldPosition));
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(1, 0, 1);
    }

    public boolean isSpawner() {
        return spawn;
    }

    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
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

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        CompoundTag logic1 = new CompoundTag();
        CompoundTag logic2 = new CompoundTag();
        this.spawnerLogicHunter.writeToNbt(logic1);
        this.spawnerLogicAdvancedHunter.writeToNbt(logic2);
        compound.put("spawner_logic_1", logic1);
        compound.put("spawner_logic_2", logic2);
        compound.putBoolean("spawn", this.spawn);
        compound.putBoolean("advanced", this.advanced);
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }


    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TentBlockEntity blockEntity) {
        if (blockEntity.spawn) {
            if (level.getGameTime() % 64 == 0) {
                if (UtilLib.isInsideStructure(level, pos, StructureTags.VILLAGE)) {
                    blockEntity.spawn = false; //Disable spawning inside villages
                }
            }
            blockEntity.spawnerLogicHunter.serverTick(level, pos);
            if (blockEntity.advanced) {
                blockEntity.spawnerLogicAdvancedHunter.serverTick(level, pos);
            }
        }
    }

    public boolean triggerEvent(int id, int type) {
        return (this.spawnerLogicHunter.setDelayToMin(id, this.level) || this.spawnerLogicAdvancedHunter.setDelayToMin(id, this.level)) || super.triggerEvent(id, type);
    }
}
