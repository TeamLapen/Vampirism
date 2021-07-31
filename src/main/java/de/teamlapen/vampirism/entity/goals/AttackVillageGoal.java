package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class AttackVillageGoal<T extends VampirismEntity & IVillageCaptureEntity> extends TargetGoal {

    private final T attacker;
    protected final EntityPredicate entityPredicate;
    private final double distance = getFollowDistance() * 4;

    public AttackVillageGoal(T creature) {
        super(creature, false, false);
        this.attacker = creature;
        this.entityPredicate = new EntityPredicate() {
            @Override
            public boolean test(@Nullable LivingEntity attackEntity, @Nonnull LivingEntity targetEntity) {
                if (attacker.getCaptureInfo() != null && attacker.getCaptureInfo().shouldForceTargets() && getFollowDistance() > 0) {
                    range(-1.0D);
                } else if (getFollowDistance() < 0) {
                    range(distance);
                }
                return super.test(attackEntity, targetEntity);
            }
        }.selector(VampirismAPI.factionRegistry().getPredicate(attacker.getFaction(), false)).allowUnseeable();
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
