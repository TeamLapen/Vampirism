package de.teamlapen.vampirism.modcompat;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Helper for compatibility with PlayerRevive https://www.curseforge.com/minecraft/mc-mods/playerrevive
 * <p>
 * The down time from PlayerRevive waiting for a revive is counted towards the DBNO timeout before the player can resurrect.
 * Using these methods: https://github.com/CreativeMD/PlayerRevive/blob/5a2fae4075a7fdfe3452f18b4939b6d21372542e/src/main/java/team/creative/playerrevive/server/PlayerReviveServer.java#L23-L33
 * Based on https://github.com/CreativeMD/PlayerRevive/issues/97#issuecomment-1255953836
 *
 */
public class PlayerReviveHelper {

    private static Method m_getDownedTime;
    private static boolean use_player_revive_time = true;

    public static int getPreviousDownTime(Player player){
        if (use_player_revive_time && !player.level().isClientSide()) {
            if (!ModList.get().isLoaded("playerrevive")) {
                use_player_revive_time = false;
            } else {
                if (m_getDownedTime == null) {
                    try {
                        Class<?> clazz = Class.forName("team.creative.playerrevive.server.PlayerReviveServer");
                        m_getDownedTime = clazz.getMethod("downedTime", Player.class);

                    } catch (ClassNotFoundException e) {
                        LogManager.getLogger().error("Failed to find PlayerReviveServer class for compatibility even though PlayerRevive is installed", e);
                        use_player_revive_time = false;
                    } catch (NoSuchMethodException e) {
                        LogManager.getLogger().error("Failed to find downedTime method from PlayerReviveServer class for compatibility even though PlayerRevive is installed", e);
                        use_player_revive_time = false;
                    }
                }
                if(m_getDownedTime != null){
                    try {
                        Object result = m_getDownedTime.invoke(null, player);
                        return (Integer) result;
                    } catch (IllegalArgumentException|IllegalAccessException|InvocationTargetException|ClassCastException e) {
                        LogManager.getLogger().error("Failed to obtain downed time from PlayerRevive method for compatibility", e);
                        throw new RuntimeException(e);
                    }
                }
            }

        }
        return 0;
    }
}
