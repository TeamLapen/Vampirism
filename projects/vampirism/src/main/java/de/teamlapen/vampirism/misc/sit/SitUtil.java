/*
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */

package de.teamlapen.vampirism.misc.sit;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Use this class to manage sit entities correctly
 */
public class SitUtil {
    /**
     * <dimension type id, <position, <entity, previous player position>>>
     * This map only gets populated on server side.
     */
    private static final Map<Identifier, Map<BlockPos, SitEntity>> OCCUPIED = new HashMap<>();

    /**
     * Adds a sit entity to the map that keeps track of them. This does not spawn the entity itself.
     *
     * @param level    The world to add the entity in
     * @param pos The position at which to add the entity
     * @param sit   The entity to add
     * @return true if the entity was added, false otherwise. This is always false on the client.
     */
    public static boolean registerSitEntity(Level level, BlockPos pos, SitEntity sit) {
        if (level.isClientSide()) return false;

        Identifier dimension = getDimensionId(level);
        OCCUPIED.computeIfAbsent(dimension, s -> new HashMap<>());

        Map<BlockPos, SitEntity> map = OCCUPIED.get(dimension);
        SitEntity existing = map.get(pos);

        if (existing != null && existing != sit) {
            sit.discard();
            return false;
        }

        map.put(pos, sit);
        return true;
    }

    /**
     * Removes a sit entity from the map that keeps track of them. This does not remove the entity itself.
     *
     * @param level The world to remove the entity from
     * @param pos   The position to remove the entity from
     */
    public static void unregisterSitEntity(Level level, BlockPos pos) {
        if (level.isClientSide()) return;

        Map<BlockPos, SitEntity> map = OCCUPIED.get(getDimensionId(level));
        if (map != null) {
            map.remove(pos);
        }
    }

    /**
     * Gets the sit entity that is situated at the given position in the given world
     *
     * @param level The world to get the entity from
     * @param pos   The position to get the entity from
     * @return The entity at the given position in the given world, null if there is none. This is always null on the client.
     */
    public static @Nullable SitEntity getSitEntity(Level level, BlockPos pos) {
        if (level.isClientSide()) return null;

        Map<BlockPos, SitEntity> map = OCCUPIED.get(getDimensionId(level));
        return map != null ? map.get(pos) : null;
    }

    /**
     * Tries to make a player sit on the block by creating a new sit entity and starting riding it.
     */
    public static void startSitting(Player player, Level level, BlockPos pos, double offset) {
        if (level.isClientSide()) return;

        if (player.isShiftKeyDown() || isSitting(player)) return;

        if (!isPlayerInRange(player, pos) || isOccupied(level, pos) || !player.getMainHandItem().isEmpty()) return;

        SitEntity sit = SitEntity.createEntity(player, level, pos, offset);
        if (sit != null && registerSitEntity(level, pos, sit)) {
            level.addFreshEntity(sit);
            player.startRiding(sit);
        }
    }

    /**
     * Returns whether the player is close enough to the block to be able to sit on it
     *
     * @param player The player
     * @param pos    The position of the block to sit on
     * @return true if the player is close enough, false otherwise
     */
    private static boolean isPlayerInRange(Player player, BlockPos pos) {
        Vec3 playerPos = player.position().add(0.5, 0.5, 0.5);
        Vec3 blockCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        double reach = Player.DEFAULT_BLOCK_INTERACTION_RANGE;
        AttributeInstance reachAttribute = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        if (reachAttribute != null) {
            reach = reachAttribute.getValue();
        }

        AABB range = new AABB(
                blockCenter.x - reach, blockCenter.y - reach, blockCenter.z - reach,
                blockCenter.x + reach, blockCenter.y + reach, blockCenter.z + reach
        );

        return range.contains(playerPos);
    }

    /**
     * Checks whether there is a player sitting at the given block position in the given world
     *
     * @param level The world to check in
     * @param pos   The position to check at
     * @return true if a player is sitting at the given position in the given world, false otherwise. This is always false on the client.
     */
    public static boolean isOccupied(Level level, BlockPos pos) {
        Map<BlockPos, SitEntity> map = OCCUPIED.get(getDimensionId(level));
        return map != null && map.containsKey(pos);
    }

    /**
     * Checks whether a player is sitting anywhere
     *
     * @param player The player to check
     * @return true if the given player is sitting anywhere, false otherwise
     */
    public static boolean isSitting(Player player) {
        for (Map<BlockPos, SitEntity> map : OCCUPIED.values()) {
            for (SitEntity sit : map.values()) {
                if (sit.hasPassenger(player)) return true;
            }
        }

        return false;
    }

    public static @Nullable Vec3 tryMultipleStandUpLocations(Entity entity, CollisionGetter level, BlockPos... positions) {
        for (BlockPos pos : positions) {
            Vec3 result = tryStandUpLocation(entity, level, pos);
            if (result != null) return result;
        }

        return null;
    }

    static @Nullable Vec3 tryStandUpLocation(Entity entity, CollisionGetter level, BlockPos pos) {
        return DismountHelper.findSafeDismountLocation(entity.getType(), level, pos, true);
    }

    private static Identifier getDimensionId(Level level) {
        return level.dimension().identifier();
    }
}