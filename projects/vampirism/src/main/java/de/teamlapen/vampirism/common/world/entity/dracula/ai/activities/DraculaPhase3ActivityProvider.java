package de.teamlapen.vampirism.common.world.entity.dracula.ai.activities;

import de.teamlapen.vampirism.common.core.ModActivities;
import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.ActivityBuilder;
import de.teamlapen.vampirism.common.world.entity.ai.activities.BehaviorDescription;
import de.teamlapen.vampirism.common.world.entity.ai.system.AiActivityProvider;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.DraculaState;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.AnimatedMeleeAttackBehavior;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.BloodProjectilesBehavior;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.BloodSiphonBehavior;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.MistFormBehavior;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.NearbyKnockbackBehavior;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.RegenerationBehavior;
import de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.SummonVampireBats;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Optional;
import java.util.Set;

public class DraculaPhase3ActivityProvider extends AiActivityProvider<Dracula> {

    public DraculaPhase3ActivityProvider() {
        super(ModActivities.DRACULA_PHASE_3);
    }

    @Override
    protected void createActivity(ActivityBuilder<Dracula> builder) {
        builder.requires(ModMemoryTypes.DRACULA_PHASE_3, MemoryStatus.VALUE_PRESENT)
                .add(buildStopTarget())
                .add(MistFormBehavior.build(0.6f))
                .add(buildOutOfRange())
                .add(buildStartAttack())
                .add(buildMelee())
                .add(NearbyKnockbackBehavior.build(2, 0.5))
                .add(DraculaIdleActivityProvider.buildLook())
                .add(DraculaIdleActivityProvider.buildMovement(0.4f))
        ;

        var actions = builder.useActions();

        actions.addAction(ModActivities.DRACULA_REGENERATION, RegenerationBehavior::configure);
        actions.addAction(ModActivities.DRACULA_BLOOD_PROJECTILES, BloodProjectilesBehavior::configure);
        actions.addAction(ModActivities.DRACULA_SUMMON_BATS, SummonVampireBats::configure);
        actions.addAction(ModActivities.DRACULA_BLOOD_SIPHON, BloodSiphonBehavior::configure);
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(ServerLevel level, Dracula dracula) {
        if (dracula.getState() == DraculaState.MIST) {
            return Optional.empty();
        }
        Optional<LivingEntity> optional = BehaviorUtils.getLivingEntityFromUUIDMemory(dracula, MemoryModuleType.ANGRY_AT);
        Optional<? extends LivingEntity> result;
        if (optional.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight(level, dracula, optional.get())) {
            result = optional;
        } else {
            result = dracula.getBrain().getMemory(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get()).flatMap(x -> x.findClosest(y -> true));
        }
        return result;
    }

    //<editor-fold desc="Phase Behaviors">

    private BehaviorDescription<Dracula> buildStopTarget() {
        return new BehaviorDescription<>(StopAttackingIfTargetInvalid.create(), Set.of(), Set.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE));
    }

    private BehaviorDescription<Dracula> buildOutOfRange() {
        return new BehaviorDescription<>(SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F), Set.of(), Set.of(MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.ATTACK_TARGET, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES));
    }

    private BehaviorDescription<Dracula> buildStartAttack() {
        return new BehaviorDescription<>(StartAttacking.create(DraculaPhase3ActivityProvider::findNearestValidAttackTarget), Set.of(ModSensors.NEAREST_TARGETABLE_ENTITIES.get()), Set.of(MemoryModuleType.ATTACK_TARGET,MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, ModMemoryTypes.NEAREST_ATTACKABLE.get(), MemoryModuleType.ANGRY_AT));
    }

    public BehaviorDescription<Dracula> buildMelee() {
        return AnimatedMeleeAttackBehavior.build(15);
    }

    //</editor-fold>
}
