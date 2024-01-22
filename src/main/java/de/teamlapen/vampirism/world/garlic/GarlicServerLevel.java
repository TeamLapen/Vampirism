package de.teamlapen.vampirism.world.garlic;

import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundAddGarlicEmitterPacket;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundRemoveGarlicEmitterPacket;
import de.teamlapen.vampirism.network.packet.garlic.ClientboundUpdateGarlicEmitterPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GarlicServerLevel extends GarlicLevel {

    private final ServerLevel serverLevel;

    public GarlicServerLevel(ServerLevel serverLevel) {
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
