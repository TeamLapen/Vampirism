package de.teamlapen.vampirism.client;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.client.gui.screens.SelectMinionScreen;
import de.teamlapen.vampirism.client.gui.screens.VampireBookScreen;
import de.teamlapen.vampirism.client.renderer.VampirismClientEntityRegistry;
import de.teamlapen.vampirism.data.ClientSkillTreeData;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.inventory.TaskBoardMenu;
import de.teamlapen.vampirism.inventory.VampirismMenu;
import de.teamlapen.vampirism.network.*;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ClientPayloadHandler {

    public static void handleBloodValues(ClientboundBloodValuePacket msg, ConfigurationPayloadContext context) {
        context.workHandler().execute(() ->
        {
            ((VampirismClientEntityRegistry) VampirismAPI.entityRegistry()).applyDataConvertibleOverlays((Map<EntityType<? extends PathfinderMob>, ResourceLocation>) (Object) msg.convertibleOverlay());
            Map<ResourceLocation, Float> entities = msg.getValues()[0];
            ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).applyNewResources(entities);
            BloodConversionRegistry.applyNewEntitiesResources(entities);
            BloodConversionRegistry.applyNewItemResources(msg.getValues()[1]);
            BloodConversionRegistry.applyNewFluidResources(msg.getValues()[2]);
        });
        context.replyHandler().send(new ServerboundBloodValuesCompletedPacket());
    }

    public static void handleBossEventSound(ClientboundBossEventSoundPacket msg, PlayPayloadContext context) {
        context.workHandler().execute(() -> VampirismMod.proxy.addBossEventSound(msg.bossEventUuid(), msg.sound()));
    }

    public static void handleVampireBookPacket(ClientboundOpenVampireBookPacket msg, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            openScreen(new VampireBookScreen(VampireBookManager.getInstance().getBookById(msg.bookId())));
        });
    }

    public static void handlePlayEventPacket(ClientboundPlayEventPacket msg, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            if (msg.type() == 1) {
                VampirismMod.proxy.spawnParticles(Minecraft.getInstance().level, msg.pos(), Block.stateById(msg.stateId()));
            }
            else if(msg.type() == 2){
                Minecraft.getInstance().getMusicManager().stopPlaying();
            }
        });
    }

    public static void handleRequestMinionSelectPacket(ClientboundRequestMinionSelectPacket msg, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            openScreen(new SelectMinionScreen(msg.action(), msg.minions()));
        });
    }

    public static void handleSundamageData(ClientboundSundamagePacket msg, IPayloadContext context) {
        context.workHandler().execute(() -> ((SundamageRegistry) VampirismAPI.sundamageRegistry()).applyNetworkData(msg));
    }

    public static void handleTaskPacket(ClientboundTaskPacket msg, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
            if (msg.containerId() == container.containerId && container instanceof VampirismMenu) {
                ((VampirismMenu) container).init(msg.taskWrappers(), msg.completableTasks(), msg.completedRequirements());
            }
        });
    }

    public static void handleTaskStatusPacket(ClientboundTaskStatusPacket msg, PlayPayloadContext context) {
        context.workHandler().execute(() -> {
            AbstractContainerMenu container = Objects.requireNonNull(Minecraft.getInstance().player).containerMenu;
            if (msg.containerId() == container.containerId && container instanceof TaskBoardMenu) {
                ((TaskBoardMenu) container).init((Set<ITaskInstance>) msg.available(), msg.completableTasks(), msg.completedRequirements(), msg.taskBoardId());
            }
        });
    }

    public static void handleUpdateMultiBossInfoPacket(ClientboundUpdateMultiBossEventPacket msg, PlayPayloadContext context) {
        context.workHandler().execute(() -> VampirismMod.proxy.handleUpdateMultiBossInfoPacket(msg));
    }

    private static void openScreen(Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }

    public static void handleSkillTreePacket(ClientboundSkillTreePacket msg, IPayloadContext context) {
        context.workHandler().execute(() -> ClientSkillTreeData.init(msg.skillTrees()));
    }
}
