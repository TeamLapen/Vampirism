package de.teamlapen.vampirism.entity.goals;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.IronGolemEntity;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */

public class GolemTargetVampireGoal extends NearestAttackableTargetGoal<LivingEntity> {
    private final IronGolemEntity golem;

    public GolemTargetVampireGoal(IronGolemEntity creature) {
        super(creature, LivingEntity.class, 4, false, false, VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, false, false, VReference.VAMPIRE_FACTION));
        this.golem = creature;
    }

    @Override
    public boolean shouldExecute() {//TODO 1.14 village
//        Village v = golem.getVillage();
//        if (v == null) return false;
//        VampirismVillage vv = VampirismVillage.get(v);
//        if (vv == null || VReference.VAMPIRE_FACTION.equals(vv.getControllingFaction())) {
//            return false;
//        }
        return super.shouldExecute();
    }
}