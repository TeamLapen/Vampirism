package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.world.villages.VampirismVillage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.village.Village;

/**
 * Targets vampires if the golem as a non vampire village assigned
 */

public class GolemAITargetVampire extends EntityAINearestAttackableTarget<EntityLivingBase> {
    private final EntityIronGolem golem;

    public GolemAITargetVampire(EntityIronGolem creature) {
        super(creature, EntityLivingBase.class, 4, false, false, VampirismAPI.factionRegistry().getPredicate(VReference.HUNTER_FACTION, true, true, false, false, VReference.VAMPIRE_FACTION));
        this.golem = creature;
    }

    @Override
    public boolean shouldExecute() {
        Village v = golem.getVillage();
        if (v == null) return false;
        VampirismVillage vv = VampirismVillage.get(v);
        if (vv == null || VReference.VAMPIRE_FACTION.equals(vv.getControllingFaction())) {
            return false;
        }
        return super.shouldExecute();
    }
}
