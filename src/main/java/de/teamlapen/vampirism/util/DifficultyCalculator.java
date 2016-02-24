package de.teamlapen.vampirism.util;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.difficulty.Difficulty;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Max on 22.02.2016.
 */
public class DifficultyCalculator {

    /**
     * Can be null if no players are found
     *
     * @param playerList
     * @return a difficulty level based on the given player's faction levels
     */
    private static
    @Nullable
    Difficulty calculateDifficulty(List<? extends EntityPlayer> playerList) {
        if (playerList == null || playerList.isEmpty()) return null;
        int min = Integer.MAX_VALUE;
        int max = 0;
        int sum = 0;
        for (EntityPlayer p : playerList) {
            IFactionPlayerHandler handler = FactionPlayerHandler.get(p);
            if (handler.getCurrentLevel() == 0) {
                min = 0;
                continue;
            }
            int level = (int) (handler.getCurrentLevel() / (float) handler.getCurrentFaction().getHighestReachableLevel() * 100F);
            if (level < min) {
                min = level;
            }
            if (level > max) {
                max = level;
            }
            sum += max;
        }
        return new Difficulty(min, max, Math.round(sum / (float) playerList.size()));
    }

    /**
     * Can be null if no players are found
     *
     * @param w
     * @return A difficulty level based on the world's player's faction levels
     */
    public static Difficulty getWorldDifficulty(World w) {
        return calculateDifficulty(w.playerEntities);
    }

    /**
     * Can be null if no players are found
     *
     * @return A difficulty level based on the faction level of all players in the specified area
     */
    public static Difficulty getLocalDifficulty(World w, BlockPos center, int radius) {

        List<EntityPlayer> list = w.getEntitiesWithinAABB(EntityPlayer.class, UtilLib.createBB(center, radius, true));
        return calculateDifficulty(list);
    }

    /**
     * Can be null if no players are found
     * ONLY CALL THIS SERVER SIDE
     *
     * @return A difficulty level based on the server's player's faction levels
     */
    public static Difficulty getGlobalDifficulty() {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            throw new IllegalStateException("You can only use this method on server side");
        }
        return calculateDifficulty(MinecraftServer.getServer().getConfigurationManager().playerEntityList);
    }

    /**
     * Tries to find a difficulty for the given position, by checking local area, world and then sever. If none returns a difficulty a zero difficulty is returned.
     * ONLY CALLED SERVER SIDE
     *
     * @param world
     * @param pos
     * @param radius
     * @return
     */
    public static
    @Nonnull
    Difficulty findDifficultyForPos(World world, BlockPos pos, int radius) {
        Difficulty d = getLocalDifficulty(world, pos, radius);
        if (d == null) {
            d = getWorldDifficulty(world);
            if (d == null) {
                d = getGlobalDifficulty();
                if (d == null) {
                    d = new Difficulty(0, 0, 0);
                }
            }
        }
        return d;
    }
}
