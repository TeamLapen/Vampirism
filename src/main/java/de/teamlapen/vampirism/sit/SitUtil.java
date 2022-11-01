/**
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */

package de.teamlapen.vampirism.sit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

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
    private static final Map<ResourceLocation, Map<BlockPos, Pair<SitEntity, BlockPos>>> OCCUPIED = new HashMap<>();

    /**
     * Adds a sit entity to the map that keeps track of them. This does not spawn the entity itself.
     *
     * @param world     The world to add the entity in
     * @param blockPos  The position at which to add the entity
     * @param entity    The entity to add
     * @param playerPos The position of the player who is sitting down. Used for correctly positioning the player after dismounting
     * @return true if the entity was added, false otherwhise. This is always false on the client.
     */
    public static boolean addSitEntity(World world, BlockPos blockPos, SitEntity entity, BlockPos playerPos) {
        if (!world.isClientSide && playerPos != null) {
            ResourceLocation id = getDimensionTypeId(world);

            if (!OCCUPIED.containsKey(id))
                OCCUPIED.put(id, new HashMap<>());

            OCCUPIED.get(id).put(blockPos, Pair.of(entity, playerPos));
            return true;
        }

        return false;
    }

    /**
     * Removes a sit entity from the map that keeps track of them. This does not remove the entity itself.
     *
     * @param world The world to remove the entity from
     * @param pos   The position to remove the entity from
     * @return true if the entity was removed, false otherwise. This is always false on the client.
     */
    public static boolean removeSitEntity(World world, BlockPos pos) {
        if (!world.isClientSide) {
            ResourceLocation id = getDimensionTypeId(world);

            if (OCCUPIED.containsKey(id)) {
                OCCUPIED.get(id).remove(pos).getLeft();
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the sit entity that is situated at the given position in the given world
     *
     * @param world The world to get the entity from
     * @param pos   The position to get the entity from
     * @return The entity at the given position in the given world, null if there is none. This is always null on the client.
     */
    public static SitEntity getSitEntity(World world, BlockPos pos) {
        if (!world.isClientSide) {
            ResourceLocation id = getDimensionTypeId(world);

            if (OCCUPIED.containsKey(id) && OCCUPIED.get(id).containsKey(pos))
                return OCCUPIED.get(id).get(pos).getLeft();
        }

        return null;
    }

    /**
     * Gets the position the player was at before he sat down
     *
     * @param player    The player
     * @param sitEntity sit entity the player is sitting on
     * @return The position the player was at before he sat down, null if the player is not sitting. This is always null on the client.
     */
    public static BlockPos getPreviousPlayerPosition(PlayerEntity player, SitEntity sitEntity) {
        if (!player.level.isClientSide) {
            ResourceLocation id = getDimensionTypeId(player.level);

            if (OCCUPIED.containsKey(id)) {
                for (Pair<SitEntity, BlockPos> pair : OCCUPIED.get(id).values()) {
                    if (pair.getLeft() == sitEntity)
                        return pair.getRight();
                }
            }
        }

        return null;
    }

    /**
     * Checks whether there is a player sitting at the given block position in the given world
     *
     * @param world The world to check in
     * @param pos   The position to check at
     * @return true if a player is sitting at the given position in the given world, false otherwhise. This is always false on the client.
     */
    public static boolean isOccupied(World world, BlockPos pos) {
        ResourceLocation id = getDimensionTypeId(world);

        return SitUtil.OCCUPIED.containsKey(id) && SitUtil.OCCUPIED.get(id).containsKey(pos);
    }

    /**
     * Checks whether a player is sitting anywhere
     *
     * @param player The player to check
     * @return true if the given player is sitting anywhere, false otherwhise
     */
    public static boolean isPlayerSitting(PlayerEntity player) {
        for (ResourceLocation i : OCCUPIED.keySet()) {
            for (Pair<SitEntity, BlockPos> pair : OCCUPIED.get(i).values()) {
                if (pair.getLeft().hasPassenger(player))
                    return true;
            }
        }

        return false;
    }

    private static ResourceLocation getDimensionTypeId(World world) {
        return world.dimension().location();
    }
}