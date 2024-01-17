package de.teamlapen.vampirism.mixin;

import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.entity.ai.goals.NearestTargetGoalModifier;
import de.teamlapen.vampirism.mixin.accessor.TargetConditionAccessor;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin implements NearestTargetGoalModifier {

    @Shadow protected TargetingConditions targetConditions;
    @Shadow @Nullable protected LivingEntity target;
    @Unique
    private static final BiPredicate<Mob,LivingEntity> vampirism$nonVampireCheck = (mob, target) -> !Helper.appearsAsVampire(target, mob);
    @Unique
    private static final Predicate<LivingEntity> vampirism$noFactionEntityCheck = entity -> !(entity instanceof IFactionEntity);

    @Override
    public void ignoreVampires(Mob mob) {
        Predicate<LivingEntity> predicate = l -> vampirism$nonVampireCheck.test(mob, l);
        if (((TargetConditionAccessor) this.targetConditions).getSelector() != null) {
            predicate = predicate.and(((TargetConditionAccessor) this.targetConditions).getSelector());
        }
        this.targetConditions.selector(predicate);
    }

    @Override
    public void ignoreFactionEntities() {
        Predicate<LivingEntity> predicate = vampirism$noFactionEntityCheck;
        if (((TargetConditionAccessor) this.targetConditions).getSelector() != null) {
            predicate = predicate.and(((TargetConditionAccessor) this.targetConditions).getSelector());
        }
        this.targetConditions.selector(predicate);
    }

    @Override
    public void ignoreLineOfSight() {
        this.targetConditions.ignoreLineOfSight();
    }
}
