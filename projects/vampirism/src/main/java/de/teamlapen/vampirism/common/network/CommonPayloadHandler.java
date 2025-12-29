package de.teamlapen.vampirism.common.network;

import de.teamlapen.vampirism.common.network.packets.common.PlayerOwnedBlockEntityLockPacket;
import de.teamlapen.vampirism.common.world.inventory.base.PlayerOwnedMenu;
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
