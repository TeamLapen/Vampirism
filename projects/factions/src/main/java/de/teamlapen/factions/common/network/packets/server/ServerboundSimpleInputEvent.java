package de.teamlapen.factions.common.network.packets.server;

import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

public record ServerboundSimpleInputEvent(Event event) implements CustomPacketPayload {
    public static final Type<ServerboundSimpleInputEvent> TYPE = new Type<>(FResourceLocation.mod("simple_input"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSimpleInputEvent> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Event.class), ServerboundSimpleInputEvent::event,
            ServerboundSimpleInputEvent::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Event {
        RESET_SKILLS,
        SHOW_MINION_CALL_SELECTION,
        VAMPIRISM_MENU,
    }
}
