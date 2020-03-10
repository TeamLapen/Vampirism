package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class AttackVillageGoal<T extends VampirismEntity & IVillageCaptureEntity> extends TargetGoal {
    private static final AxisAlignedBB NULLAXIS = new AxisAlignedBB(0,0,0,0,0,0);

    private final T attacker;
    private EntityPredicate entityPredicate;
    private double distance = getTargetDistance() * 4;

    public AttackVillageGoal(T creature) {
        super(creature, false, false);
        this.attacker = creature;
        this.entityPredicate = new EntityPredicate() {
            @Override
            public boolean canTarget(@Nullable LivingEntity entity, @Nonnull LivingEntity entity1) {
                if (attacker.getVillageAttributes().map(opt -> opt.map(IVillageAttributes::shouldForceTargets).orElse(false)).orElse(false) && getTargetDistance() > 0) {
                    setDistance(-1.0D);
                } else if (getTargetDistance() < 0) {
                    setDistance(distance);
                }
                return super.canTarget(entity, entity1);
            }
        }.setCustomPredicate(VampirismAPI.factionRegistry().getPredicate(attacker.getFaction(), false));
    }

    @Override
    public boolean shouldExecute() {
        if (!attacker.isAttackingVillage()) return false;
        this.target = this.attacker.world.getClosestEntityWithinAABB(LivingEntity.class, entityPredicate, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ, this.attacker.getVillageAttributes().map(opt -> opt.map(IVillageAttributes::getVillageArea).orElse(NULLAXIS)).orElse(NULLAXIS));
        return target != null;
    }

    @Override
    public void startExecuting() {
        this.attacker.setAttackTarget(this.target);
        super.startExecuting();
    }
}
