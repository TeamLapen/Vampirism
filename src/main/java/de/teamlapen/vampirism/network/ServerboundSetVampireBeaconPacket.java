package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record ServerboundSetVampireBeaconPacket(Optional<Holder<MobEffect>> effect, Optional<Integer> amplifier) implements CustomPacketPayload {

    public static final Type<ServerboundSetVampireBeaconPacket> TYPE = new Type<>(new ResourceLocation(REFERENCE.MODID, "set_vampire_beacon"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetVampireBeaconPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT)), ServerboundSetVampireBeaconPacket::effect,
            ByteBufCodecs.optional(ByteBufCodecs.INT), ServerboundSetVampireBeaconPacket::amplifier,
            ServerboundSetVampireBeaconPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
