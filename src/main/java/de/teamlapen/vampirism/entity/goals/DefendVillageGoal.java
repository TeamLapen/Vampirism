package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.api.world.IVillageAttributes;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class DefendVillageGoal<T extends CreatureEntity & IVillageCaptureEntity> extends TargetGoal {
    private static final AxisAlignedBB NULLAXIS = new AxisAlignedBB(0,0,0,0,0,0);


    private T creature;
    private EntityPredicate entityPredicate;
    private double distance = getTargetDistance() * 4;

    public DefendVillageGoal(T creature) {
        super(creature, false, false);
        this.creature = creature;
        this.entityPredicate = new EntityPredicate() {
            @Override
            public boolean canTarget(@Nullable LivingEntity entity, @Nonnull LivingEntity entity1) {
                if (creature.getVillageAttributes().map(opt -> opt.map(IVillageAttributes::shouldForceTargets).orElse(false)).orElse(false) && getTargetDistance() > 0) {
                    setDistance(-1.0D);
                } else if (getTargetDistance() < 0) {
                    setDistance(distance);
                }
                return super.canTarget(entity, entity1);
            }
        }.setCustomPredicate(VampirismAPI.factionRegistry().getPredicate(creature.getFaction(), false));
    }

    @Override
    public boolean shouldExecute() {
        if (!creature.isDefendingVillage()) return false;
        this.target = this.creature.world.getClosestEntityWithinAABB(LivingEntity.class, entityPredicate, this.goalOwner, this.goalOwner.posX, this.goalOwner.posY + (double) this.goalOwner.getEyeHeight(), this.goalOwner.posZ, creature.getVillageAttributes().map(opt -> opt.map(IVillageAttributes::getVillageArea).orElse(NULLAXIS)).orElse(NULLAXIS));
        return target != null;
    }

    @Override
    public void startExecuting() {
        this.creature.setAttackTarget(this.target);
        super.startExecuting();
    }
}
