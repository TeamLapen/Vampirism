package de.teamlapen.vampirism.server.world.extensions;

import de.teamlapen.vampirism.common.network.packets.client.ClientboundAddGarlicEmitterPacket;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundRemoveGarlicEmitterPacket;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundUpdateGarlicEmitterPacket;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class ServerLevelGarlic extends LevelGarlic {

    private final ServerLevel serverLevel;

    public ServerLevelGarlic(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
    }

    @Override
    protected void notifyChange(Emitter emitter) {
        ClientboundAddGarlicEmitterPacket packet = new ClientboundAddGarlicEmitterPacket(emitter);
        for (ServerPlayer player : this.serverLevel.getLevel().players()) {
            player.connection.send(packet);
        }
    }

    @Override
    protected void notifyRemove(int emitter) {
        ClientboundRemoveGarlicEmitterPacket packet = new ClientboundRemoveGarlicEmitterPacket(emitter);
        for (ServerPlayer player : this.serverLevel.getLevel().players()) {
            player.connection.send(packet);
        }
    }

    @Override
    protected void notifyClear() {
        ClientboundUpdateGarlicEmitterPacket packet = new ClientboundUpdateGarlicEmitterPacket(new ArrayList<>(emitterHashMap.values()));
        for (ServerPlayer player : this.serverLevel.getLevel().players()) {
            player.connection.send(packet);
        }
    }

    @Override
    public void updatePlayer(ServerPlayer player) {
        player.connection.send(new ClientboundUpdateGarlicEmitterPacket(new ArrayList<>(emitterHashMap.values())));
    }
}
