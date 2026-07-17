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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class SummonProtectorsBehavior {

    public static void configure(ActionBuilder<Dracula> builder) {
        builder.activeMemory(ModMemoryTypes.SUMMON_PROTECTOR_ACTIVE)
                .cooldown(ModMemoryTypes.SUMMON_PROTECTOR_COOLDOWN, () -> 20 * 10)
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


    private static final int SUMMON_POS_ATTEMPTS = 8;
    private static final double SUMMON_MIN_DISTANCE = 2;
    private static final double SUMMON_MAX_DISTANCE = 4;
    private static final double SUMMON_TARGET_SEARCH_RANGE = 32;

    protected static @Nullable LivingEntity summon(ServerLevel level, Dracula dracula) {
        RandomSource random = dracula.getRandom();
        BlockPos pos = findSummonPos(level, dracula, random);
        if (pos == null) {
            return null;
        }
        return SpawnUtil.trySpawnMob(ModEntities.VAMPIRE.get(), EntitySpawnReason.EVENT, level, pos, 5, 3, 3, SpawnUtil.Strategy.ON_TOP_OF_COLLIDER, false).map(vampire -> {
            vampire.setYRot(random.nextFloat() * 360);
            vampire.setAdvancedLeader(dracula);
            vampire.addEffect(new MobEffectInstance(MobEffects.STRENGTH, MobEffectInstance.INFINITE_DURATION, 1, false, false));
            Player nearestPlayer = level.getNearestPlayer(vampire, SUMMON_TARGET_SEARCH_RANGE);
            if (nearestPlayer != null) {
                vampire.setTarget(nearestPlayer);
            }
            return vampire;
        }).orElse(null);
    }

    /**
     * Finds a candidate summon position close to Dracula that has a clear line of sight from him,
     * so summons don't end up inside a wall or in a room on the other side of one.
     */
    private static @Nullable BlockPos findSummonPos(ServerLevel level, Dracula dracula, RandomSource random) {
        Vec3 from = dracula.getEyePosition();
        for (int i = 0; i < SUMMON_POS_ATTEMPTS; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = SUMMON_MIN_DISTANCE + random.nextDouble() * (SUMMON_MAX_DISTANCE - SUMMON_MIN_DISTANCE);
            double x = dracula.getX() + Math.cos(angle) * distance;
            double z = dracula.getZ() + Math.sin(angle) * distance;
            Vec3 to = new Vec3(x, dracula.getY() + 1, z);
            HitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, dracula));
            if (hit.getType() == HitResult.Type.MISS) {
                return BlockPos.containing(x, dracula.getY() + 1, z);
            }
        }
        return null;
    }
}
