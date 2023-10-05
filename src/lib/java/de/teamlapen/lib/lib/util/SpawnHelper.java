package de.teamlapen.lib.lib.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Supplier;

public class SpawnHelper {

    public static <T extends Entity> Optional<T> createEntity(Level level, EntityType<T> type) {
        return Optional.ofNullable(type.create(level));
    }

    public static <T extends Entity> Optional<T> createEntity(Level level, EntityType<T> type, Vec3 pos) {
        return createEntity(level, type).map(s -> {
            s.setPos(pos);
            return s;
        });
    }

    public static <T extends Entity> Optional<T> createEntity(Level level, Supplier<EntityType<T>> type) {
        return Optional.ofNullable(type.get().create(level));
    }

    public static <T extends Entity> Optional<T> createEntity(Level level, Supplier<EntityType<T>> type, Vec3 pos) {
        return createEntity(level, type).map(s -> {
            s.setPos(pos);
            return s;
        });
    }
}
