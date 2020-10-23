package de.teamlapen.vampirism.entity.goals.villagertasks;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Objects;
import java.util.Optional;

/**
 * {@link net.minecraft.entity.ai.brain.task.SleepAtHomeTask}
 * <p>
 * TODO 1.14 village still not sleeping at day
 * TODO 1.14 village for converted villager & vampirefaction villager
 * <p>
 * {@link de.teamlapen.vampirism.entity.goals.MoveIndoorsDayGoal}
 */
public class SleepAtHomeNightTask extends Task<LivingEntity> {
    private long field_220552_a;

    public SleepAtHomeNightTask() {
        super(ImmutableMap.of(MemoryModuleType.HOME, MemoryModuleStatus.VALUE_PRESENT));
    }

    protected boolean isTimedOut(long gameTime) {
        return false;
    }

    protected void resetTask(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        if (entityIn.isSleeping()) {
            entityIn.wakeUp();
            this.field_220552_a = gameTimeIn + 40L;
        }

    }

    protected boolean shouldContinueExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        Optional<GlobalPos> optional = entityIn.getBrain().getMemory(MemoryModuleType.HOME);
        if (!optional.isPresent()) {
            return false;
        } else {
            BlockPos blockpos = optional.get().getPos();
            return entityIn.getBrain().hasActivity(Activity.REST) && entityIn.getPosY() > (double) blockpos.getY() + 0.4D && blockpos.withinDistance(entityIn.getPositionVec(), 1.14D);
        }
    }

    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner) {
        if (owner.isPassenger()) {
            return false;
        } else {
            GlobalPos globalpos = owner.getBrain().getMemory(MemoryModuleType.HOME).get();
            if (!Objects.equals(worldIn.getDimensionKey(), globalpos.getDimension())) {
                return false;
            } else {
                BlockState blockstate = worldIn.getBlockState(globalpos.getPos());
                return globalpos.getPos().withinDistance(owner.getPositionVec(), 2.0D) && blockstate.getBlock().isIn(BlockTags.BEDS) && !blockstate.get(BedBlock.OCCUPIED);
            }
        }
    }

    protected void startExecuting(ServerWorld worldIn, LivingEntity entityIn, long gameTimeIn) {
        if (gameTimeIn > this.field_220552_a) {
            entityIn.startSleeping(entityIn.getBrain().getMemory(MemoryModuleType.HOME).get().getPos());
        }

    }
}