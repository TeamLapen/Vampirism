package de.teamlapen.vampirism.common;

import de.teamlapen.vampirism.inventory.diffuser.PlayerOwnedMenu;
import de.teamlapen.vampirism.network.PlayerOwnedBlockEntityLockPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CommonPayloadHandler {

    public static void handlePlayerOwnedBlockEntityLockPacket(PlayerOwnedBlockEntityLockPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof PlayerOwnedMenu menu && context.player().containerMenu.containerId == msg.menuId()) {
                menu.setLockStatus(msg.lockData().getLockStatus());
                if (context.flow().isServerbound()) {
                    context.reply(menu.updatePackage());
                }
            }
        });
    }
}
