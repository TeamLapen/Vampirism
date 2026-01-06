package de.teamlapen.faction.client.proxy;

import de.teamlapen.faction.client.sounds.ClientSoundHandler;
import de.teamlapen.faction.common.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        this.soundHandler = new ClientSoundHandler();
    }

    @Override
    public void sendToServer(CustomPacketPayload packetPayload) {
        Minecraft.getInstance().getConnection().send(packetPayload);
    }

    @Override
    public @Nullable Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }
}
