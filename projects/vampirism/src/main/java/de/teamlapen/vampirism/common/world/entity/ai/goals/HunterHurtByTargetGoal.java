package de.teamlapen.vampirism.common.world.entity.ai.goals;

import de.teamlapen.vampirism.api.world.entity.hunter.IHunterMob;
import de.teamlapen.vampirism.common.tags.ModEntityTags;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class HunterHurtByTargetGoal extends HurtByTargetGoal {
    public HunterHurtByTargetGoal(PathfinderMob mob) {
        super(mob, IHunterMob.class);
        setAlertOthers();
    }

    /**
     * Spreads the alert to all hunters, not just those of the same type as the attacked one. So that advanced hunters also get angry when the ordinary one is attacked and vice versa.
     * Doesn't trigger hunter trainers.
     */
    @Override
    protected void alertOthers() {
        double radius = Math.max(getFollowDistance(), 12);
        AABB aabb = AABB.unitCubeFromLowerCorner(mob.position()).inflate(radius, radius, radius);
        List<? extends Mob> list = mob.level().getEntitiesOfClass(Mob.class, aabb, EntitySelector.NO_SPECTATORS);

        for (Mob possibleDefender : list) {
            LivingEntity attacker = mob.getLastHurtByMob();
            if (possibleDefender != mob && possibleDefender.getType().is(ModEntityTags.ALERTABLE_HUNTERS) && attacker != null && possibleDefender.getTarget() == null && !possibleDefender.isAlliedTo(attacker)) {
                this.alertOther(possibleDefender, attacker);
            }
        }
    }
}
