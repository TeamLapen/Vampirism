package de.teamlapen.vampirism.world;

import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.UpdateMultiBossInfoPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;

import java.awt.*;
import java.util.Set;

public class ServerMultiBossInfo extends MultiBossInfo {

    private final Set<ServerPlayer> players = Sets.newHashSet();
    private boolean visible = true;


    public ServerMultiBossInfo(Component nameIn, BossEvent.BossBarOverlay overlayIn, Color... entries) {
        super(Mth.createInsecureUUID(), nameIn, overlayIn, entries);
    }

    public void addPlayer(ServerPlayer player) {
        if (this.players.add(player) && this.visible) {
            VampirismMod.dispatcher.sendTo(new UpdateMultiBossInfoPacket(UpdateMultiBossInfoPacket.OperationType.ADD, this), player);
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.sendUpdate(UpdateMultiBossInfoPacket.OperationType.UPDATE_PROGRESS);
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
                VampirismMod.dispatcher.sendTo(new UpdateMultiBossInfoPacket(visible ? UpdateMultiBossInfoPacket.OperationType.ADD : UpdateMultiBossInfoPacket.OperationType.REMOVE, this), player);
            }
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.remove(player) && this.visible) {
            VampirismMod.dispatcher.sendTo(new UpdateMultiBossInfoPacket(UpdateMultiBossInfoPacket.OperationType.REMOVE, this), player);
        }
    }

    @Override
    public void setColors(Color... entries) {
        super.setColors(entries);
        this.sendUpdate(UpdateMultiBossInfoPacket.OperationType.ADD);
    }

    @Override
    public void setName(Component name) {
        super.setName(name);
        this.sendUpdate(UpdateMultiBossInfoPacket.OperationType.UPDATE_NAME);
    }

    @Override
    public void setOverlay(BossEvent.BossBarOverlay overlay) {
        super.setOverlay(overlay);
        this.sendUpdate(UpdateMultiBossInfoPacket.OperationType.UPDATE_STYLE);
    }

    @Override
    public void setPercentage(Color color, float perc) {
        super.setPercentage(color, perc);
        this.sendUpdate(UpdateMultiBossInfoPacket.OperationType.UPDATE_PROGRESS);
    }

    @Override
    public void setPercentage(float... perc) {
        super.setPercentage(perc);
        this.sendUpdate(UpdateMultiBossInfoPacket.OperationType.UPDATE_PROGRESS);
    }

    private void sendUpdate(UpdateMultiBossInfoPacket.OperationType operation) {
        if (this.visible) {
            UpdateMultiBossInfoPacket packet = new UpdateMultiBossInfoPacket(operation, this);

            for (ServerPlayer player : this.players) {
                VampirismMod.dispatcher.sendTo(packet, player);
            }
        }
    }
}
