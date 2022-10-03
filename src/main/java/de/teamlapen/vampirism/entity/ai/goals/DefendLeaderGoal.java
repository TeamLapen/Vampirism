package de.teamlapen.vampirism.entity.ai.goals;

import de.teamlapen.vampirism.api.entity.IEntityLeader;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DefendLeaderGoal extends TargetGoal {
    private final @NotNull BasicVampireEntity entity;
    private @Nullable LivingEntity attacker;
    private int timestamp;

    public DefendLeaderGoal(@NotNull BasicVampireEntity basicVampire) {
        super(basicVampire, false);
        this.entity = basicVampire;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    public boolean canUse() {
        IEntityLeader leader = this.entity.getAdvancedLeader();
        if (leader == null) {
            return false;
        } else {
            this.attacker = leader.getRepresentingEntity().getLastHurtByMob();
            int i = leader.getRepresentingEntity().getLastHurtByMobTimestamp();
            return i != this.timestamp && this.canAttack(this.attacker, TargetingConditions.DEFAULT);
        }

    }

    public void start() {
        this.mob.setTarget(this.attacker);
        IEntityLeader leader = this.entity.getAdvancedLeader();
        if (leader != null) {
            this.timestamp = leader.getRepresentingEntity().getLastHurtByMobTimestamp();
        }

        super.start();
    }
}