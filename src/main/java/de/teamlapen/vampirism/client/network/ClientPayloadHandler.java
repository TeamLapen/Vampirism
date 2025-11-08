package de.teamlapen.vampirism.client.network;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.client.ClientProxy;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.client.data.ClientSkillTreeData;
import de.teamlapen.vampirism.client.gui.screens.SelectMinionScreen;
import de.teamlapen.vampirism.client.gui.screens.VampireBookScreen;
import de.teamlapen.vampirism.common.entity.SundamageRegistry;
import de.teamlapen.vampirism.common.inventory.TaskBoardMenu;
import de.teamlapen.vampirism.common.inventory.VampirismMenu;
import de.teamlapen.vampirism.common.network.packets.client.*;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import de.teamlapen.vampirism.common.world.attachments.LevelGarlic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Set;

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

    public static void handleRequestMinionSelectPacket(ClientboundRequestMinionSelectPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> openScreen(new SelectMinionScreen(msg.action(), msg.minions())));
    }

    public static void handleSundamageData(ClientboundSundamagePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ((SundamageRegistry) VampirismAPI.sundamageRegistry()).applyNetworkData(msg));
    }

    public static void handleTaskPacket(ClientboundTaskPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            AbstractContainerMenu container = context.player().containerMenu;
            if (msg.containerId() == container.containerId && container instanceof VampirismMenu) {
                ((VampirismMenu) container).init(msg.taskWrappers(), msg.completableTasks(), msg.completedRequirements());
            }
        });
    }

    public static void handleTaskStatusPacket(ClientboundTaskStatusPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            AbstractContainerMenu container = context.player().containerMenu;
            if (msg.containerId() == container.containerId && container instanceof TaskBoardMenu) {
                ((TaskBoardMenu) container).init((Set<ITaskInstance>) msg.available(), msg.completableTasks(), msg.completedRequirements(), msg.taskBoardId());
            }
        });
    }

    public static void handleUpdateMultiBossInfoPacket(ClientboundUpdateMultiBossEventPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> VampirismModClient.getServices().bossInfoOverlay().read(msg));
    }

    private static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    public static void handleSkillTreePacket(ClientboundSkillTreePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientSkillTreeData.init(msg.skillTrees()));
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

    public static void handlePlaySoundEventPacket(ClientboundPlaySoundEventPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            SimpleSoundInstance simpleSoundInstance = SimpleSoundInstance.forAmbientAddition(msg.soundEvent().value());
            Minecraft.getInstance().getSoundManager().play(simpleSoundInstance);
            context.player().level().playLocalSound(context.player(), msg.soundEvent().value(), SoundSource.AMBIENT, 1,1);
        });
    }

    public static void handleRecipesPacket(ClientboundRecipesPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ((ClientProxy) VampirismMod.proxy).setRecipes(msg.recipes());
        });
    }
}
