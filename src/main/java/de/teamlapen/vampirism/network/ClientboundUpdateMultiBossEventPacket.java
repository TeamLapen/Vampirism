package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.world.MultiBossEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record ClientboundUpdateMultiBossEventPacket(UUID uniqueId, OperationType operation, List<Color> colors, Map<Color, Float> entries, Component name, BossEvent.BossBarOverlay overlay) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(REFERENCE.MODID, "update_multi_boss_event");
    public static final Codec<ClientboundUpdateMultiBossEventPacket> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ClientboundTaskPacket.UUID_CODEC.fieldOf("unique_id").forGetter(ClientboundUpdateMultiBossEventPacket::uniqueId),
                    StringRepresentable.fromEnum(OperationType::values).fieldOf("operation").forGetter(ClientboundUpdateMultiBossEventPacket::operation),
                    ExtraCodecs.strictOptionalField(Color.CODEC.listOf(), "colors", List.of()).forGetter(ClientboundUpdateMultiBossEventPacket::colors),
                    ExtraCodecs.strictOptionalField(Codec.unboundedMap(Color.CODEC,Codec.FLOAT), "entries", Map.of()).forGetter(ClientboundUpdateMultiBossEventPacket::entries),
                    ExtraCodecs.strictOptionalField(ComponentSerialization.CODEC, "name", Component.empty()).forGetter(ClientboundUpdateMultiBossEventPacket::name),
                    ExtraCodecs.strictOptionalField(Codec.STRING.xmap(BossEvent.BossBarOverlay::byName, BossEvent.BossBarOverlay::getName), "overlay", BossEvent.BossBarOverlay.PROGRESS).forGetter(ClientboundUpdateMultiBossEventPacket::overlay)
            ).apply(inst, ClientboundUpdateMultiBossEventPacket::new));

    public ClientboundUpdateMultiBossEventPacket(OperationType operation, @NotNull MultiBossEvent data) {
        this(data.getUniqueId(), operation, data.getColors(), data.getEntries(), data.getName(), data.getOverlay());
    }

    @Override
    public void write(@NotNull FriendlyByteBuf pBuffer) {
        pBuffer.writeJsonWithCodec(CODEC, this);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }

    public enum OperationType implements StringRepresentable {
        ADD("add"),
        REMOVE("remove"),
        UPDATE_PROGRESS("update_progress"),
        UPDATE_NAME("update_name"),
        UPDATE_STYLE("update_style"),
        UPDATE_PROPERTIES("update_properties");

        private final String name;

        OperationType(String name) {
            this.name = name;
        }


        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public static OperationType byName(String name) {
            return Arrays.stream(values()).filter(v -> v.name.equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown operation type: " + name));
        }
    }
}
