package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.world.IVampirismVillage;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

import javax.annotation.Nullable;

/**
 * Similar to vanilla EntityAIDefendVillage, but for hunter
 */
public class HunterAIDefendVillage extends EntityAITarget {

    private final IVillageHunterCreature hunter;
    private EntityLivingBase targetEntity;

    /**
     * @param hunter The task owner
     */
    public HunterAIDefendVillage(IVillageHunterCreature hunter) {
        super(hunter.getRepresentingCreature(), false, false);
        this.hunter = hunter;

        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        IVampirismVillage village = hunter.getVampirismVillage();
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

    public interface IVillageHunterCreature extends IHunter {
        EntityCreature getRepresentingCreature();

        @Nullable
        IVampirismVillage getVampirismVillage();
    }
}
