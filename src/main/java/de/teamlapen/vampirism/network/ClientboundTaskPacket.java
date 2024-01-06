package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.entity.player.TaskManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ClientboundTaskPacket(int containerId,
                                    Map<UUID, TaskManager.TaskWrapper> taskWrappers,
                                    Map<UUID, Set<UUID>> completableTasks,
                                    Map<UUID, Map<UUID, Map<ResourceLocation, Integer>>> completedRequirements) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "task");
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.comapFlatMap(s -> {
        try {
            return DataResult.success(UUID.fromString(s));
        } catch (Exception e){
            return DataResult.error(() -> "Invalid UUID: " + s);
        }
    }, UUID::toString).stable();
    private static final Codec<Set<UUID>> SET_CODEC = UUID_CODEC.listOf().comapFlatMap(s -> {
        try {
            return DataResult.success((Set<UUID>)new HashSet<>(s));
        } catch (Exception e) {
            return DataResult.error(() -> "Invalid List");
        }
    }, ArrayList::new).stable();
    public static final Codec<ClientboundTaskPacket> CODEC = RecordCodecBuilder.create(func -> {
        return func.group(
                Codec.INT.fieldOf("containerId").forGetter(ClientboundTaskPacket::containerId),
                Codec.unboundedMap(UUID_CODEC, TaskManager.TaskWrapper.CODEC).fieldOf("taskWrappers").forGetter(ClientboundTaskPacket::taskWrappers),
                Codec.unboundedMap(UUID_CODEC, SET_CODEC).fieldOf("completableTasks").forGetter(ClientboundTaskPacket::completableTasks),
                Codec.unboundedMap(UUID_CODEC, Codec.unboundedMap(UUID_CODEC, Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT))).fieldOf("completedRequirements").forGetter(ClientboundTaskPacket::completedRequirements)
        ).apply(func, ClientboundTaskPacket::new);
    });

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
