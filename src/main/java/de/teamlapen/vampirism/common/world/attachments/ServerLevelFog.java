package de.teamlapen.vampirism.common.world.attachments;

import de.teamlapen.vampirism.common.network.packets.client.ClientboundAddFogEmitterPacket;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundRemoveFogEmitterPacket;
import de.teamlapen.vampirism.common.network.packets.client.ClientboundUpdateFogEmitterPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class ServerLevelFog extends LevelFog {

    private final ServerLevel serverLevel;

    public ServerLevelFog(ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
    }

    @Override
    protected void notifyChange(Emitter emitter) {
        ClientboundAddFogEmitterPacket packet = new ClientboundAddFogEmitterPacket(emitter);
        for (ServerPlayer player : this.serverLevel.getLevel().players()) {
            player.connection.send(packet);
        }
    }

    @Override
    protected void notifyRemove(BlockPos pos, boolean temp) {
        ClientboundRemoveFogEmitterPacket packet = new ClientboundRemoveFogEmitterPacket(pos, temp);
        for (ServerPlayer player : this.serverLevel.getLevel().players()) {
            player.connection.send(packet);
        }
    }

    @Override
    protected void notifyClear() {
        ClientboundUpdateFogEmitterPacket packet = new ClientboundUpdateFogEmitterPacket(new ArrayList<>(fogAreas.values()), new ArrayList<>(tmpFogAreas.values()));
        for (ServerPlayer player : this.serverLevel.getLevel().players()) {
            player.connection.send(packet);
        }
    }

    @Override
    public void updatePlayer(ServerPlayer player) {
        player.connection.send(new ClientboundUpdateFogEmitterPacket(new ArrayList<>(fogAreas.values()), new ArrayList<>(tmpFogAreas.values())));
    }

}
