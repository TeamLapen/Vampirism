package de.teamlapen.faction.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SpawnUtil {

    public static void replaceEntity(LivingEntity old, LivingEntity replacement) {
        Level w = old.level();
        NeoForge.EVENT_BUS.post(new LivingConversionEvent.Post(old, replacement));
        old.remove(Entity.RemovalReason.DISCARDED);
        w.addFreshEntity(replacement);
    }

    public static boolean spawnEntityInWorld(ServerLevel world, AABB box, @Nullable Entity e, int maxTry, List<? extends LivingEntity> avoidedEntities, EntitySpawnReason reason) {
        if (e == null) return false;

        if (!world.hasChunksAt((int) box.minX, (int) box.minY, (int) box.minZ, (int) box.maxX, (int) box.maxY, (int) box.maxZ)) {
            return false;
        }
        boolean flag = false;
        int i = 0;
        BlockPos backupPos = null; //
        while (!flag && i++ < maxTry) {
            BlockPos c = getRandomPosInBox(world, box); //TODO select a better location (more viable)
            if (world.noCollision(new AABB(c))) {
                if (world.isAreaLoaded(c, 5) && SpawnPlacements.isSpawnPositionOk(e.getType(), world, c)) {//I see no other way
                    e.setPos(c.getX(), c.getY() + 0.2, c.getZ());
                    if (SpawnPlacements.checkSpawnRules(e.getType(), world, reason, c, world.getRandom()) && !(e instanceof Mob) || (((Mob) e).checkSpawnRules(world, reason) && ((Mob) e).checkSpawnObstruction(e.level()))) {
                        backupPos = c; //Store the location in case we do not find a better one
                        for (LivingEntity p : avoidedEntities) {

                            if (!(p.distanceToSqr(e) < 500 && p.hasLineOfSight(e))) {
                                flag = true;
                            }
                        }
                    }
                }
            }
        }
        if (!flag && backupPos != null) {
            //If we did not find a "hidden" position, use the last valid position (if available)
            e.setPos(backupPos.getX(), backupPos.getY() + 0.2, backupPos.getZ());
            flag = true;
        }

        if (flag) {
            world.addFreshEntity(e);
            onInitialSpawn(world, e, reason);
            return true;
        }
        return false;
    }

    private static void onInitialSpawn(ServerLevel level, Entity e, EntitySpawnReason reason) {
        if (e instanceof Mob mob) {
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(e.blockPosition()), reason, null);
        }
    }

    @Nullable
    public static Entity spawnEntityInWorld(ServerLevel world, AABB box, EntityType<?> entityType, int maxTry, List<? extends LivingEntity> avoidedEntities, EntitySpawnReason reason) {
        Entity e = entityType.create(world, reason);
        if (spawnEntityInWorld(world, box, e, maxTry, avoidedEntities, reason)) {
            return e;
        } else if (e != null){
            e.remove(Entity.RemovalReason.DISCARDED);
        }
        return null;
    }

    private static BlockPos getRandomPosInBox(Level w, AABB box) {
        int x = (int) box.minX + w.random.nextInt((int) (box.maxX - box.minX) + 1);
        int z = (int) box.minZ + w.random.nextInt((int) (box.maxZ - box.minZ) + 1);
        int y = w.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 5;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
        while (y > box.minY && !w.getBlockState(pos).isRedstoneConductor(w, pos)) {
            pos.set(x, --y, z);
        }

        if (y < box.minY || y > box.maxY - 1) {
            pos.set(x, (int) box.minY + w.random.nextInt((int) (box.maxY - box.minY) + 1), z);
        }
        return pos.above();
    }

    public static <T extends Entity> @NotNull Optional<T> createEntity(EntityType<T> type, Level world, EntitySpawnReason spawnReason) {
        T e = type.create(world, spawnReason);
        if (e == null) {
            return Optional.empty();
        }
        return Optional.of(e);
    }

    @Nullable
    public static <T extends LivingEntity> T spawn(EntityType<T> entity, Level level, EntitySpawnReason reason, Consumer<T> functions) {
        T t = entity.create(level, reason);
        if (t != null) {
            functions.accept(t);
            level.addFreshEntity(t);
        }
        return t;
    }

    @Nullable
    public static <T extends LivingEntity> T spawn(Supplier<EntityType<T>> entity, Level level, EntitySpawnReason reason, Consumer<T> functions) {
        T t = entity.get().create(level, reason);
        if (t != null) {
            functions.accept(t);
            level.addFreshEntity(t);
        }
        return t;
    }
}
