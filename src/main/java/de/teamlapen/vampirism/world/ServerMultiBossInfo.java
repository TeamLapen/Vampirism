package de.teamlapen.vampirism.world;

import com.google.common.collect.Sets;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.UpdateMultiBossInfoPacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

import java.util.Set;

public class ServerMultiBossInfo extends MultiBossInfo {

    private final Set<ServerPlayerEntity> players = Sets.newHashSet();
    private boolean visible = true;


    public ServerMultiBossInfo(ITextComponent nameIn, BossInfo.Overlay overlayIn, MultiBossInfo.Entry... entries) {
        super(MathHelper.getRandomUUID(), nameIn, overlayIn, entries);
    }

    public Set<ServerPlayerEntity> getPlayers() {
        return players;
    }

    @Override
    public void setPercentage(ResourceLocation id, float perc) {
        super.setPercentage(id, perc);
        this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PCT);
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
    public void setEntries(Entry... entry) {
        super.setEntries(entry);
        this.sendUpdate(SUpdateBossInfoPacket.Operation.UPDATE_PCT);
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
}
