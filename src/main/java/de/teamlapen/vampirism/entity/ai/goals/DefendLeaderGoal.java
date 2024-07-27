package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.entity.IEntityFollower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DefendLeaderGoal<T extends Mob & IEntityFollower> extends TargetGoal {
    private final @NotNull T entity;
    private @Nullable LivingEntity attacker;
    private int timestamp;

    public DefendLeaderGoal(@NotNull T mob) {
        super(mob, false);
        this.entity = mob;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        IEntityLeader leader = this.entity.getLeader();
        if (leader == null) {
            return false;
        } else {
            this.attacker = leader.asEntity().getLastHurtByMob();
            int i = leader.asEntity().getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
        }

    }

    public void start() {
        this.mob.setTarget(this.attacker);
        IEntityLeader leader = this.entity.getLeader();
        if (leader != null) {
            this.timestamp = leader.asEntity().getLastHurtByMobTimestamp();
        }

        super.start();
    }
}