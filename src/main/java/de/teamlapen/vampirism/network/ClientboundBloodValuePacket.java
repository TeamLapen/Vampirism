package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record ClientboundBloodValuePacket(Map<ResourceLocation, Float>[] values, Map<EntityType<?>, ResourceLocation> convertibleOverlay) implements IMessage.IClientBoundMessage {

    public static final Codec<ClientboundBloodValuePacket> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.FLOAT).listOf().fieldOf("values").forGetter(l -> List.of(l.values)),
                Codec.unboundedMap(ForgeRegistries.ENTITY_TYPES.getCodec(), ResourceLocation.CODEC).fieldOf("convertible_overlay").forGetter(l -> l.convertibleOverlay)
        ).apply(builder, (values1, overlay) -> new ClientboundBloodValuePacket(values1.toArray((Map<ResourceLocation, Float>[]) new Map[0]), overlay));
    });

    static void encode(@NotNull ClientboundBloodValuePacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(CODEC, msg);
    }

    static @NotNull ClientboundBloodValuePacket decode(@NotNull FriendlyByteBuf buf) {
        return buf.readJsonWithCodec(CODEC);
    }

    public static void handle(final ClientboundBloodValuePacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> VampirismMod.proxy.handleBloodValuePacket(msg));
        ctx.setPacketHandled(true);
    }

    public Map<ResourceLocation, Float>[] getValues() {
        return values;
    }
}
