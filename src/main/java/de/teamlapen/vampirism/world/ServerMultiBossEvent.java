package de.teamlapen.vampirism.world;

import com.google.common.collect.Sets;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.SUpdateMultiBossEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

import java.util.Set;

public class ServerMultiBossEvent extends MultiBossEvent {

    private final Set<ServerPlayer> players = Sets.newHashSet();
    private boolean visible = true;


    public ServerMultiBossEvent(Component nameIn, BossEvent.BossBarOverlay overlayIn, Color... entries) {
        super(Mth.createInsecureUUID(), nameIn, overlayIn, entries);
    }

    public void addPlayer(ServerPlayer player) {
        if (this.players.add(player) && this.visible) {
            VampirismMod.dispatcher.sendTo(new SUpdateMultiBossEventPacket(SUpdateMultiBossEventPacket.OperationType.ADD, this), player);
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.sendUpdate(SUpdateMultiBossEventPacket.OperationType.UPDATE_PROGRESS);
    }

    public Set<ServerPlayer> getPlayers() {
        return players;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;

            for (ServerPlayer player : this.players) {
                VampirismMod.dispatcher.sendTo(new SUpdateMultiBossEventPacket(visible ? SUpdateMultiBossEventPacket.OperationType.ADD : SUpdateMultiBossEventPacket.OperationType.REMOVE, this), player);
            }
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.remove(player) && this.visible) {
            VampirismMod.dispatcher.sendTo(new SUpdateMultiBossEventPacket(SUpdateMultiBossEventPacket.OperationType.REMOVE, this), player);
        }
    }

    @Override
    public void setColors(Color... entries) {
        super.setColors(entries);
        this.sendUpdate(SUpdateMultiBossEventPacket.OperationType.ADD);
    }

    @Override
    public void setName(Component name) {
        super.setName(name);
        this.sendUpdate(SUpdateMultiBossEventPacket.OperationType.UPDATE_NAME);
    }

    @Override
    public void setOverlay(BossEvent.BossBarOverlay overlay) {
        super.setOverlay(overlay);
        this.sendUpdate(SUpdateMultiBossEventPacket.OperationType.UPDATE_STYLE);
    }

    @Override
    public void setPercentage(Color color, float perc) {
        super.setPercentage(color, perc);
        this.sendUpdate(SUpdateMultiBossEventPacket.OperationType.UPDATE_PROGRESS);
    }

    @Override
    public void setPercentage(float... perc) {
        super.setPercentage(perc);
        this.sendUpdate(SUpdateMultiBossEventPacket.OperationType.UPDATE_PROGRESS);
    }

    private void sendUpdate(SUpdateMultiBossEventPacket.OperationType operation) {
        if (this.visible) {
            SUpdateMultiBossEventPacket packet = new SUpdateMultiBossEventPacket(operation, this);

            for (ServerPlayer player : this.players) {
                VampirismMod.dispatcher.sendTo(packet, player);
            }
        }
    }
}
