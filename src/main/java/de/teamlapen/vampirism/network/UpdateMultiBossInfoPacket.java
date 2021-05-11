package de.teamlapen.vampirism.network;

import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.world.MultiBossInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class UpdateMultiBossInfoPacket implements IMessage {
    protected Map<ResourceLocation, MultiBossInfo.Entry> entries;
    private UUID uniqueId;
    private SUpdateBossInfoPacket.Operation operation;
    private ITextComponent name;

    public UpdateMultiBossInfoPacket(SUpdateBossInfoPacket.Operation operation, MultiBossInfo data) {
        this.uniqueId = data.getUniqueId();
        this.operation = operation;
        this.name = data.getName();
        this.entries = data.getEntries();
    }

    private UpdateMultiBossInfoPacket(SUpdateBossInfoPacket.Operation operation, UUID uuid) {
        this.uniqueId = uuid;
        this.operation = operation;
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

    public Map<ResourceLocation, MultiBossInfo.Entry> getEntries() {
        return entries;
    }

    static void encode(UpdateMultiBossInfoPacket msg, PacketBuffer buf) {
        buf.writeUniqueId(msg.uniqueId);
        buf.writeEnumValue(msg.operation);
        switch (msg.operation) {
            case ADD:
                buf.writeTextComponent(msg.name);
            case UPDATE_PCT:
                buf.writeVarInt(msg.entries.size());
                for (MultiBossInfo.Entry value : msg.entries.values()) {
                    buf.writeResourceLocation(value.getId());
                    buf.writeEnumValue(value.getColor());
                    buf.writeFloat(value.getPercentage());
                    buf.writeVarInt(value.getOrdinal());
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
        packet.entries = new LinkedHashMap<>();
        switch (operation) {
            case ADD:
                packet.name = buf.readTextComponent();
            case UPDATE_PCT:
                Map<ResourceLocation, MultiBossInfo.Entry> entries = new LinkedHashMap<>();
                int size = buf.readVarInt();
                for (int i = 0; i < size; i++) {
                    ResourceLocation id = buf.readResourceLocation();
                    BossInfo.Color color = buf.readEnumValue(BossInfo.Color.class);
                    float perc =buf.readFloat();
                    int ordinal = buf.readVarInt();
                    entries.put(id, new MultiBossInfo.Entry(id, color, perc,ordinal ));
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
