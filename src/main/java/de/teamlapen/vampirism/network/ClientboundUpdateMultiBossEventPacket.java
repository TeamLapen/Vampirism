package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.world.MultiBossEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public record ClientboundUpdateMultiBossEventPacket(UUID uniqueId, OperationType operation, List<Color> colors, Map<Color, Float> entries, Component name, BossEvent.BossBarOverlay overlay) implements IMessage {
    static void encode(@NotNull ClientboundUpdateMultiBossEventPacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeUUID(msg.uniqueId);
        buf.writeEnum(msg.operation);
        switch (msg.operation) {
            case ADD:
                buf.writeComponent(msg.name);
                buf.writeVarInt(msg.colors.size());
                msg.colors.forEach(color -> buf.writeVarInt(color.getRGB()));
                buf.writeEnum(msg.overlay);
            case UPDATE_PROGRESS:
                buf.writeVarInt(msg.entries.size());
                for (Map.Entry<Color, Float> value : msg.entries.entrySet()) {
                    buf.writeVarInt(value.getKey().getRGB());
                    buf.writeFloat(value.getValue());
                }
                break;
            case UPDATE_NAME:
                buf.writeComponent(msg.name);
                break;
            case UPDATE_STYLE:
                buf.writeEnum(msg.overlay);
                break;
        }
    }

    static @NotNull ClientboundUpdateMultiBossEventPacket decode(@NotNull FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        OperationType operation = buf.readEnum(OperationType.class);
        Component name = null;
        List<Color> colors = null;
        Map<Color, Float> entries = null;
        BossEvent.BossBarOverlay overlay = null;
        switch (operation) {
            case ADD:
                name = buf.readComponent();
                int size = buf.readVarInt();
                List<Color> colorsList = new LinkedList<>();
                for (int i = 0; i < size; i++) {
                    colorsList.add(new Color(buf.readVarInt(), true));
                }
                colors = Collections.unmodifiableList(colorsList);
                overlay = buf.readEnum(BossEvent.BossBarOverlay.class);
            case UPDATE_PROGRESS:
                Map<Color, Float> entriesMap = new LinkedHashMap<>();
                int size2 = buf.readVarInt();
                for (int i = 0; i < size2; i++) {
                    Color color = new Color(buf.readVarInt(), true);
                    float perc = buf.readFloat();
                    entriesMap.put(color, perc);
                }
                entries = Collections.unmodifiableMap(entriesMap);
                break;
            case UPDATE_NAME:
                name = buf.readComponent();
                break;
            case UPDATE_STYLE:
                overlay = buf.readEnum(BossEvent.BossBarOverlay.class);
                break;
        }
        return new ClientboundUpdateMultiBossEventPacket(uuid, operation , colors, entries, name, overlay);
    }

    public static void handle(final ClientboundUpdateMultiBossEventPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleUpdateMultiBossInfoPacket(msg));
        ctx.setPacketHandled(true);
    }


    public ClientboundUpdateMultiBossEventPacket(OperationType operation, @NotNull MultiBossEvent data) {
        this(data.getUniqueId(), operation, data.getColors(), data.getEntries(), data.getName() , data.getOverlay());
    }

    public enum OperationType {
        ADD,
        REMOVE,
        UPDATE_PROGRESS,
        UPDATE_NAME,
        UPDATE_STYLE,
        UPDATE_PROPERTIES
    }
}
