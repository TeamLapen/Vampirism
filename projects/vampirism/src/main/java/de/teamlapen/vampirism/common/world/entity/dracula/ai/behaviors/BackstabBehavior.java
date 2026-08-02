package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.core.ModSensors;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.ActionBuilder;
import de.teamlapen.vampirism.common.world.entity.ai.memory.HurtByEntities;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class BackstabBehavior extends Behavior<Dracula> {

    public static void configure(ActionBuilder<Dracula> builder) {
        builder.activeMemory(ModMemoryTypes.BACKSTAB_ACTIVE)
                .cooldown(ModMemoryTypes.BACKSTAB_COOLDOWN, () -> 15 * 20)
                .add(BackstabBehavior.create(), BackstabBehavior.sensors(), BackstabBehavior.memories())
                .requires(ModMemoryTypes.HURT_BY_ENTITIES, MemoryStatus.VALUE_PRESENT)
                .canActivate((level, dracula) -> dracula.getBrain().hasMemoryValue(ModMemoryTypes.HURT_BY_ENTITIES.get()));
    }

    private static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of(ModSensors.DRACULA_ATTACKABLE_SENSOR.get(), SensorType.NEAREST_LIVING_ENTITIES, ModSensors.HURT_BY.get());
    }

    private static Set<MemoryModuleType<?>> memories() {
        return Set.of(ModMemoryTypes.BACKSTAB_ACTIVE.get(), ModMemoryTypes.BACKSTAB_COOLDOWN.get(), ModMemoryTypes.HURT_BY_ENTITIES.get());
    }

    @Nullable
    private Vec3 originalPos;
    private int ticks = 0;

    public BackstabBehavior() {
        super(Map.of(
                ModMemoryTypes.HURT_BY_ENTITIES.get(), MemoryStatus.VALUE_PRESENT,
                ModMemoryTypes.BACKSTAB_ACTIVE.get(), MemoryStatus.VALUE_PRESENT,
                ModMemoryTypes.BACKSTAB_COOLDOWN.get(), MemoryStatus.VALUE_ABSENT
        ), 100);
    }

    public static BackstabBehavior create() {
        return new BackstabBehavior();
    }

    @Override
    protected void start(ServerLevel level, Dracula entity, long gameTime) {
        this.originalPos = entity.position();
        this.ticks = 0;

        var targetOpt = getTarget(entity);
        if (targetOpt.isEmpty())  {
            doStop(level, entity, gameTime);
            return;
        }
        var target = targetOpt.get().entity();
        Vec3 behind = target.position().add(target.getLookAngle().scale(-1.5));
        entity.teleportTo(behind.x, behind.y, behind.z);

        entity.doHurtTarget(level, target);
        entity.swing(entity.getUsedItemHand());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Dracula owner) {
        return getTarget(owner).isPresent();
    }

    private Optional<HurtByEntities.HurtBy> getTarget(Dracula owner) {
        HurtByEntities hurtByEntities = owner.getBrain().getMemory(ModMemoryTypes.HURT_BY_ENTITIES.get()).orElseGet(HurtByEntities::empty);
        return hurtByEntities.hurtBy().stream().filter(x -> x.distanceSqt() > 5 * 5 && x.distanceSqt() < 20 * 20 && x.entity().distanceToSqr(owner) > 5 * 5 && x.entity().isAlive()).findFirst();
    }

    @Override
    protected void tick(ServerLevel level, Dracula entity, long gameTime) {
        this.ticks++;
        if (this.ticks >= 20) {
            assert originalPos != null;
            entity.teleportTo(originalPos.x, originalPos.y, originalPos.z);
            entity.getBrain().eraseMemory(ModMemoryTypes.BACKSTAB_ACTIVE.get());
        }
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Dracula entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(ModMemoryTypes.BACKSTAB_ACTIVE.get());
    }
}
