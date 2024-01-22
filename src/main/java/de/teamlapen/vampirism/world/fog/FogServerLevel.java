package de.teamlapen.vampirism.world.fog;

import de.teamlapen.vampirism.network.packet.fog.ClientboundAddFogEmitterPacket;
import de.teamlapen.vampirism.network.packet.fog.ClientboundRemoveFogEmitterPacket;
import de.teamlapen.vampirism.network.packet.fog.ClientboundUpdateFogEmitterPacket;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundAddGarlicEmitterPacket;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundRemoveGarlicEmitterPacket;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundUpdateGarlicEmitterPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;

public class FogServerLevel extends FogLevel {

    private final ServerLevel serverLevel;

    public FogServerLevel(ServerLevel serverLevel) {
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
