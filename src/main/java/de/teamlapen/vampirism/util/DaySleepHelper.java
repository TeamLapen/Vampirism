package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.network.InputEventPacket;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

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
 *     Unlike the vanilla world we do not keep track of sleeping players as we do not have access to the necessary information/events but we just frequently check if enough palyers are sleeping
 *
 *
 *     TODO check if https://github.com/MinecraftForge/MinecraftForge/pull/5013 helps in any way
 */
public class DaySleepHelper {


    /**
     * Check if all players are fully asleep and if so make it night and wake all players
     *
     * @param world
     */
    public static void checkSleepWorld(World world) {
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
            if (sleeping / (float) total * 100 < VampirismConfig.SERVER.coffinSleepPercentage.get()) return;

        if (world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)) {
            long l = world.getDayTime() + 24000L;
            world.setDayTime(l - l % 24000L);
        }

        world.getPlayers().stream().map(VampirePlayer::get).filter(VampirePlayer::isPlayerSleeping).forEach((player) -> {
            player.wakeUpPlayer(false, false, true);
        });
        if (world.getGameRules().getBoolean(GameRules.DO_WEATHER_CYCLE)) {
            world.getDimension().resetRainAndThunder();
        }


    }


}
