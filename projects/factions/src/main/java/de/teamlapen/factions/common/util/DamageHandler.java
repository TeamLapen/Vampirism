package de.teamlapen.factions.common.util;

import de.teamlapen.factions.common.world.ModDamageSources;
import de.teamlapen.factions.common.world.attachments.LevelDamage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Function;

public class DamageHandler {

    public static Optional<DamageSource> getDamageSource(Level world, Function<ModDamageSources, DamageSource> sourceFunc) {
        return Optional.of(LevelDamage.get(world)).map(sourceFunc);
    }

    public static boolean hurtModded(ServerLevel level, Entity entity, Function<ModDamageSources, DamageSource> sourceFunc, float amount) {
        return getDamageSource(level, sourceFunc).map(source -> entity.hurtServer(level, source, amount)).orElse(false);
    }

    @SuppressWarnings("UnusedReturnValue")
    public static boolean hurtVanilla(ServerLevel level, Entity entity, Function<DamageSources, DamageSource> sourceFunc, float amount) {
        DamageSource source = sourceFunc.apply(level.damageSources());
        return entity.hurtServer(level, source, amount);
    }
}
