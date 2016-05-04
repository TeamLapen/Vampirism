package de.teamlapen.vampirism.entity.minions.ai;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;

public class MinionAIHurtByTarget extends EntityAIHurtByTarget {
    final IMinion minion;

    public MinionAIHurtByTarget(IMinion minion, boolean p_i1660_2_) {
        super(MinionHelper.entity(minion), p_i1660_2_);
        this.minion = minion;
    }

    @Override
    protected boolean isSuitableTarget(EntityLivingBase target, boolean p_75296_2_) {
        if (MinionHelper.isLordSafe(minion, target)) return false;
        return super.isSuitableTarget(target, p_75296_2_);
    }
}