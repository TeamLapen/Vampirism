package de.teamlapen.vampirism.common.world.entity.dracula.ai.behaviors;

import com.mojang.datafixers.kinds.OptionalBox;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModMemoryTypes;
import de.teamlapen.vampirism.common.world.entity.ai.activities.actions.ActionBuilder;
import de.teamlapen.vampirism.common.world.entity.dracula.Dracula;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class SummonProtectorsBehavior {

    public static void configure(ActionBuilder<Dracula> builder) {
        builder.activeMemory(ModMemoryTypes.SUMMON_PROTECTOR_ACTIVE)
                .cooldown(ModMemoryTypes.SUMMON_PROTECTOR_COOLDOWN, () -> 20 * 20)
                .add(SummonProtectorsBehavior.create(), SummonProtectorsBehavior.sensors(), SummonProtectorsBehavior.memories())
                .canActivate((level, dracula) -> dracula.getBrain().getMemory(ModMemoryTypes.SUMMONS.get()).map(List::size).orElse(0) < SummonProtectorsBehavior.MAX_SUMMONS * 0.7);
    }

    public static Set<SensorType<? extends Sensor<? super Dracula>>> sensors() {
        return Set.of();
    }

    public static Set<MemoryModuleType<?>> memories() {
        return Set.of(
                ModMemoryTypes.SUMMON_PROTECTOR_COOLDOWN.get(),
                ModMemoryTypes.SUMMON_PROTECTOR_ACTIVE.get(),
                ModMemoryTypes.SUMMONS.get(),
                ModMemoryTypes.ALLIES.get()
        );
    }

    public static final int MAX_SUMMONS = 10;

    public static OneShot<Dracula> create() {
        return BehaviorBuilder.create(
                inst -> inst.group(
                        inst.absent(ModMemoryTypes.SUMMON_PROTECTOR_COOLDOWN.get()),
                        inst.present(ModMemoryTypes.SUMMON_PROTECTOR_ACTIVE.get()),
                        inst.registered(ModMemoryTypes.SUMMONS.get()),
                        inst.registered(ModMemoryTypes.ALLIES.get())
                ).apply(inst, (used, using, summons, allies) ->
                        ((level, dracula, gameTime) -> {
                            Brain<Dracula> brain = dracula.getBrain();
                            List<UUID> uuids = brain.getMemory(ModMemoryTypes.SUMMONS.get()).orElseGet(List::of);
                            List<LivingEntity> entities = new ArrayList<>();
                            for (UUID uuid : uuids) {
                                Entity entity = level.getEntity(uuid);
                                if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive() && livingEntity.distanceToSqr(dracula) < 100 * 100) {
                                    entities.add(livingEntity);
                                }
                            }
                            int maxSummons = MAX_SUMMONS + 2 * (dracula.getFightScalePlayers() - 1);
                            int spawned = 0;
                            while (entities.size() < maxSummons && spawned < 2 + 2 * dracula.getFightScalePlayers()) {
                                LivingEntity summon = summon(level, dracula);
                                if (summon != null) {
                                    entities.add(summon);
                                }
                                spawned++;
                            }
                            summons.set(entities.stream().map(Entity::getUUID).toList());
                            addAllies(entities, inst, allies);
                            return true;
                        })

                ));
    }

    private static void addAllies(List<LivingEntity> entities, BehaviorBuilder.Instance<Dracula> inst, MemoryAccessor<OptionalBox.Mu, Set<UUID>> allies) {
        var uuids = inst.tryGet(allies).map(HashSet::new).orElseGet(HashSet::new);
        uuids.addAll(entities.stream().map(Entity::getUUID).toList());
        allies.set(uuids);
    }


    protected static @Nullable LivingEntity summon(ServerLevel level, Dracula dracula) {
        RandomSource random = dracula.getRandom();
        BlockPos pos = BlockPos.containing(
                dracula.getX() + (random.nextDouble() - random.nextDouble()) * 5 + 0.5,
                dracula.getY() + 1,
                dracula.getZ() + (random.nextDouble() - random.nextDouble()) * 5 + 0.5);
        return SpawnUtil.trySpawnMob(ModEntities.VAMPIRE.get(), EntitySpawnReason.EVENT, level, pos, 5, 3, 3, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER, false).map(vampire -> {
            vampire.setYRot(random.nextFloat() * 360);
            vampire.setAdvancedLeader(dracula);
            return vampire;
        }).orElse(null);
    }
}
