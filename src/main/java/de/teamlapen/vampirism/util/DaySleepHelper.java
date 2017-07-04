package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle coffin sleep during the day.
 * This works in the following way:
 * The player is set to sleep by {@link VampirePlayer#trySleep(BlockPos)} which is similar to {@link EntityPlayer#trySleep(BlockPos)}, but works during the day.
 * It uses reflection to modify size and more. It does not set  {@link EntityPlayer#sleeping} but {@link VampirePlayer#sleepingInCoffin}.
 * This method also sends a sleep packet to the client, which uses the vanilla methods to sleep. Only the sleep gui is replaced by a custom one in the client event handler, when the vanilla gui is open and the player is in a coffin.
 * That GUI sends a  {@link InputEventPacket#WAKEUP} packet to the server.
 * The player should be waked by {@link VampirePlayer#wakeUpPlayer(boolean, boolean, boolean)}, which uses the vanilla wakeup method, but also sets the vampire player variables and updates this class.
 * {@link VampirePlayer} also updates the size every tick to 0.2/0.2 since it is reset by vanilla and sets {@link EntityPlayer#noClip} each tick, so the player does not collide with blocks above the coffin. Because of the we also have to set the motion variables to zero, so the player does no fall.
 * <p>
 * {@link DaySleepHelper#updateAllPlayersSleeping(World)} has to be called every time a player leaves/enters a coffin or leaves/enters the world.
 */
public class DaySleepHelper {
    private static final Map<Integer, Boolean> allPlayersAsleep = new HashMap<>();

    /**
     * Updates the all players sleeping flag
     *
     * @param world
     */
    public static void updateAllPlayersSleeping(World world) {
        updateAllPlayersSleeping(world, 0);
    }

    /**
     * Updates the all players sleeping flag
     *
     * @param world
     * @param ignorePlayers This many players will be ignored for calculation. Used for log out event
     */
    public static void updateAllPlayersSleeping(World world, int ignorePlayers) {
        if (!world.playerEntities.isEmpty()) {
            int i = 0;
            int j = 0;

            for (EntityPlayer entityplayer : world.playerEntities) {
                if (entityplayer.isSpectator()) {
                    ++i;
                } else if (VampirePlayer.get(entityplayer).isPlayerSleeping()) {
                    ++j;
                }
            }

            boolean all = j > 0 && j >= world.playerEntities.size() - i - ignorePlayers;
            allPlayersAsleep.put(world.provider.getDimension(), all);
        }
    }

    /**
     * Check if all players are fully asleep and if so make it night and wake all players
     *
     * @param world
     */
    public static void checkSleepWorld(World world) {
        if (allPlayersAsleep.get(world.provider.getDimension()) == Boolean.TRUE) {
            int sleeping = 0;
            int total = 0;
            for (EntityPlayer entityplayer : world.playerEntities) {
                if (!entityplayer.isSpectator()) {
                    total++;
                    if (VampirePlayer.get(entityplayer).isPlayerFullyAsleep()) {
                        sleeping++;
                    }
                }
            }
            if (sleeping / (float) total * 100 < Configs.coffin_sleep_percentage) return;
            if (world.getGameRules().getBoolean("doDaylightCycle")) {
                long i = world.getWorldInfo().getWorldTime() + 24000L;
                world.getWorldInfo().setWorldTime(i - i % 24000L + 12700L);
            }

            wakeAllPlayers(world);
        }
    }

    /**
     * Wake all sleeping vampire player
     *
     * @param world
     */
    public static void wakeAllPlayers(World world) {
        allPlayersAsleep.put(world.provider.getDimension(), Boolean.FALSE);

        for (EntityPlayer entityplayer : world.playerEntities) {
            VampirePlayer vampirePlayer = VampirePlayer.get(entityplayer);
            if (vampirePlayer.isPlayerSleeping()) {
                vampirePlayer.wakeUpPlayer(false, false, true);
            }
        }

        if (!world.isRemote) {
            world.provider.resetRainAndThunder();
        }
    }
}
