package de.teamlapen.vampirism.client.network;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.gui.screens.HeritageScreen;
import de.teamlapen.vampirism.client.gui.screens.VampireBookScreen;
import de.teamlapen.vampirism.common.network.packets.client.*;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Set;
import java.util.function.Consumer;

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
                case STOP_MUSIC:
                    Minecraft.getInstance().getMusicManager().stopPlaying();
                    break;
            }
        });
    }

    public static void handleSundamageData(ClientboundSundamagePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> VampirismMod.services().sunDamageRegistry().applyNetworkData(msg));
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

    public static void handleUpdateDimensionPacket(ClientboundUpdateDimensionsPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            final LocalPlayer player = Minecraft.getInstance().player;
            if (player == null)
                return;

            final Set<ResourceKey<Level>> dimensionList = player.connection.levels();
            if (dimensionList == null)
                return;

            Consumer<ResourceKey<Level>> keyConsumer = msg.add()
                    ? dimensionList::add
                    : dimensionList::remove;

            msg.keys().forEach(keyConsumer);
        });
    }

    public static void handleDraculaEventPacket(ClientboundDraculaEventPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> VampirismModClient.services().draculaEventOverlay().handle(msg));
    }

    public static void handleVelmorraCollapsePacket(ClientboundVelmorraCollapsePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> VampirismModClient.services().velmorraCollapseHandler().setProgress(msg.progress()));
    }

    public static void handleHeritagePacket(ClientboundHeritagePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().screen instanceof HeritageScreen screen) {
                screen.setHeritage(msg);
            }
        });
    }
}
