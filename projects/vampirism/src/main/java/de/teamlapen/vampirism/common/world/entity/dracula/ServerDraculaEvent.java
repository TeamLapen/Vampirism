package de.teamlapen.vampirism.common.world.entity.dracula;

import de.teamlapen.vampirism.common.network.packets.client.ClientboundDraculaEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.HashSet;
import java.util.Set;

public class ServerDraculaEvent extends DraculaEvent {

    private final Set<ServerPlayer> players = new HashSet<>();
    protected boolean isVisible;

    public ServerDraculaEvent(float percentage, FightStage stage, boolean inVulnerable) {
        super(Mth.createInsecureUUID(), percentage, stage, inVulnerable);
    }

    public Set<ServerPlayer> getPlayers() {
        return this.players;
    }

    public void update(Dracula dracula) {
        setStage(dracula.getStage());
        setInVulnerable(dracula.isInvulnerable());
        setPercentage(dracula.getHealth() / dracula.getMaxHealth());
        if (this.isVisible) {
            sendUpdate(ClientboundDraculaEventPacket.OperationType.UPDATE);
        } else {
            setVisible(true);
        }
    }

    public void clear() {
        var packet = new ClientboundDraculaEventPacket(new ClientboundDraculaEventPacket.RemoveOperation(this));
        for (ServerPlayer player : this.players) {
            player.connection.send(packet);
        }
        this.players.clear();
    }

    public void addPlayer(ServerPlayer player) {

        if (this.players.add(player) && this.isVisible) {
            player.connection.send(new ClientboundDraculaEventPacket(new ClientboundDraculaEventPacket.AddOperation(this)));
        }
    }

    public void removePlayer(ServerPlayer player) {
        if (this.players.remove(player)) {
            player.connection.send(new ClientboundDraculaEventPacket(new ClientboundDraculaEventPacket.RemoveOperation(this)));
        }
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        for (ServerPlayer player : this.players) {
            player.connection.send(new ClientboundDraculaEventPacket(visible ? new ClientboundDraculaEventPacket.AddOperation(this) : new ClientboundDraculaEventPacket.RemoveOperation(this)));
        }
    }

    @Override
    public void setPercentage(float percentage) {
        if (percentage != this.getPercentage()) {
            super.setPercentage(percentage);
            sendUpdate(ClientboundDraculaEventPacket.OperationType.UPDATE_PROGRESS);
        }
    }

    @Override
    public void setStage(FightStage stage) {
        if (stage != this.getStage()) {
            super.setStage(stage);
            sendUpdate(ClientboundDraculaEventPacket.OperationType.UPDATE_STAGE);
        }
    }

    @Override
    public void setInVulnerable(boolean inVulnerable) {
        if (inVulnerable != this.isInVulnerable()) {
            super.setInVulnerable(inVulnerable);
            sendUpdate(ClientboundDraculaEventPacket.OperationType.UPDATE_INVULNERABLE);
        }
    }

    private void sendUpdate(ClientboundDraculaEventPacket.OperationType operation) {
        if (this.isVisible) {
            var param = switch (operation) {
                case UPDATE_PROGRESS -> new ClientboundDraculaEventPacket.UpdateProgressOperation(this);
                case UPDATE_STAGE -> new ClientboundDraculaEventPacket.UpdateStageOperation(this);
                case UPDATE_INVULNERABLE -> new ClientboundDraculaEventPacket.UpdateVulnerableOperation(this);
                case UPDATE -> new ClientboundDraculaEventPacket.UpdateOperation(this);
                case ADD -> new ClientboundDraculaEventPacket.AddOperation(this);
                case REMOVE -> new ClientboundDraculaEventPacket.RemoveOperation(this);
            };

            var packet = new ClientboundDraculaEventPacket(param);

            for (ServerPlayer player : this.players) {
                player.connection.send(packet);
            }

        }
    }

}
