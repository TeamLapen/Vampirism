package de.teamlapen.library.common.network;

import de.teamlapen.library.common.network.packets.PlayerOwnedBlockEntityLockPacket;
import de.teamlapen.library.common.world.inventory.PlayerOwnedMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class LibraryPayloadHandler {
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
