package de.teamlapen.vampirism.common.network.packets.client;

import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.entity.dracula.DraculaEvent;
import de.teamlapen.vampirism.common.world.entity.dracula.FightStage;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record ClientboundDraculaEventPacket(Operation operation) implements CustomPacketPayload {

    public static final Type<ClientboundDraculaEventPacket> TYPE = new Type<>(VIdentifier.mod("update_dracula_event"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDraculaEventPacket> CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ClientboundDraculaEventPacket decode(RegistryFriendlyByteBuf p_320376_) {
            OperationType operationType = p_320376_.readEnum(OperationType.class);
            Operation operation = operationType.streamCodec.decode(p_320376_);
            return new ClientboundDraculaEventPacket(operation);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ClientboundDraculaEventPacket packet) {
            buffer.writeEnum(packet.operation.getType());
            packet.operation.getType().codec().encode(buffer, packet.operation);
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public interface Operation {
        OperationType getType();

        UUID id();
    }

    public record AddOperation(UUID id, float percentage, FightStage stage, boolean isVulnerable) implements Operation {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDraculaEventPacket.AddOperation> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ClientboundDraculaEventPacket.AddOperation::id,
                ByteBufCodecs.FLOAT, ClientboundDraculaEventPacket.AddOperation::percentage,
                FightStage.STREAM_CODEC, ClientboundDraculaEventPacket.AddOperation::stage,
                ByteBufCodecs.BOOL, ClientboundDraculaEventPacket.AddOperation::isVulnerable,
                ClientboundDraculaEventPacket.AddOperation::new
        );

        public AddOperation(DraculaEvent event) {
            this(event.id(), event.getPercentage(), event.getStage(), event.isInVulnerable());
        }

        @Override
        public ClientboundDraculaEventPacket.OperationType getType() {
            return ClientboundDraculaEventPacket.OperationType.ADD;
        }

    }

    public record RemoveOperation(UUID id) implements ClientboundDraculaEventPacket.Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDraculaEventPacket.RemoveOperation> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ClientboundDraculaEventPacket.RemoveOperation::id,
                ClientboundDraculaEventPacket.RemoveOperation::new
        );

        public RemoveOperation(DraculaEvent event) {
            this(event.id());
        }

        @Override
        public ClientboundDraculaEventPacket.OperationType getType() {
            return ClientboundDraculaEventPacket.OperationType.REMOVE;
        }

    }

    public record UpdateProgressOperation(UUID id, float percentage) implements ClientboundDraculaEventPacket.Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDraculaEventPacket.UpdateProgressOperation> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ClientboundDraculaEventPacket.UpdateProgressOperation::id,
                ByteBufCodecs.FLOAT, ClientboundDraculaEventPacket.UpdateProgressOperation::percentage,
                ClientboundDraculaEventPacket.UpdateProgressOperation::new
        );

        public UpdateProgressOperation(DraculaEvent event) {
            this(event.id(), event.getPercentage());
        }

        @Override
        public ClientboundDraculaEventPacket.OperationType getType() {
            return ClientboundDraculaEventPacket.OperationType.UPDATE_PROGRESS;
        }
    }

    public record UpdateVulnerableOperation(UUID id, boolean vulnerable) implements ClientboundDraculaEventPacket.Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDraculaEventPacket.UpdateVulnerableOperation> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ClientboundDraculaEventPacket.UpdateVulnerableOperation::id,
                ByteBufCodecs.BOOL, ClientboundDraculaEventPacket.UpdateVulnerableOperation::vulnerable,
                ClientboundDraculaEventPacket.UpdateVulnerableOperation::new
        );

        public UpdateVulnerableOperation(DraculaEvent event) {
            this(event.id(), event.isInVulnerable());
        }

        @Override
        public ClientboundDraculaEventPacket.OperationType getType() {
            return OperationType.UPDATE_INVULNERABLE;
        }
    }

    public record UpdateOperation(UUID id, float percentage, FightStage stage, boolean vulnerable) implements ClientboundDraculaEventPacket.Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDraculaEventPacket.UpdateOperation> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ClientboundDraculaEventPacket.UpdateOperation::id,
                ByteBufCodecs.FLOAT, ClientboundDraculaEventPacket.UpdateOperation::percentage,
                FightStage.STREAM_CODEC, ClientboundDraculaEventPacket.UpdateOperation::stage,
                ByteBufCodecs.BOOL, ClientboundDraculaEventPacket.UpdateOperation::vulnerable,
                ClientboundDraculaEventPacket.UpdateOperation::new
        );

        public UpdateOperation(DraculaEvent event) {
            this(event.id(), event.getPercentage(), event.getStage(), event.isInVulnerable());
        }

        @Override
        public ClientboundDraculaEventPacket.OperationType getType() {
            return OperationType.UPDATE;
        }
    }

    public record UpdateStageOperation(UUID id, FightStage stage) implements ClientboundDraculaEventPacket.Operation {
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDraculaEventPacket.UpdateStageOperation> CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC, ClientboundDraculaEventPacket.UpdateStageOperation::id,
                FightStage.STREAM_CODEC, ClientboundDraculaEventPacket.UpdateStageOperation::stage,
                ClientboundDraculaEventPacket.UpdateStageOperation::new
        );

        public UpdateStageOperation(DraculaEvent event) {
            this(event.id(), event.getStage());
        }

        @Override
        public ClientboundDraculaEventPacket.OperationType getType() {
            return ClientboundDraculaEventPacket.OperationType.UPDATE_STAGE;
        }
    }


    public enum OperationType {
        ADD(ClientboundDraculaEventPacket.AddOperation.CODEC),
        REMOVE(ClientboundDraculaEventPacket.RemoveOperation.CODEC),
        UPDATE_PROGRESS(ClientboundDraculaEventPacket.UpdateProgressOperation.CODEC),
        UPDATE_INVULNERABLE(ClientboundDraculaEventPacket.UpdateVulnerableOperation.CODEC),
        UPDATE_STAGE(ClientboundDraculaEventPacket.UpdateStageOperation.CODEC),
        UPDATE(ClientboundDraculaEventPacket.UpdateOperation.CODEC)
        ;


        private final StreamCodec<RegistryFriendlyByteBuf, ? extends ClientboundDraculaEventPacket.Operation> streamCodec;

        OperationType(StreamCodec<RegistryFriendlyByteBuf, ? extends ClientboundDraculaEventPacket.Operation> streamCodec) {
            this.streamCodec = streamCodec;
        }

        public StreamCodec<RegistryFriendlyByteBuf, ClientboundDraculaEventPacket.Operation> codec() {
            return SafeCast.cast(this.streamCodec);
        }
    }
}
