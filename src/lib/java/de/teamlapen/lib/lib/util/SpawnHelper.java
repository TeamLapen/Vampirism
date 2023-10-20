package de.teamlapen.lib.lib.util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpawnHelper {

    public static <T extends LivingEntity> T spawn(EntityType<T> entity, Level level, Consumer<T> functions) {
        T t = entity.create(level);
        if (t != null) {
            functions.accept(t);
            level.addFreshEntity(t);
        }
        return t;
    }

    public static <T extends LivingEntity> T spawn(Supplier<EntityType<T>> entity, Level level, Consumer<T> functions) {
        T t = entity.get().create(level);
        if (t != null) {
            functions.accept(t);
            level.addFreshEntity(t);
        }
        return t;
    }
}
