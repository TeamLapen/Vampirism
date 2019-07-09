package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.List;


public class AttackVillageGoal<T extends VampirismEntity & IVillageCaptureEntity> extends TargetGoal {

    private final T attacker;
    private final NearestAttackableTargetGoal.Sorter sorter;
    private LivingEntity targetEntity;

    public AttackVillageGoal(T creature) {
        super(creature, false, false);
        this.attacker = creature;
        this.sorter = new NearestAttackableTargetGoal.Sorter(creature);

    }

    @Override
    public boolean shouldExecute() {
        if (!attacker.isAttackingVillage()) {
            return false;
        }
        List<LivingEntity> list = this.attacker.world.getEntitiesWithinAABB(LivingEntity.class, attacker.getTargetVillageArea(), VampirismAPI.factionRegistry().getPredicate(attacker.getFaction(), false).and(entity -> entity instanceof LivingEntity && isSuitableTarget((LivingEntity) entity, false)));
        if (list.isEmpty()) {
            return false;
        } else {
            list.sort(this.sorter);
            this.targetEntity = list.get(0);
            return true;
        }
    }

    @Override
    public void startExecuting() {
        this.attacker.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }

    @Override
    protected double getTargetDistance() {
        return super.getTargetDistance() * 4;
    }
}
