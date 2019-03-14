package de.teamlapen.vampirism.entity.ai;

import com.google.common.base.Predicates;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IVillageCaptureEntity;
import de.teamlapen.vampirism.entity.EntityVampirism;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;

import java.util.List;


public class EntityAIAttackVillage<T extends EntityVampirism & IVillageCaptureEntity> extends EntityAITarget {

    private final T attacker;
    private final EntityAINearestAttackableTarget.Sorter sorter;
    private EntityLivingBase targetEntity;

    public EntityAIAttackVillage(T creature) {
        super(creature, false, false);
        this.attacker = creature;
        this.sorter = new EntityAINearestAttackableTarget.Sorter(creature);

    }

    @Override
    public boolean shouldExecute() {
        if (!attacker.isAttackingVillage()) {
            return false;
        }
        List<EntityLivingBase> list = this.attacker.world.getEntitiesWithinAABB(EntityLivingBase.class, attacker.getTargetVillageArea(), Predicates.and(VampirismAPI.factionRegistry().getPredicate(attacker.getFaction(), false), (entity -> {
            return isSuitableTarget(entity, false);
        })));
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
