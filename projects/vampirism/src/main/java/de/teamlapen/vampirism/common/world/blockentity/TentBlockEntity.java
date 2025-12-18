package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.vampirism.api.VEnums;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.server.SimpleSpawnerLogic;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.common.world.entity.hunter.BasicHunterEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

/**
 * spawns hunters for tents
 */
public class TentBlockEntity extends BlockEntity {


    private final @NotNull SimpleSpawnerLogic<BasicHunterEntity> spawnerLogicHunter;
    private final @NotNull SimpleSpawnerLogic<AdvancedHunterEntity> spawnerLogicAdvancedHunter;
    private boolean spawn = false;
    private boolean advanced = false;

    public TentBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModBlockEntities.TENT.get(), pos, state);
        this.spawnerLogicHunter = new SimpleSpawnerLogic<>(ModEntities.HUNTER.get()).setActivateRange(64).setSpawnRange(6).setMinSpawnDelay(600).setMaxSpawnDelay(1000).setMaxNearbyEntities(2).setDailyLimit(ModConfig.BALANCE.hunterTentMaxSpawn.get()).setLimitTotalEntities(VEnums.HUNTER_CATEGORY.getValue()).setOnSpawned(hunter -> hunter.makeCampHunter(this.worldPosition));
        this.spawnerLogicAdvancedHunter = new SimpleSpawnerLogic<>(ModEntities.ADVANCED_HUNTER.get()).setActivateRange(64).setSpawnRange(6).setMinSpawnDelay(1200).setMaxSpawnDelay(2000).setMaxNearbyEntities(1).setDailyLimit(1).setLimitTotalEntities(VEnums.HUNTER_CATEGORY.getValue()).setOnSpawned(hunter -> hunter.makeCampHunter(this.worldPosition));
    }

    public boolean isSpawner() {
        return spawn;
    }

    @Override
    public void loadAdditional(@NotNull ValueInput input) {
        super.loadAdditional(input);
        this.spawnerLogicHunter.deserialize(input.childOrEmpty("spawner_logic_1"));
        this.spawnerLogicAdvancedHunter.deserialize(input.childOrEmpty("spawner_logic_2"));
        this.advanced = input.getBooleanOr("advanced", false);
        this.spawn = input.getBooleanOr("spawn", false);
    }

    @Override
    public void saveAdditional(@NotNull ValueOutput output) {
        super.saveAdditional(output);
        this.spawnerLogicHunter.serialize(output.child("spawn_logic_1"));
        this.spawnerLogicAdvancedHunter.serialize(output.child("spawn_logic_2"));
        output.putBoolean("spawn", this.spawn);
        output.putBoolean("advanced", this.advanced);
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }


    public void setSpawn(boolean spawn) {
        this.spawn = spawn;
    }

    public static void serverTick(@NotNull Level level, @NotNull BlockPos pos, BlockState state, @NotNull TentBlockEntity blockEntity) {
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
