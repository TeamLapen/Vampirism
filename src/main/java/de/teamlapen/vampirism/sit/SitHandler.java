/**
 * Licenced under GNU GPLv3. See LICENCE.txt in this package.
 * Credits to bl4ckscor3's Sit https://github.com/bl4ckscor3/Sit/
 */

package de.teamlapen.vampirism.sit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SitHandler {
    public static void startSitting(PlayerEntity player, World level, BlockPos pos, double offset) {
        if (!level.isClientSide && !SitUtil.isPlayerSitting(player) && !player.isShiftKeyDown()) {
            if (isPlayerInRange(player, pos) && !SitUtil.isOccupied(level, pos) && player.getMainHandItem().isEmpty()) //level.getBlockState(pos.above()).isAir(level, pos.above()
            {

                SitEntity sit = SitEntity.newEntity(level, pos, offset, player.position());

                if (SitUtil.addSitEntity(level, pos, sit)) {
                    level.addFreshEntity(sit);
                    player.startRiding(sit);
                }
            }
        }
    }


    @SubscribeEvent
    public static void onBreak(BreakEvent event) {
        if (!event.getWorld().isClientSide()) {
            //BreakEvent gets a World in its constructor, so the cast is safe
            SitEntity entity = SitUtil.getSitEntity((World) event.getWorld(), event.getPos());

            if (entity != null) {
                entity.remove();
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
    private static boolean isPlayerInRange(PlayerEntity player, BlockPos pos) {
        BlockPos playerPos = player.blockPosition();
        double blockReachDistance = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();

//        if(blockReachDistance == 0) //player has to stand on top of the block
//            return playerPos.getY() - pos.getY() <= 1 && playerPos.getX() - pos.getX() == 0 && playerPos.getZ() - pos.getZ() == 0;

        pos = pos.offset(0.5D, 0.5D, 0.5D);

        AxisAlignedBB range = new AxisAlignedBB(pos.getX() + blockReachDistance, pos.getY() + blockReachDistance, pos.getZ() + blockReachDistance, pos.getX() - blockReachDistance, pos.getY() - blockReachDistance, pos.getZ() - blockReachDistance);

        playerPos = playerPos.offset(0.5D, 0.5D, 0.5D);
        return range.minX <= playerPos.getX() && range.minY <= playerPos.getY() && range.minZ <= playerPos.getZ() && range.maxX >= playerPos.getX() && range.maxY >= playerPos.getY() && range.maxZ >= playerPos.getZ();
    }
}