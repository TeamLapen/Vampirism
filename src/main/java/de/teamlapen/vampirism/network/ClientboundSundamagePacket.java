package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public record ClientboundSundamagePacket(List<ResourceKey<DimensionType>> dimensions, List<ResourceKey<Biome>> biomes, List<ResourceKey<Level>> noSunDamageLevels, List<ResourceKey<Level>> sunDamageLevels) implements IMessage.IClientBoundMessage {

    public static final Codec<ClientboundSundamagePacket> CODEC = RecordCodecBuilder.create(inst -> {
        return inst.group(
                ResourceKey.codec(Registries.DIMENSION_TYPE).listOf().fieldOf("dimensions").forGetter(ClientboundSundamagePacket::dimensions),
                ResourceKey.codec(Registries.BIOME).listOf().fieldOf("biomes").forGetter(ClientboundSundamagePacket::biomes),
                ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("noSunDamageLevels").forGetter(ClientboundSundamagePacket::noSunDamageLevels),
                ResourceKey.codec(Registries.DIMENSION).listOf().fieldOf("sunDamageLevels").forGetter(ClientboundSundamagePacket::sunDamageLevels)
        ).apply(inst, ClientboundSundamagePacket::new);
    });

    static void encode(@NotNull ClientboundSundamagePacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(CODEC, msg);
    }

    static @NotNull ClientboundSundamagePacket decode(@NotNull FriendlyByteBuf buf) {
        return buf.readJsonWithCodec(CODEC);
    }

    public static void handle(final @NotNull ClientboundSundamagePacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ((SundamageRegistry) VampirismAPI.sundamageRegistry()).applyNetworkData(msg);
        });
        context.setPacketHandled(true);
    }
}
