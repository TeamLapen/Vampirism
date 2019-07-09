package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.config.Configs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * TODO integrate with e.g. ForgeEssentials
 */
public class Permissions {
    public static boolean getPermission(String permission) {
        return true;
    }

    public static boolean getPermission(String permission, PlayerEntity player) {
        if ("pvp".equals(permission)) {
            if (!player.getEntityWorld().isRemote) {
                return ServerLifecycleHooks.getCurrentServer().isPVPEnabled();
            }
        }
        return true;
    }

    public static boolean canPlayerTurnPlayer(PlayerEntity player) {
        return Configs.playerCanTurnPlayer;
    }
}
