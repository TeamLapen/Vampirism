package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.ai.goals.NearestTargetGoalModifier;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin implements NearestTargetGoalModifier {

    @Shadow protected EntityPredicate targetConditions;
    private static final Predicate<LivingEntity> nonVampireCheck = entity -> !Helper.isVampire(entity);
    private static final Predicate<LivingEntity> noFactionEntityCheck = entity -> !(entity instanceof IFactionEntity);

    @Override
    public void ignoreVampires() {
        Predicate<LivingEntity> predicate = nonVampireCheck;
        if (((EntityPredicateAccessor) this.targetConditions).getSelector() != null) {
            predicate = predicate.and(((EntityPredicateAccessor) this.targetConditions).getSelector());
        }
        this.targetConditions.selector(predicate);
    }

    @Override
    public void ignoreFactionEntities() {
        Predicate<LivingEntity> predicate = noFactionEntityCheck;
        if (((EntityPredicateAccessor) this.targetConditions).getSelector() != null) {
            predicate = predicate.and(((EntityPredicateAccessor) this.targetConditions).getSelector());
        }
        this.targetConditions.selector(predicate);
    }
}
