package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class Permissions {
    public static final String VAMPIRISM = "vampirism.check";
    public static final String BITE_PLAYER = "vampirism.bite.attack.player";
    public static final String BITE = "vampirism.bite.attack";
    public static final String FEED = "vampirism.bite.feed";
    public static final String FEED_PLAYER = "vampirism.bite.feed.player";
    public static final String INFECT_PLAYER = "vampirism.infect.player";
    public static final String ACTION_PREFIX = "vampirism.action.";

    public static void init() {
        PermissionAPI.registerNode(VAMPIRISM, DefaultPermissionLevel.ALL, "Used to check if permission system works");
        PermissionAPI.registerNode(BITE_PLAYER, DefaultPermissionLevel.ALL, "Allow players to bite attack players");
        PermissionAPI.registerNode(BITE, DefaultPermissionLevel.ALL, "Allow players to bite attack creatures");
        PermissionAPI.registerNode(FEED, DefaultPermissionLevel.ALL, "Allow feeding");
        PermissionAPI.registerNode(FEED_PLAYER, DefaultPermissionLevel.ALL, "Allow feeding from players");
        PermissionAPI.registerNode(INFECT_PLAYER, DefaultPermissionLevel.ALL, "Allow players to infect other players");
        for (IAction action : ModRegistries.ACTIONS.getValues()) {
            PermissionAPI.registerNode(ACTION_PREFIX + action.getRegistryName().getNamespace() + "." + action.getRegistryName().getPath(), DefaultPermissionLevel.ALL, new StringTextComponent("Use action ").appendSibling(new TranslationTextComponent(action.getTranslationKey())).getFormattedText());
        }
    }

    public static boolean isPvpEnabled(PlayerEntity player) {
        if (!player.getEntityWorld().isRemote) {
            return ServerLifecycleHooks.getCurrentServer().isPVPEnabled();
        }
        return true;
    }


}
