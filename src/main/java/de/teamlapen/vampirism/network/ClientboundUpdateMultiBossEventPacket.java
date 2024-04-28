package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.util.ByteBufferCodecUtil;
import de.teamlapen.vampirism.world.MultiBossEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.BossEvent;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Stream;

public record ClientboundUpdateMultiBossEventPacket(Operation operation) implements CustomPacketPayload {

    public static final Type<ClientboundUpdateMultiBossEventPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "update_multi_boss_event"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateMultiBossEventPacket> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ClientboundUpdateMultiBossEventPacket decode(RegistryFriendlyByteBuf p_320376_) {
            OperationType operationType = p_320376_.readEnum(OperationType.class);
            Operation operation = operationType.streamCodec.decode(p_320376_);
            return new ClientboundUpdateMultiBossEventPacket(operation);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ClientboundUpdateMultiBossEventPacket packet) {
            buffer.writeEnum(packet.operation.getType());
            packet.operation.getType().codec().encode(buffer, packet.operation);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public interface Operation {
        OperationType getType();

        UUID uniqueId();
    }

    public record AddOperation(UUID uniqueId, List<Color> colors, Map<Color, Float> entries, Component name, BossEvent.BossBarOverlay overlay) implements Operation {

        public static final StreamCodec<RegistryFriendlyByteBuf, AddOperation> CODEC = StreamCodec.composite(
                ByteBufferCodecUtil.UUID, AddOperation::uniqueId,
                Color.STREAM_CODEC.apply(ByteBufCodecs.list()), AddOperation::colors,
                ByteBufCodecs.map(s -> new HashMap<>(), Color.STREAM_CODEC, ByteBufCodecs.FLOAT), AddOperation::entries,
                ComponentSerialization.STREAM_CODEC, AddOperation::name,
                NeoForgeStreamCodecs.enumCodec(BossEvent.BossBarOverlay.class), AddOperation::overlay,
                AddOperation::new
        );

        public AddOperation(MultiBossEvent event) {
            this(event.getUniqueId(), event.getColors(), event.getEntries(), event.getName(), event.getOverlay());
        }

        @Override
        public OperationType getType() {
            return OperationType.ADD;
        }

    }

    public record RemoveOperation(UUID uniqueId) implements Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, RemoveOperation> CODEC = StreamCodec.composite(
                ByteBufferCodecUtil.UUID, RemoveOperation::uniqueId,
                RemoveOperation::new
        );

        public RemoveOperation(MultiBossEvent event) {
            this(event.getUniqueId());
        }

        @Override
        public OperationType getType() {
            return OperationType.REMOVE;
        }

    }

    public record UpdateProgressOperation(UUID uniqueId, Map<Color, Float> entries) implements Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, UpdateProgressOperation> CODEC = StreamCodec.composite(
                ByteBufferCodecUtil.UUID, UpdateProgressOperation::uniqueId,
                ByteBufCodecs.map(s -> new HashMap<>(),Color.STREAM_CODEC,ByteBufCodecs.FLOAT), UpdateProgressOperation::entries,
                UpdateProgressOperation::new
        );

        public UpdateProgressOperation(MultiBossEvent event) {
            this(event.getUniqueId(), event.getEntries());
        }

        @Override
        public OperationType getType() {
            return OperationType.UPDATE_PROGRESS;
        }
    }

    public record UpdateNameOperation(UUID uniqueId, Component name) implements Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, UpdateNameOperation> CODEC = StreamCodec.composite(
                ByteBufferCodecUtil.UUID, UpdateNameOperation::uniqueId,
                ComponentSerialization.STREAM_CODEC, UpdateNameOperation::name,
                UpdateNameOperation::new
        );

        public UpdateNameOperation(MultiBossEvent event) {
            this(event.getUniqueId(), event.getName());
        }

        @Override
        public OperationType getType() {
            return OperationType.UPDATE_NAME;
        }
    }

    public record UpdateStyle(UUID uniqueId, BossEvent.BossBarOverlay overlay) implements Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, UpdateStyle> CODEC = StreamCodec.composite(
                ByteBufferCodecUtil.UUID, UpdateStyle::uniqueId,
                NeoForgeStreamCodecs.enumCodec(BossEvent.BossBarOverlay.class), UpdateStyle::overlay,
                UpdateStyle::new
        );

        public UpdateStyle(MultiBossEvent event) {
            this(event.getUniqueId(), event.getOverlay());
        }

        @Override
        public OperationType getType() {
            return OperationType.UPDATE_STYLE;
        }
    }


    public enum OperationType {
        ADD(AddOperation.CODEC),
        REMOVE(RemoveOperation.CODEC),
        UPDATE_PROGRESS(UpdateProgressOperation.CODEC),
        UPDATE_NAME(UpdateNameOperation.CODEC),
        UPDATE_STYLE(UpdateStyle.CODEC);


        private final StreamCodec<RegistryFriendlyByteBuf, ? extends Operation> streamCodec;

        OperationType(StreamCodec<RegistryFriendlyByteBuf, ? extends Operation> streamCodec) {
            this.streamCodec = streamCodec;
        }

        public StreamCodec<RegistryFriendlyByteBuf, Operation> codec() {
            return (StreamCodec<RegistryFriendlyByteBuf, Operation>) streamCodec;
        }
    }
}
