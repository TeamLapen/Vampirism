package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.config.Configs;
import net.minecraft.entity.player.EntityPlayer;

/**
 * TODO integrate with e.g. ForgeEssentials
 */
public class Permissions {
    public static boolean getPermission(String permission) {
        return true;
    }

    public static boolean getPermission(String permission, EntityPlayer player) {
        if ("pvp".equals(permission)) {
            if (!player.getEntityWorld().isRemote) {
                return ServerLifecycleHooks.getCurrentServer().isPVPEnabled();
            }
        }
        return true;
    }

    public static boolean canPlayerTurnPlayer(EntityPlayer player) {
        return Configs.playerCanTurnPlayer;
    }
}
