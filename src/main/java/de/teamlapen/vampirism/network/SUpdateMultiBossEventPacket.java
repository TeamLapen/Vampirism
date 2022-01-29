package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.world.MultiBossEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class SUpdateMultiBossEventPacket implements IMessage {
    static void encode(SUpdateMultiBossEventPacket msg, FriendlyByteBuf buf) {
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

    static SUpdateMultiBossEventPacket decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        OperationType operation = buf.readEnum(OperationType.class);
        SUpdateMultiBossEventPacket packet = new SUpdateMultiBossEventPacket(operation, uuid);
        switch (operation) {
            case ADD:
                packet.name = buf.readComponent();
                int size = buf.readVarInt();
                List<Color> colors = new LinkedList<>();
                for (int i = 0; i < size; i++) {
                    colors.add(new Color(buf.readVarInt(), true));
                }
                packet.colors = colors;
                packet.overlay = buf.readEnum(BossEvent.BossBarOverlay.class);
            case UPDATE_PROGRESS:
                Map<Color, Float> entries = new LinkedHashMap<>();
                int size2 = buf.readVarInt();
                for (int i = 0; i < size2; i++) {
                    Color color = new Color(buf.readVarInt(), true);
                    float perc = buf.readFloat();
                    entries.put(color, perc);
                }
                packet.entries = entries;
                break;
            case UPDATE_NAME:
                packet.name = buf.readComponent();
                break;
            case UPDATE_STYLE:
                packet.overlay = buf.readEnum(BossEvent.BossBarOverlay.class);
                break;
        }
        return packet;
    }

    public static void handle(final SUpdateMultiBossEventPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleUpdateMultiBossInfoPacket(msg));
        ctx.setPacketHandled(true);
    }

    private final UUID uniqueId;
    private final OperationType operation;
    protected List<Color> colors;
    protected Map<Color, Float> entries;
    private Component name;
    private BossEvent.BossBarOverlay overlay;

    public SUpdateMultiBossEventPacket(OperationType operation, MultiBossEvent data) {
        this.uniqueId = data.getUniqueId();
        this.operation = operation;
        this.name = data.getName();
        this.colors = data.getColors();
        this.entries = data.getEntries();
        this.overlay = data.getOverlay();
    }

    private SUpdateMultiBossEventPacket(OperationType operation, UUID uuid) {
        this.uniqueId = uuid;
        this.operation = operation;
        this.entries = new LinkedHashMap<>();
    }

    public List<Color> getColors() {
        return colors;
    }

    public Map<Color, Float> getEntries() {
        return entries;
    }

    public Component getName() {
        return name;
    }

    public OperationType getOperation() {
        return operation;
    }

    public BossEvent.BossBarOverlay getOverlay() {
        return overlay;
    }

    public UUID getUniqueId() {
        return uniqueId;
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
