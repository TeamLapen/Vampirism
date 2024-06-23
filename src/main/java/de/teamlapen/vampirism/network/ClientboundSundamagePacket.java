package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClientboundSundamagePacket(List<ResourceKey<DimensionType>> dimensions, List<ResourceKey<Biome>> biomes, List<ResourceKey<Level>> noSunDamageLevels, List<ResourceKey<Level>> sunDamageLevels) implements CustomPacketPayload {

    public static final Type<ClientboundSundamagePacket> TYPE = new Type<>(VResourceLocation.mod("sundamage"));

    public static final StreamCodec<FriendlyByteBuf, ClientboundSundamagePacket> CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION_TYPE).apply(ByteBufCodecs.list()), ClientboundSundamagePacket::dimensions,
            ResourceKey.streamCodec(Registries.BIOME).apply(ByteBufCodecs.list()), ClientboundSundamagePacket::biomes,
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()), ClientboundSundamagePacket::noSunDamageLevels,
            ResourceKey.streamCodec(Registries.DIMENSION).apply(ByteBufCodecs.list()), ClientboundSundamagePacket::sunDamageLevels,
            ClientboundSundamagePacket::new
    );
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
