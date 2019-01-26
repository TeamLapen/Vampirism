package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.world.IVampirismVillageOLD;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import javax.annotation.Nullable;

/**
 * Similar to vanilla EntityAIDefendVillage, but for hunter
 */
public class HunterAIDefendVillage<T extends EntityCreature & HunterAIDefendVillage.IVillageHunterCreature> extends EntityAITarget {

    private final T hunter;
    private EntityLivingBase targetEntity;

    /**
     * @param hunter The task owner
     */
    public HunterAIDefendVillage(T hunter) {
        super(hunter.getRepresentingCreature(), false, false);
        this.hunter = hunter;

        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        IVampirismVillageOLD village = hunter.getVampirismVillage();
        if (village == null) return false;

        IVampire targetVampire = village.findNearestVillageAggressor(hunter.getRepresentingCreature());
        if (targetVampire == null) {
            this.targetEntity = null;
        } else {
            this.targetEntity = targetVampire.getRepresentingEntity();
            return this.isSuitableTarget(targetEntity, false);
        }


        return false;
    }

    @Override
    public void startExecuting() {
        this.hunter.getRepresentingCreature().setAttackTarget(targetEntity);
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.targetEntity = null;
    }

    public interface IVillageHunterCreature extends IHunter {
        EntityCreature getRepresentingCreature();

        @Nullable
        IVampirismVillageOLD getVampirismVillage();
    }
}
