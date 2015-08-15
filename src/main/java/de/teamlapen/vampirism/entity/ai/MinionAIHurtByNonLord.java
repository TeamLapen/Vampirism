package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.entity.minions.EntityVampireMinion;
import de.teamlapen.vampirism.entity.minions.IMinion;
import de.teamlapen.vampirism.entity.minions.MinionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;

public class MinionAIHurtByNonLord extends EntityAIHurtByTarget {
    IMinion minion;

    public MinionAIHurtByNonLord(EntityVampireMinion minion, boolean p_i1660_2_) {
        super(minion, p_i1660_2_);
        this.minion = minion;
    }

    @Override
    protected boolean isSuitableTarget(EntityLivingBase target, boolean p_75296_2_) {
        if (MinionHelper.isLordSafe(minion, target)) return false;
        return super.isSuitableTarget(target, p_75296_2_);
    }
}
