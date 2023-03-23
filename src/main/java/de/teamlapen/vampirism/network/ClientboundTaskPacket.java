package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.player.TaskManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public record ClientboundTaskPacket(int containerId,
                                    Map<UUID, TaskManager.TaskWrapper> taskWrappers,
                                    Map<UUID, Set<UUID>> completableTasks,
                                    Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements) implements IMessage.IClientBoundMessage {

    public static void encode(@NotNull ClientboundTaskPacket msg, @NotNull FriendlyByteBuf buffer) {
        buffer.writeVarInt(msg.containerId);
        buffer.writeVarInt(msg.completableTasks.size());
        buffer.writeVarInt(msg.completedRequirements.size());
        buffer.writeVarInt(msg.taskWrappers.size());
        msg.completableTasks.forEach((uuid, tasks) -> {
            buffer.writeUUID(uuid);
            buffer.writeVarInt(tasks.size());
            tasks.forEach(buffer::writeUUID);
        });
        msg.completedRequirements.forEach(((uuid, taskMapMap) -> {
            buffer.writeUUID(uuid);
            buffer.writeVarInt(taskMapMap.size());
            taskMapMap.forEach((task, data) -> {
                buffer.writeUUID(task);
                buffer.writeVarInt(data.size());
                data.forEach((loc, val) -> {
                    buffer.writeResourceLocation(loc);
                    buffer.writeVarInt(val);
                });
            });
        }));
        msg.taskWrappers.forEach((id, taskWrapper) -> taskWrapper.encode(buffer));
    }

    public static @NotNull ClientboundTaskPacket decode(@NotNull FriendlyByteBuf buffer) {
        int containerId = buffer.readVarInt();
        int completableSize = buffer.readVarInt();
        int statSize = buffer.readVarInt();
        int taskWrapperSIze = buffer.readVarInt();
        Map<UUID, Set<UUID>> completableTasks = new HashMap<>();
        for (int i = 0; i < completableSize; i++) {
            UUID uuid = buffer.readUUID();
            Set<UUID> task = new HashSet<>();
            int taskSize = buffer.readVarInt();
            for (int i1 = 0; i1 < taskSize; i1++) {
                task.add(buffer.readUUID());
            }
            completableTasks.put(uuid, task);
        }
        Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements = new HashMap<>();
        for (int i = 0; i < statSize; i++) {
            UUID uuid = buffer.readUUID();
            Map<UUID, Map<ResourceLocation, Integer>> taskRequirements = new HashMap<>();
            int taskRequirementSize = buffer.readVarInt();
            for (int i1 = 0; i1 < taskRequirementSize; i1++) {
                UUID task = buffer.readUUID();
                int requirementSize = buffer.readVarInt();
                Map<ResourceLocation, Integer> requirements = new HashMap<>();
                for (int i2 = 0; i2 < requirementSize; i2++) {
                    requirements.put(buffer.readResourceLocation(), buffer.readVarInt());
                }
                taskRequirements.put(task, requirements);
            }
            completedRequirements.put(uuid, taskRequirements);
        }
        Map<UUID, TaskManager.TaskWrapper> taskWrapper = new HashMap<>();
        for (int i = 0; i < taskWrapperSIze; i++) {
            TaskManager.TaskWrapper wrapper = buffer.readJsonWithCodec(TaskManager.TaskWrapper.CODEC);
            taskWrapper.put(wrapper.getId(), wrapper);
        }
        return new ClientboundTaskPacket(containerId, taskWrapper, completableTasks, completedRequirements);
    }

    public static void handle(final ClientboundTaskPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleTaskPacket(msg));
        ctx.setPacketHandled(true);
    }

}
