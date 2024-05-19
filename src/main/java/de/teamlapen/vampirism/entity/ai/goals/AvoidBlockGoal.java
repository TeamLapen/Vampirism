package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.core.ModVillage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;

public class AvoidBlockGoal extends Goal {


    private final PathfinderMob mob;
    private final Holder<PoiType> avoid;
    private final int minDistance;
    private Vec3 toAvoid;
    @Nullable
    protected Path path;
    protected final PathNavigation pathNav;
    private final double walkSpeedModifier;

    public AvoidBlockGoal(PathfinderMob mob, Holder<PoiType> avoid, int minDistance, double pWalkSpeedModifier) {
        this.mob = mob;
        this.avoid = avoid;
        this.minDistance = minDistance;
        this.pathNav = mob.getNavigation();
        this.walkSpeedModifier = pWalkSpeedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.mob.level() instanceof ServerLevel serverLevel) {
            Vec3 position = this.mob.position();
            this.toAvoid = serverLevel.getPoiManager().getInRange(l -> l.is(this.avoid), this.mob.blockPosition(), this.minDistance, PoiManager.Occupancy.ANY).map(PoiRecord::getPos).map(BlockPos::getCenter).min(Comparator.comparingInt(o -> (int) position.distanceTo(o))).orElse(null);
            if (toAvoid == null){
                return false;
            } else {
                Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid);
                if (vec3 == null) {
                    return false;
                } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(position)) {
                    return false;
                } else {
                    this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                    return this.path != null;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.pathNav.isDone();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void start() {
        this.pathNav.moveTo(this.path, this.walkSpeedModifier);
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void stop() {
        this.toAvoid = null;
    }
}
