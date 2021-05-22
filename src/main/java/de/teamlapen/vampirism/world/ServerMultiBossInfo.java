package de.teamlapen.vampirism.world;

import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.UpdateMultiBossInfoPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

import java.awt.*;
import java.util.Set;

public class ServerMultiBossInfo extends MultiBossInfo {

    private final Set<ServerPlayerEntity> players = Sets.newHashSet();
    private boolean visible = true;


    public ServerMultiBossInfo(ITextComponent nameIn, BossInfo.Overlay overlayIn, Color... entries) {
        super(MathHelper.getRandomUUID(), nameIn, overlayIn, entries);
    }

    public Set<ServerPlayerEntity> getPlayers() {
        return players;
    }

    @Override
    public void setPercentage(Color color, float perc) {
        super.setPercentage(color, perc);
        this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PCT);
    }

    @Override
    public void clear() {
        super.clear();
        this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PCT);
    }

    @Override
    public void setPercentage(float... perc) {
        super.setPercentage(perc);
        this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PCT);
    }

    @Override
    public void setOverlay(BossInfo.Overlay overlay) {
        super.setOverlay(overlay);
        this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_STYLE);
    }

    public void addPlayer(ServerPlayerEntity player){
        if (this.players.add(player) && this.visible) {
            VampirismMod.dispatcher.sendTo(new UpdateMultiBossInfoPacket(SUpdateBossInfoPacket.Operation.ADD, this), player);
        }
    }

    public void removePlayer(ServerPlayerEntity player){
        if (this.players.remove(player) && this.visible) {
            VampirismMod.dispatcher.sendTo(new UpdateMultiBossInfoPacket(SUpdateBossInfoPacket.Operation.REMOVE, this), player);
        }
    }

    @Override
    public void setColors(Color... entries) {
        super.setColors(entries);
        this.sendUpdate(SUpdateBossInfoPacket.Operation.ADD);
    }

    @Override
    public void setName(ITextComponent name) {
        super.setName(name);
        this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_NAME);
    }

    private void sendUpdate(SUpdateBossInfoPacket.Operation operation) {
        if(this.visible) {
            UpdateMultiBossInfoPacket packet = new UpdateMultiBossInfoPacket(operation, this);

            for (ServerPlayerEntity player : this.players) {
                VampirismMod.dispatcher.sendTo(packet, player);
            }
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;

            for (ServerPlayerEntity player : this.players) {
                VampirismMod.dispatcher.sendTo(new UpdateMultiBossInfoPacket(visible? SUpdateBossInfoPacket.Operation.ADD: SUpdateBossInfoPacket.Operation.REMOVE, this), player);
            }
        }
    }
}
