package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle coffin sleep during the day.
 * This works in the following way:
 * The player is set to sleep by {@link VampirePlayer#trySleep(BlockPos)} which is similar to {@link PlayerEntity#trySleep(BlockPos)}, but works during the day.
 * It uses reflection to modify size and more.
 * This method also sends a sleep packet to the client, which uses the vanilla methods to sleep. Only the sleep gui is replaced by a custom one in the client event handler, when the vanilla gui is open and the player is in a coffin.
 * That GUI sends a  {@link InputEventPacket#WAKEUP} packet to the server.
 * The player should be waked by {@link VampirePlayer#wakeUpPlayer(boolean, boolean, boolean)}, which uses the vanilla wakeup method, but also sets the vampire player variables and updates this class.
 * {@link VampirePlayer} also updates the size every tick to 0.2/0.2 since it is reset by vanilla and sets {@link PlayerEntity#noClip} each tick, so the player does not collide with blocks above the coffin. Because of the we also have to set the motion variables to zero, so the player does no fall.
 * <p>
 * {@link DaySleepHelper#updateAllPlayersSleeping(World)} has to be called every time a player leaves/enters a coffin or leaves/enters the world.
 */
public class DaySleepHelper {
    private static final Map<Integer, Boolean> enoughPlayersAsleep = new HashMap<>();

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
        if (!world.getPlayers().isEmpty()) {
            int spectators = 0;
            int sleeping = 0;
            int all = 0;
            for (PlayerEntity entityplayer : world.getPlayers()) {
                all++;
                if (entityplayer.isSpectator()) {
                    ++spectators;
                } else if (VampirePlayer.get(entityplayer).isPlayerSleeping()) {
                    ++sleeping;
                }
            }
            boolean enough = sleeping > 0 && sleeping / ((float) all - spectators - ignorePlayers) * 100 >= Configs.coffin_sleep_percentage;
            enoughPlayersAsleep.put(world.getDimension().getType().getId(), enough);
        }
    }

    /**
     * Check if all players are fully asleep and if so make it night and wake all players
     *
     * @param world
     */
    public static void checkSleepWorld(World world) {
        if (enoughPlayersAsleep.get(world.getDimension().getType().getId()) == Boolean.TRUE) {
            int sleeping = 0;
            int total = 0;
            for (PlayerEntity entityplayer : world.getPlayers()) {
                if (!entityplayer.isSpectator()) {
                    total++;
                    if (VampirePlayer.get(entityplayer).isPlayerFullyAsleep()) {
                        sleeping++;
                    }
                }
            }
            if (sleeping / (float) total * 100 < Configs.coffin_sleep_percentage) return;
            if (world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
                long i = world.getWorldInfo().getDayTime() + 24000L;
                world.getWorldInfo().setDayTime(i - i % 24000L + 12700L);
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
        enoughPlayersAsleep.put(world.getDimension().getType().getId(), Boolean.FALSE);

        for (PlayerEntity entityplayer : world.getPlayers()) {
            VampirePlayer vampirePlayer = VampirePlayer.get(entityplayer);
            if (vampirePlayer.isPlayerSleeping()) {
                vampirePlayer.wakeUpPlayer(false, false, true);
            }
        }

        if (!world.isRemote) {
            world.getDimension().resetRainAndThunder();
        }
    }
}
