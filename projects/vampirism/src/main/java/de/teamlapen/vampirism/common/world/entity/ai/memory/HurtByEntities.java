package de.teamlapen.vampirism.common.world.entity.ai.memory;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Memory module for tracking entities that have hurt the entity
 */
public class HurtByEntities {

    private static final HurtByEntities EMPTY = new HurtByEntities();

    public static HurtByEntities empty() {
        return EMPTY;
    }

    private final Map<LivingEntity, HurtBy> hurtByMap;
    private final List<HurtBy> byDistance;

    private HurtByEntities() {
        this(new HashMap<>());
    }

    public HurtByEntities(List<HurtBy> hurtBy) {
        this(hurtBy.stream().collect(Collectors.toMap(HurtBy::entity, Function.identity())));
    }
    public HurtByEntities(Map<LivingEntity, HurtBy> hurtByMap) {
        this.hurtByMap = hurtByMap;
        this.byDistance = hurtByMap.values().stream().sorted(Comparator.comparingDouble(d -> d.distanceSqt)).toList();
    }

    public HurtByEntities hurtBy(ServerLevel level, LivingEntity entity, DamageSource source) {
        Entity sourceEntity = source.getEntity();
        if (!(sourceEntity instanceof LivingEntity living)) return this;

        HashMap<LivingEntity, HurtBy> livingEntityHurtByHashMap = new HashMap<>(hurtByMap);
        livingEntityHurtByHashMap.put(living, new HurtBy(living, level.getGameTime(), sourceEntity.distanceToSqr(entity)));
        return new HurtByEntities(livingEntityHurtByHashMap);
    }

    public List<HurtBy> hurtBy() {
        return this.byDistance;
    }

    public record HurtBy(LivingEntity entity, long time, double distanceSqt, long keepInMemoryTicks) {

        public HurtBy(LivingEntity entity, long time, double distance) {
            this(entity, time, distance, 20);
        }
    }
}
