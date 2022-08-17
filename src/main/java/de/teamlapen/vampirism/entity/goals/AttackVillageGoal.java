package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class AttackVillageGoal<T extends VampirismEntity & IVillageCaptureEntity> extends TargetGoal {

    private final T attacker;
    protected final TargetingConditions entityPredicate;
    private final double distance = getFollowDistance() * 4;

    public AttackVillageGoal(T creature) {
        super(creature, false, false);
        this.attacker = creature;
        this.entityPredicate = new TargetingConditions(true) {
            @Override
            public boolean test(@Nullable LivingEntity attackEntity, @NotNull LivingEntity targetEntity) {
                if (attacker.getCaptureInfo() != null && attacker.getCaptureInfo().shouldForceTargets() && getFollowDistance() > 0) {
                    range(-1.0D);
                } else if (getFollowDistance() < 0) {
                    range(distance);
                }
                return super.test(attackEntity, targetEntity);
            }
        }.selector(VampirismAPI.factionRegistry().getPredicate(attacker.getFaction(), false)).ignoreInvisibilityTesting();
    }

    @Override
    public boolean canUse() {
        if (!attacker.isAttackingVillage()) return false;
        if (attacker.getTargetVillageArea() == null) return false;
        this.targetMob = this.attacker.level.getNearestEntity(LivingEntity.class, entityPredicate, this.mob, this.mob.getX(), this.mob.getY() + (double) this.mob.getEyeHeight(), this.mob.getZ(), attacker.getTargetVillageArea());
        return targetMob != null;
    }

    @Override
    public void start() {
        this.attacker.setTarget(this.targetMob);
        super.start();
    }
}
