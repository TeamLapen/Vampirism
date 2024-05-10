package de.teamlapen.vampirism.client;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.client.gui.screens.SelectMinionScreen;
import de.teamlapen.vampirism.client.gui.screens.VampireBookScreen;
import de.teamlapen.vampirism.data.ClientSkillTreeData;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.inventory.TaskBoardMenu;
import de.teamlapen.vampirism.inventory.VampirismMenu;
import de.teamlapen.vampirism.network.*;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Set;

public class ClientPayloadHandler {

    public static void handleBossEventSound(ClientboundBossEventSoundPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> VampirismMod.proxy.addBossEventSound(msg.bossEventUuid(), msg.sound()));
    }

    public static void handleVampireBookPacket(ClientboundOpenVampireBookPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> openScreen(new VampireBookScreen(VampireBookManager.getInstance().getBookById(msg.bookId()))));
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
        context.enqueueWork(() -> VampirismModClient.getINSTANCE().getBossInfoOverlay().read(msg));
    }

    private static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    public static void handleSkillTreePacket(ClientboundSkillTreePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientSkillTreeData.init(msg.skillTrees()));
    }
}
