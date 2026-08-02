package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors.flyingsword;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.particles.FlyingBloodEntityParticleOptions;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.ActionBuilder;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import de.teamlapen.vampirism.common.world.entity.dracula.FlyingSwordEntity;
import de.teamlapen.vampirism.common.world.entity.dracula.IDraculaAnimations;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlyingSwordAttack extends Behavior<Dracula> {

    public static void configure(ActionBuilder<Dracula> builder) {
        builder.activeMemory(ModMemoryTypes.FLYING_SWORD_ACTIVE)
                .cooldown(ModMemoryTypes.FLYING_SWORD_COOLDOWN, () -> 30 * 20)
                .add(EquipSword.create(), EquipSword.sensors(), EquipSword.memories())
                .add(FlyingSwordAttack.create(), FlyingSwordAttack.sensors(), FlyingSwordAttack.memories())
                .add(UnEquipSword.create(), UnEquipSword.sensors(), UnEquipSword.memories());
    }

    private Phase phase = Phase.CHANNELING;
    private int ticks = 0;
    private int attacksDone = 0;
    private int totalAttacks = 0;
    private final List<LivingEntity> targets = new ArrayList<>();

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(SensorType.NEAREST_LIVING_ENTITIES, ModSensors.DRACULA_ATTACKABLE_SENSOR.get());
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(
                ModMemoryTypes.FLYING_SWORD_COOLDOWN.get(),
                ModMemoryTypes.FLYING_SWORD_ACTIVE.get(),
                ModMemoryTypes.FLYING_SWORD_EQUIPPED.get(),
                ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(),
                ModMemoryTypes.FLYING_SWORD_SHOT.get()
        );
    }

    public static FlyingSwordAttack create() {
        return new FlyingSwordAttack();
    }

    public FlyingSwordAttack() {
        super(Map.of(
                ModMemoryTypes.FLYING_SWORD_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT,
                ModMemoryTypes.FLYING_SWORD_ACTIVE.get(), MemoryStatus.VALUE_PRESENT,
                ModMemoryTypes.FLYING_SWORD_EQUIPPED.get(), MemoryStatus.VALUE_PRESENT,
                ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get(), MemoryStatus.VALUE_PRESENT,
                ModMemoryTypes.FLYING_SWORD_SHOT.get(), MemoryStatus.VALUE_ABSENT
        ), 400);
    }

    @Override
    protected void start(ServerLevel level, Dracula entity, long gameTime) {
        this.phase = Phase.CHANNELING;
        this.ticks = 0;
        this.attacksDone = 0;
        this.totalAttacks = entity.getRandom().nextInt(7) + 6;
        this.targets.clear();
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void tick(ServerLevel level, Dracula entity, long gameTime) {
        this.ticks++;
        entity.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);

        if (phase == Phase.CHANNELING) {
            if (ticks % 10 == 0) {
                 NearestVisibleLivingEntities visibleEntities = entity.getBrain().getMemory(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get()).orElseGet(NearestVisibleLivingEntities::empty);
                 visibleEntities.findAll(e -> e.distanceToSqr(entity) < 15 * 15 && e != entity).forEach(target -> {
                     level.addParticle(new FlyingBloodEntityParticleOptions(entity.getId(), true), target.getX(), target.getY(),target.getZ(), 1,1,1);
                     target.hurtServer(level, level.damageSources().magic(), 0.5f);

                     if (!targets.contains(target)) {
                         targets.add(target);
                     }
                 });
            }

            if (ticks >= 40) {
                if (targets.isEmpty()) {
                    NearestVisibleLivingEntities visibleEntities = entity.getBrain().getMemory(ModMemoryTypes.NEAREST_VISIBLE_ATTACKABLE.get()).orElseGet(NearestVisibleLivingEntities::empty);
                    for (LivingEntity e : visibleEntities.findAll(e -> e != entity)) {
                        targets.add(e);
                        break;
                    }
                }

                if (targets.isEmpty()) {
                    doStop(level, entity, gameTime);
                } else {
                    phase = Phase.ATTACKING;
                    ticks = 0;
                }
            }
        } else if (phase == Phase.ATTACKING) {
            if (ticks % 15 == 0) {
                if (attacksDone < totalAttacks) {
                    targets.removeIf(e -> !e.isAlive());
                    if (targets.isEmpty()) {
                        doStop(level, entity, gameTime);
                        return;
                    }
                    LivingEntity target = targets.get(entity.getRandom().nextInt(targets.size()));
                    
                    entity.swing(InteractionHand.MAIN_HAND);
                    BehaviorUtils.lookAtEntity(entity, target);

                    float damage = 10f + (targets.size() * 0.5f);
                    FlyingSwordEntity sword = new FlyingSwordEntity(level, entity, target, damage);
                    level.addFreshEntity(sword);
                    entity.triggerAnim(IDraculaAnimations.Animation.FLYING_SWORD_1, IDraculaAnimations.Animation.FLYING_SWORD_2);
                    attacksDone++;
                } else {
                    doStop(level, entity, gameTime);
                }
            }
        }
    }

    @Override
    protected void stop(ServerLevel level, Dracula entity, long gameTime) {
        super.stop(level, entity, gameTime);
        entity.getBrain().setMemory(ModMemoryTypes.FLYING_SWORD_SHOT.get(), Unit.INSTANCE);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Dracula entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(ModMemoryTypes.FLYING_SWORD_ACTIVE.get());
    }


    private enum Phase {
        CHANNELING,
        ATTACKING
    }
}
