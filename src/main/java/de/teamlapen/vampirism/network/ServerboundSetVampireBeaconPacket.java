package de.teamlapen.vampirism.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.network.IMessage;
import de.teamlapen.vampirism.inventory.VampireBeaconMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public record ServerboundSetVampireBeaconPacket(Optional<MobEffect> effect, Optional<Integer> amplifier) implements IMessage.IServerBoundMessage {

    public static final Codec<ServerboundSetVampireBeaconPacket> CODEC = RecordCodecBuilder.create(builder -> {
        return builder.group(
                ForgeRegistries.MOB_EFFECTS.getCodec().optionalFieldOf("effect").forGetter(l -> l.effect),
                Codec.INT.optionalFieldOf("amplifier").forGetter(l -> l.amplifier)
        ).apply(builder, ServerboundSetVampireBeaconPacket::new);
    });

    static void encode(@NotNull ServerboundSetVampireBeaconPacket msg, @NotNull FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(CODEC, msg);
    }

    static @NotNull ServerboundSetVampireBeaconPacket decode(@NotNull FriendlyByteBuf buf) {
        return buf.readJsonWithCodec(CODEC);
    }

    public static void handle(final ServerboundSetVampireBeaconPacket msg, @NotNull Supplier<NetworkEvent.Context> contextSupplier) {
        final NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender.containerMenu instanceof VampireBeaconMenu beaconMenu && beaconMenu.stillValid(sender)) {
                beaconMenu.updateEffects(msg.effect, msg.amplifier);
            }
        });
        ctx.setPacketHandled(true);
    }
}
