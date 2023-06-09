/*
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */

package de.teamlapen.vampirism.sit;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

public class SitHandler {
    public static void startSitting(@NotNull Player player, @NotNull Level level, @NotNull BlockPos pos, double offset) {
        if (!level.isClientSide && !SitUtil.isPlayerSitting(player) && !player.isShiftKeyDown()) {
            if (isPlayerInRange(player, pos) && !SitUtil.isOccupied(level, pos) && player.getMainHandItem().isEmpty()) //level.getBlockState(pos.above()).isAir(level, pos.above()
            {

                SitEntity sit = SitEntity.newEntity(level, pos, offset);

                if (SitUtil.addSitEntity(level, pos, sit, player.blockPosition())) {
                    level.addFreshEntity(sit);
                    player.startRiding(sit);
                }
            }
        }
    }


    @SubscribeEvent
    public static void onBreak(BlockEvent.@NotNull BreakEvent event) {
        if (!event.getLevel().isClientSide()) {
            //BreakEvent gets a World in its constructor, so the cast is safe
            SitEntity entity = SitUtil.getSitEntity((Level) event.getLevel(), event.getPos());

            if (entity != null) {
                SitUtil.removeSitEntity((Level) event.getLevel(), event.getPos());
                entity.ejectPassengers();
            }
        }
    }

    /**
     * Returns whether the player is close enough to the block to be able to sit on it
     *
     * @param player The player
     * @param pos    The position of the block to sit on
     * @return true if the player is close enough, false otherwise
     */
    private static boolean isPlayerInRange(@NotNull Player player, BlockPos pos) {
        Vec3 playerPos = player.position();
        Vec3 blockPos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        double blockReachDistance = player.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue();

//        if(blockReachDistance == 0) //player has to stand on top of the block
//            return playerPos.getY() - blockPos.getY() <= 1 && playerPos.getX() - blockPos.getX() == 0 && playerPos.getZ() - blockPos.getZ() == 0;

        blockPos = blockPos.add(0.5D, 0.5D, 0.5D);

        AABB range = new AABB(blockPos.x() + blockReachDistance, blockPos.y() + blockReachDistance, blockPos.z() + blockReachDistance, blockPos.x() - blockReachDistance, blockPos.y() - blockReachDistance, blockPos.z() - blockReachDistance);

        playerPos = playerPos.add(0.5D, 0.5D, 0.5D);
        return range.minX <= playerPos.x() && range.minY <= playerPos.y() && range.minZ <= playerPos.z() && range.maxX >= playerPos.x() && range.maxY >= playerPos.y() && range.maxZ >= playerPos.z();
    }
}