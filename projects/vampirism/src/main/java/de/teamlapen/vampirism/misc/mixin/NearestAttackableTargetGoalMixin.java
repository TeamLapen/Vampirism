package de.teamlapen.vampirism.misc.mixin;

import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.vampirism.common.util.AndTargetSelector;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.misc.extension.INearestTargetGoal;
import de.teamlapen.vampirism.misc.mixin.accessor.TargetConditionAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.function.BiPredicate;

@Mixin(NearestAttackableTargetGoal.class)
public class NearestAttackableTargetGoalMixin implements INearestTargetGoal {

    @Shadow
    protected TargetingConditions targetConditions;

    @Shadow
    @Nullable
    protected LivingEntity target;

    @Unique
    private static final BiPredicate<Mob, LivingEntity> vampirism$nonVampireCheck = (mob, target) -> !Helper.appearsAsVampire(target, mob);

    @Unique
    private static final TargetingConditions.Selector vampirism$noFactionEntityCheck = (entity, level) -> !(entity instanceof IFactionEntity);

    @Override
    public void vampirism$ignoreVampires(Mob mob) {
        TargetingConditions.Selector predicate = (l, level) -> vampirism$nonVampireCheck.test(mob, l);
        if (((TargetConditionAccessor) this.targetConditions).getSelector() != null) {
            predicate = new AndTargetSelector(predicate, ((TargetConditionAccessor) this.targetConditions).getSelector());
        }
        this.targetConditions.selector(predicate);
    }

    @Override
    public void vampirism$ignoreFactionEntities() {
        TargetingConditions.Selector predicate = vampirism$noFactionEntityCheck;
        if (((TargetConditionAccessor) this.targetConditions).getSelector() != null) {
            predicate = new AndTargetSelector(predicate, ((TargetConditionAccessor) this.targetConditions).getSelector());
        }
        this.targetConditions.selector(predicate);
    }

    @Override
    public void vampirism$ignoreLineOfSight() {
        this.targetConditions.ignoreLineOfSight();
    }
}
