package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.IDraculaAnimations;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Set;

/**
 * Vanilla {@link net.minecraft.world.entity.ai.behavior.MeleeAttack} variant that additionally triggers one of
 * Dracula's melee attack animations on each swing.
 */
public class AnimatedMeleeAttackBehavior {

    public static BehaviorDescription<Dracula> build(int cooldownBetweenAttacks) {
        return new BehaviorDescription<>(create(cooldownBetweenAttacks), Set.of(SensorType.NEAREST_LIVING_ENTITIES), Set.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES));
    }

    public static OneShot<Dracula> create(int cooldownBetweenAttacks) {
        return BehaviorBuilder.create(inst -> inst.group(
                inst.registered(MemoryModuleType.LOOK_TARGET),
                inst.present(MemoryModuleType.ATTACK_TARGET),
                inst.absent(MemoryModuleType.ATTACK_COOLING_DOWN),
                inst.present(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
        ).apply(inst, (lookTarget, attackTarget, attackCoolingDown, nearestVisible) -> (level, dracula, gameTime) -> {
            LivingEntity target = inst.get(attackTarget);
            if (dracula.isWithinMeleeAttackRange(target) && inst.get(nearestVisible).contains(target)) {
                lookTarget.set(new EntityTracker(target, true));
                dracula.swing(InteractionHand.MAIN_HAND);
                dracula.triggerAnim(IDraculaAnimations.Animation.ATTACK_1, IDraculaAnimations.Animation.ATTACK_2);
                dracula.doHurtTarget(level, target);
                attackCoolingDown.setWithExpiry(true, cooldownBetweenAttacks);
                return true;
            }
            return false;
        }));
    }
}
