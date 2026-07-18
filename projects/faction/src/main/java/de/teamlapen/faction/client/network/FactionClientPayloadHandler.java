package de.teamlapen.faction.client.network;

import de.teamlapen.faction.FactionsMod;
import de.teamlapen.faction.client.FactionsClientMod;
import de.teamlapen.faction.common.config.FactionConfig;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.factions.skills.ClientSkillTreeData;
import de.teamlapen.faction.common.factions.skills.ClientboundSkillTreePacket;
import de.teamlapen.faction.common.network.packets.client.*;
import de.teamlapen.faction.common.network.packets.server.ClientboundActionBindingPacket;
import de.teamlapen.faction.common.network.packets.server.ServerboundSelectMinionTaskPacket;
import de.teamlapen.faction.common.world.IEventReceiver;
import de.teamlapen.faction.common.world.inventory.FactionMenu;
import de.teamlapen.faction.common.world.inventory.TaskBoardMenu;
import de.teamlapen.gui.components.IComponentWithAction;
import de.teamlapen.gui.screens.SelectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class FactionClientPayloadHandler {

    public static void handleTaskStatusPacket(ClientboundTaskStatusPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            AbstractContainerMenu container = context.player().containerMenu;
            if (msg.containerId() == container.containerId && container instanceof TaskBoardMenu) {
                ((TaskBoardMenu) container).init(msg.available(), msg.completableTasks(), msg.completedRequirements(), msg.taskBoardId());
            }
        });
    }

    public static void handleSkillTreePacket(ClientboundSkillTreePacket msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientSkillTreeData.init(msg.skillTrees()));
    }

    public static void handleRequestMinionSelectPacket(ClientboundRequestMinionSelectPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> openScreen(new SelectionScreen(Component.translatable("gui.factionapi.select_minion"), msg.minions().stream().map(x -> IComponentWithAction.of(x.getSecond(), () -> {
            FactionsMod.proxy.sendToServer(new ServerboundSelectMinionTaskPacket(x.getFirst(), ServerboundSelectMinionTaskPacket.RECALL));
        })).toList())));
    }

    public static void handleTaskPacket(ClientboundTaskPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            AbstractContainerMenu container = context.player().containerMenu;
            if (msg.containerId() == container.containerId && container instanceof FactionMenu) {
                ((FactionMenu) container).init(msg.taskWrappers(), msg.completableTasks(), msg.completedRequirements());
            }
        });
    }

    public static void handleUpdateMultiBossInfoPacket(ClientboundUpdateMultiBossEventPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> FactionsClientMod.services().bossInfoOverlay().read(msg));
    }

    public static void handlePlaySoundEventPacket(ClientboundPlaySoundEventPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            SimpleSoundInstance simpleSoundInstance = SimpleSoundInstance.forAmbientAddition(msg.soundEvent().value());
            Minecraft.getInstance().getSoundManager().play(simpleSoundInstance);
            context.player().level().playLocalSound(context.player(), msg.soundEvent().value(), SoundSource.AMBIENT, 1,1);
        });
    }

    private static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    public static void handleActionBindingPacket(ClientboundActionBindingPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = FactionPlayerHandler.get(context.player());
            FactionConfig.preferences().actionBindings().update(player.getFaction(), msg.actionBindingId(), msg.action());
        });
    }

    public static void handleEventPacket(ClientboundEventPacket msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(msg.entityId());
            if (entity instanceof IEventReceiver receiver) {
                receiver.onEvent(msg.eventId());
            }
        });
    }
}
