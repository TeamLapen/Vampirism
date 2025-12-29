package de.teamlapen.factions.common.proxy;

import de.teamlapen.factions.common.sounds.ISoundHandler;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface IProxy {

    @Nullable
    Player getClientPlayer();

    ISoundHandler getSoundHandler();

    default void sendToServer(CustomPacketPayload packetPayload) {

    }
}
