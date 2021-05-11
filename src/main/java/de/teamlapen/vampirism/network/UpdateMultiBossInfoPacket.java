package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.world.MultiBossInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.*;
import java.util.function.Supplier;

public class UpdateMultiBossInfoPacket implements IMessage {
    private final UUID uniqueId;
    private final SUpdateBossInfoPacket.Operation operation;
    private ITextComponent name;
    protected List<BossInfo.Color> colors;
    protected Map<BossInfo.Color,Float> entries;

    public UpdateMultiBossInfoPacket(SUpdateBossInfoPacket.Operation operation, MultiBossInfo data) {
        this.uniqueId = data.getUniqueId();
        this.operation = operation;
        this.name = data.getName();
        this.colors = data.getColors();
        this.entries = data.getEntries();
    }

    private UpdateMultiBossInfoPacket(SUpdateBossInfoPacket.Operation operation, UUID uuid) {
        this.uniqueId = uuid;
        this.operation = operation;
        this.entries = new LinkedHashMap<>();
    }

    public SUpdateBossInfoPacket.Operation getOperation() {
        return operation;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public ITextComponent getName() {
        return name;
    }

    public Map<BossInfo.Color, Float> getEntries() {
        return entries;
    }

    public List<BossInfo.Color> getColors() {
        return colors;
    }

    static void encode(UpdateMultiBossInfoPacket msg, PacketBuffer buf) {
        buf.writeUniqueId(msg.uniqueId);
        buf.writeEnumValue(msg.operation);
        switch (msg.operation) {
            case ADD:
                buf.writeTextComponent(msg.name);
                buf.writeVarInt(msg.colors.size());
                msg.colors.forEach(buf::writeEnumValue);
            case UPDATE_PCT:
                buf.writeVarInt(msg.entries.size());
                for (Map.Entry<BossInfo.Color, Float> value : msg.entries.entrySet()) {
                    buf.writeEnumValue(value.getKey());
                    buf.writeFloat(value.getValue());
                }
                break;
            case UPDATE_NAME:
                buf.writeTextComponent(msg.name);
                break;
        }
    }

    static UpdateMultiBossInfoPacket decode(PacketBuffer buf) {
        UUID uuid = buf.readUniqueId();
        SUpdateBossInfoPacket.Operation operation = buf.readEnumValue(SUpdateBossInfoPacket.Operation.class);
        UpdateMultiBossInfoPacket packet = new UpdateMultiBossInfoPacket(operation, uuid);
        switch (operation) {
            case ADD:
                packet.name = buf.readTextComponent();
                int size = buf.readVarInt();
                List<BossInfo.Color> colors = new LinkedList<>();
                for (int i = 0; i < size; i++) {
                    colors.add(buf.readEnumValue(BossInfo.Color.class));
                }
                packet.colors = colors;
            case UPDATE_PCT:
                Map<BossInfo.Color,Float> entries = new LinkedHashMap<>();
                int size2 = buf.readVarInt();
                for (int i = 0; i < size2; i++) {
                    BossInfo.Color color = buf.readEnumValue(BossInfo.Color.class);
                    float perc =buf.readFloat();
                    entries.put(color, perc);
                }
                packet.entries = entries;
                break;
            case UPDATE_NAME:
                packet.name = buf.readTextComponent();
                break;
        }
        return packet;
    }

    public static void handle(final UpdateMultiBossInfoPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleUpdateMultiBossInfoPacket(msg));
        ctx.setPacketHandled(true);
    }


}
