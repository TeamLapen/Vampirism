package de.teamlapen.vampirism.client.network;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.client.gui.screens.VampireBookScreen;
import de.teamlapen.vampirism.common.entity.SundamageRegistry;
import de.teamlapen.vampirism.common.network.packets.client.*;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {

    public static void handleBossEventSound(ClientboundBossEventSoundPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> VampirismMod.proxy.addBossEventSound(msg.bossEventUuid(), msg.sound()));
    }

    public static void handleVampireBookPacket(ClientboundOpenVampireBookPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> openScreen(new VampireBookScreen(msg.vampireBook())));
    }

    public static void handlePlayEventPacket(ClientboundPlayEventPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            switch (msg.event()) {
                case 1:
                    VampirismMod.proxy.spawnParticles(Minecraft.getInstance().level, msg.pos(), Block.stateById(msg.stateId()));
                    break;
                case 2:
                    Minecraft.getInstance().getMusicManager().stopPlaying();
                    break;
            }
        });
    }

    public static void handleSundamageData(ClientboundSundamagePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ((SundamageRegistry) VampirismAPI.sundamageRegistry()).applyNetworkData(msg));
    }

    private static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    public static void handleRemoveGarlicEmitterPacket(ClientboundRemoveGarlicEmitterPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> LevelGarlic.get(context.player().level()).removeGarlicBlock(msg.emitterId()));
    }

    public static void handleAddGarlicEmitterPacket(ClientboundAddGarlicEmitterPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> LevelGarlic.get(context.player().level()).registerGarlicBlock(msg.emitter().strength(), msg.emitter().pos()));
    }

    public static void handleUpdateGarlicEmitterPacket(ClientboundUpdateGarlicEmitterPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> LevelGarlic.get(context.player().level()).fill(msg.emitters()));
    }

    public static void handleUpdateFogEmitterPacket(ClientboundUpdateFogEmitterPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> LevelFog.get(context.player().level()).fill(msg.emitters(), msg.emittersTmp()));
    }

    public static void handleAddFogEmitterPacket(ClientboundAddFogEmitterPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> LevelFog.get(context.player().level()).add(msg.emitter()));
    }

    public static void handleRemoveFogEmitterPacket(ClientboundRemoveFogEmitterPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> LevelFog.get(context.player().level()).remove(msg.position(), msg.tmp()));
    }

}
