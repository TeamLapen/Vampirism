package de.teamlapen.vampirism.network;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

/**
 * Collection of simple input events that do not need any additional information
 */
public record ServerboundSimpleInputEvent(Event event) implements CustomPacketPayload {
    public static final Type<ServerboundSimpleInputEvent> TYPE = new Type<>(VResourceLocation.mod("simple_input"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSimpleInputEvent> CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Event.class), ServerboundSimpleInputEvent::event,
            ServerboundSimpleInputEvent::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Event {
        FINISH_SUCK_BLOOD,
        RESET_SKILLS,
        REVERT_BACK,
        TOGGLE_VAMPIRE_VISION,
        TRAINER_LEVELUP,
        BASIC_HUNTER_LEVELUP,
        SHOW_MINION_CALL_SELECTION,
        VAMPIRISM_MENU,
        RESURRECT,
        GIVE_UP
    }
}
